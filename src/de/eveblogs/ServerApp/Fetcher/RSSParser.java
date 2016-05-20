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

import de.eveblogs.ServerApp.EveBlogs;
import de.eveblogs.ServerApp.Utilities.Blog;
import de.eveblogs.ServerApp.Utilities.Blogpost;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
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

    private final LinkedList<Blog> blogList;
    private final LinkedList<String> dateFormatPattern;
    private final SimpleDateFormat dateFormat;

    /**
     * Creates a new RSSParser.
     *
     * @param blogList a list of blogs whose feeds need to be parsed.
     */
    public RSSParser(LinkedList<Blog> blogList) {
        this.blogList = blogList;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);


        /*
         * TODO test if there are cases that dont't use one of these formats. TODO find a more elegant solution
         */
        this.dateFormatPattern = new LinkedList<>(Arrays.asList(EveBlogs.getDefaultConfig().getProperty("dateFormats").split("\", *")));
        ListIterator<String> it = dateFormatPattern.listIterator();
        while (it.hasNext()) {
            it.set(it.next().replace("\"", ""));
        }
    }

    /**
     * Parses all blogs in the list given to the constructor and returns the blogposts in an ArrayList.
     *
     * @return an ArrayList with at least all blogbosts published later than the latest blogpost of each blog added to the database. May contain more.
     */
    public LinkedList<Blogpost> getBlogpostList() {
        final LinkedList<Blogpost> blogpostList = new LinkedList<>();
        for (Blog blog : blogList) {
            blogpostList.addAll(parseBlogposts(blog));
            blog.setLastUpdate();
            blog.writeToDatabase();
        }
        return blogpostList;
    }

    private LinkedList<Blogpost> parseBlogposts(Blog blog) {
        final LinkedList<Blogpost> blogpostList = new LinkedList<>();
        XMLEventReader reader = RSSFeedFetcher.getRSSFeed(blog);
        if (reader != null) {
            try {
                boolean itemFlag = false; // flag that is set when the parser is inside a item tag.
                String name = null;
                String link = null;
                String description = null;
                Date pubDate = null;

                loop: // labels the loop to jump out of it when no new blogposts are there to be parsed.
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();
                    switch (event.getEventType()) {
                        case XMLEvent.START_ELEMENT:
                            StartElement startElement = event.asStartElement();

                            /*
                             * TODO try to bring this in a bit nicer form.
                             *
                             * TODO implement the parser for atom feeds. (element instead of item)
                             */
                            String elementContent = startElement.getName().toString();
                            if (elementContent.equals("item")) {
                                itemFlag = true;
                            } else if (elementContent.equals(blog.getElementName("blogpostName"))) {
                                if (itemFlag) {
                                    StringBuilder builder = new StringBuilder(255);
                                    while (!reader.peek().isEndElement()) {
                                        builder.append(reader.nextEvent().asCharacters().getData());
                                    }
                                    name = builder.toString();
                                }
                            } else if (elementContent.equals(blog.getElementName("blogpostLink"))) {
                                if (itemFlag) {
                                    link = reader.nextEvent().asCharacters().getData();
                                }
                            } else if (elementContent.equals(blog.getElementName("description"))) {
                                if (itemFlag) {
                                    StringBuilder builder = new StringBuilder(255);
                                    while (!reader.peek().isEndElement()) {
                                        builder.append(reader.nextEvent().asCharacters().getData());
                                    }
//                                    description = reader.nextEvent().asCharacters().getData();
                                    description = builder.toString();
                                }
                            } else if (elementContent.equals(blog.getElementName("publicationDateTime"))) {
                                /*
                                 * tries to parse the date with all formatters in this.formatterList. if all fail the current time is set and a warning logged.
                                 */
                                if (itemFlag) {

                                    for (String dateFormatString : dateFormatPattern) {
                                        dateFormat.applyPattern(dateFormatString);
                                        try {
                                            pubDate = dateFormat.parse(reader.peek().asCharacters().toString());
                                        } catch (ParseException ex) {
                                            continue;  // cycle through
                                        }
                                        break; // if parsing was successful break out
                                    }

                                    /*
                                     * as soon as a date is parsed that is before the last update of the blog the rest of the file is skipped.
                                     */
                                    if ((pubDate != null) && (blog.getLastUpdate() != null) && pubDate.before(blog.getLastUpdate())) {
                                        blog.setLastUpdate(new Date());
                                        break loop;
                                    }
                                    if (pubDate == null) {
                                        pubDate = new Date();
                                        Logger.getLogger(RSSParser.class.getName()).log(Level.WARNING, "Blog {0} uses unsupported DateTime format", blog);
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
                                     * creates a new Blogpost if there is a link and either a name or a descripion. These Elements must be present for it to be
                                     * a validate RSS document according to the RSS specifications 2.0
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
