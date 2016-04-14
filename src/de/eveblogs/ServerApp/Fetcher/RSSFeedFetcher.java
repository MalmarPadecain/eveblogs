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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * This class provides some static methods to get the feeds.
 *
 * @author Malmar Padecain
 */
public class RSSFeedFetcher {

    /**
     *
     * @param blog the blog to get the feed from.
     * @return a reader on the given URL or null in case the blog had no feed location specefied or error.
     */
    public static XMLEventReader getRSSFeed(Blog blog) {
        if (blog.getFeedURL() != null) {
            return getRSSFeed(blog.getFeedURL());
        } else {
            return null;
        }
    }

    /**
     *
     * @param url a string representing the URL to the feed.
     * @return a reader on to the given URL or null in case of error.
     * @throws MalformedURLException if the given string cannot be parsed to a URL.
     */
    public static XMLEventReader getRSSFeed(String url) throws MalformedURLException {
        return getRSSFeed(new URL(url));
    }

    /**
     *
     * @param url the URL to the feed
     * @return a reader on the given URL or null in case of error.
     */
    public static XMLEventReader getRSSFeed(URL url) {
        try {
            return XMLInputFactory.newInstance().createXMLEventReader(url.openConnection().getInputStream());
        } catch (XMLStreamException | IOException ex) {
            Logger.getLogger(RSSFeedFetcher.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
