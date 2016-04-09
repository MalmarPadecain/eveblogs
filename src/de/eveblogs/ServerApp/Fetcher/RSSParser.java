/*
 * Copyright (C) 2016 Malmar Padecain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.eveblogs.ServerApp.Fetcher;

import de.eveblogs.ServerApp.Utilities.Blog;
import de.eveblogs.ServerApp.Utilities.Blogpost;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Class to create Blogposts from a RSS feed.
 *
 * @author Malmar Padecain
 */
public class RSSParser {

    private final ArrayList<Blog> blogList;
    private final LinkedList<String> dateFormatPattern;
    private final SimpleDateFormat dateFormat;

    /**
     * Creates a new RSSParser.
     *
     * @param blogList a list of blogs whose feeds need to be parsed.
     */
    public RSSParser(ArrayList<Blog> blogList) {
        this.blogList = blogList;
        this.dateFormat = new SimpleDateFormat();

        /*
        * TODO add this to the propperties file.
         */
        this.dateFormatPattern = new LinkedList<>();

        /*
        * TODO test if there are cases that dont't use one of these formats.
         */
        this.dateFormatPattern.add("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        this.dateFormatPattern.add("EEE, dd MMM yyyy kk:mm:ss XXXX");
        /*
        * Commentetd out to rewrite to Java 7 compatibility.
        this.formatterList.add(DateTimeFormatter.RFC_1123_DATE_TIME); //The format that is used in most feeds using simple RSS
        this.formatterList.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")); // The fomat that is used in most Atom feeds
         */
    }

    /**
     * Parses all blogs in the list given to the constructor and returns the
     * blogposts in an ArrayList.
     *
     * @return an ArrayList with at least all blogbosts published later than the
     * latest blogpost of each blog added to the database. May contain more.
     */
    public ArrayList<Blogpost> getBlogpostList() {
        final ArrayList<Blogpost> blogpostList = new ArrayList<>(5);
        for (Blog blog : blogList) {
            blogpostList.addAll(parseBlogposts(blog));
        }
        return blogpostList;
    }

    private ArrayList<Blogpost> parseBlogposts(Blog blog) {
        final ArrayList<Blogpost> blogpostList = new ArrayList<>(2);
        XMLEventReader reader = RSSFeedFetcher.getRSSFeed(blog);
        if (reader != null) {
            try {
                boolean itemFlag = false; // flag that is set when the parser is inside a item tag.
                String name = null;
                String link = null;
                String description = null;
                Date pubDate = null;
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();

                    switch (event.getEventType()) {
                        case XMLEvent.START_ELEMENT:
                            StartElement startElement = event.asStartElement();

                            /*
                            * TODO in case of an escape sequence in a string the parser stops parsing and jumps to the next entry. Solve this probably with a loop in each case.
                            * TODO try to bring this in a bit a more beautiful form.
                             */
                            String elementContent = startElement.getName().toString();
                            if (elementContent.equals("item")) {
                                itemFlag = true;
                            } else if (elementContent.equals(blog.getElementName("xmlBlogpostName"))) {
                                if (itemFlag) {
                                    name = reader.nextEvent().asCharacters().getData();
                                }
                            } else if (elementContent.equals(blog.getElementName("xmlBlogpostLink"))) {
                                if (itemFlag) {
                                    link = reader.nextEvent().asCharacters().getData();
                                }
                            } else if (elementContent.equals(blog.getElementName("xmlDescription"))) {
                                if (itemFlag) {
                                    description = reader.nextEvent().asCharacters().getData();
                                }
                            } else if (elementContent.equals(blog.getElementName("xmlPublicationDateTime"))) {
                                /*
                                * tries to parse the date with all formatters in this.formatterList. 
                                * if all fail the current time is set and  a warning logged.
                                 */
                                if (itemFlag) {
                                    try {
                                        dateFormat.applyPattern(dateFormatPattern.get(0));
                                        pubDate = dateFormat.parse(reader.nextEvent().asCharacters().toString());
                                    } catch (ParseException ex) {
                                        try {
                                            dateFormat.applyPattern(dateFormatPattern.get(1));
                                            pubDate = dateFormat.parse(reader.nextEvent().asCharacters().toString());
                                        } catch (ParseException ex1) {
                                            Logger.getLogger(RSSParser.class.getName()).log(Level.WARNING, "Blog " + blog + " uses unsupported DateTime format", ex);
                                            pubDate = new Date();
                                        }
                                    }
                                }
                            }

                            break;
                        case XMLEvent.END_ELEMENT:
                            EndElement endElement = event.asEndElement();
                            switch (endElement.getName().toString()) {
                                case "item":
                                    itemFlag = false;
                                    /*
                                    * creates a new Blogpost if there is a link and either a name or a descripion. These Elements must be present for it to be a validate RSS document according to the RSS specifications 2.0
                                     */
                                    if (link != null && (name != null || description != null)) {
                                        try {
                                            blogpostList.add(new Blogpost(link, name, description, pubDate, blog));
                                        } catch (MalformedURLException ex) {
                                            Logger.getLogger(RSSParser.class.getName()).log(Level.WARNING, null, ex);
                                        }
                                    }

                                    /*
                                    * sets all variables back to NULL
                                     */
                                    name = null;
                                    link = null;
                                    description = null;
                                    pubDate = null;
                                    break;
                            }
                    }
                }
            } catch (XMLStreamException ex) {
                Logger.getLogger(RSSParser.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return blogpostList;
    }
}
