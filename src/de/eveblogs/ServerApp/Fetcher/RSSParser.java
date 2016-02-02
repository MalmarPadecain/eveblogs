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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Malmar Padecain
 */
public class RSSParser {

    private final ArrayList<Blog> blogList;

    /**
     * Creates a new RSSParser.
     *
     * @param blogList a list of blogs whose feeds need to be parsed.
     */
    public RSSParser(ArrayList<Blog> blogList) {
        this.blogList = blogList;
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
        blogList.forEach(blog -> {
            blogpostList.addAll(parse(blog));
        });
        return blogpostList;
    }

    private ArrayList<Blogpost> parse(Blog blog) {
        final ArrayList<Blogpost> blogpostList = new ArrayList<>(2);
        XMLEventReader reader = RSSFeedFetcher.getRSSFeed(blog);
        if (reader != null) {
            try {
                boolean itemFlag = false; // flag that is set when the parser is inside a item tag.
                String name = null;
                String link = null;
                String description = null;
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();

                    switch (event.getEventType()) {
                        case XMLEvent.START_ELEMENT:
                            StartElement startElement = event.asStartElement();
                            switch (startElement.getName().toString()) {
                                case "item":
                                    itemFlag = true;
                                    break;
                                case "title":
                                    if (itemFlag) {
                                        name = reader.nextEvent().asCharacters().getData();
                                    }
                                    break;
                                case "description": 
                                    if (itemFlag) {
                                        description = reader.nextEvent().asCharacters().getData();
                                    }
                                    break;
                                case "link":
                                    if (itemFlag) {
                                        link = reader.nextEvent().asCharacters().getData();
                                    }
                                    break;
                            }
                            break;
                        case XMLEvent.END_ELEMENT:
                            EndElement endElement = event.asEndElement();
                            switch (endElement.getName().toString()) {
                                case "item":
                                    itemFlag = false;
                                    if (link != null && (name != null || description != null)) {
                                        try {
                                            blogpostList.add(new Blogpost(link, name, description));
                                        } catch (MalformedURLException ex) {
                                            Logger.getLogger(RSSParser.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                    name = null;
                                    link = null;
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
