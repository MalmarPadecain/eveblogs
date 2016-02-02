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
package de.eveblogs.ServerApp.Utilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

/**
 * This class represents a blog. It contains methods to create a blog from the
 * database, write a blog to the database and manipualating a blog in the
 * database.
 *
 * @author Malmar Padecain
 */
public class Blog extends DatabaseObject {

    private URL blogURL;
    private URL feedURL;
    private String name;
    private String author;
    private LocalDateTime lastUpdate;

    /**
     * Creates a new representation of a blog from the data base. StatusFlag
     * will be ORIGINAL.
     *
     * @param primaryKey the primary key of the blog in the data base.
     */
    public Blog(int primaryKey) {
        super(primaryKey);
    }

    /**
     * Creates a new Blog. The statusFlag will be NEW and the primary key null.
     *
     * @param name the name of the blog.
     * @param author the author of the blog. Preferably an email address.
     * @param blogURL string representation of the URL of the blog.
     * @param feedURL string representation of the URL of the RSS feed.
     * @throws MalformedURLException if the given strings could not be parsed to
     * URLs.
     */
    public Blog(String name, String author, String blogURL, String feedURL) throws MalformedURLException {
        super();
        this.name = name;
        this.author = author;
        this.blogURL = new URL(blogURL);
        this.feedURL = new URL(feedURL);
    }

    /**
     *
     * @return the date and time, when feed of the blog was last fetched and
     * writen to the database.
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the last update time to the current time and date.
     */
    public void setLastUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }

    /**
     *
     * @param lastUpdate the time and date when the Blog was last updated in the
     * database.
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     *
     * @return the URL of the blog.
     */
    public URL getBlogURL() {
        return blogURL;
    }

    /**
     *
     * @param blogURL string representation of the URL.
     * @throws MalformedURLException if the given string cannot be parsed to a
     * URL.
     */
    public void setBlogURL(String blogURL) throws MalformedURLException {
        this.blogURL = new URL(blogURL);
    }

    /**
     *
     * @return the URL of the RSS feed.
     */
    public URL getFeedURL() {
        return feedURL;
    }

    /**
     *
     * @param feedURL string representation of the URL.
     * @throws MalformedURLException if the given string cannot be parsed to a
     * URL.
     */
    public void setFeedURL(String feedURL) throws MalformedURLException {
        this.feedURL = new URL(feedURL);
    }

    /**
     *
     * @return the name of the blog.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the name of the blog.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the author of the blog. May be an email address.
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author the author of the blog. Preferably an email address.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public void writeToDatabase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteFromDatabase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
