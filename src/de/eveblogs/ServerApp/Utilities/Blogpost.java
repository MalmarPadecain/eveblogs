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

import de.eveblogs.ServerApp.Database.DBConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
//import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class representing a blogpost. It contains methods to create a blogpost from the database, write a blogpost to the database and manipualating a blogpost in
 * the database.
 *
 * @author Malmar Padecain
 */
public class Blogpost extends DatabaseObject {

    private URL blogpostURL;
    private String name;
    private String description;
    private Date pubDate;
    private Blog blog;

    /**
     * Creates a new representation of a blog from the data base. StatusFlag will be ORIGINAL.
     *
     * @param primaryKey the primary key of the post in the database
     */
    public Blogpost(int primaryKey) {
        super(primaryKey);
    }

    /**
     * Creates a new Blog. The statusFlag will be NEW and the primary key null.
     *
     * @param blogpostURL string reprensatation of the URL of the blogpost.
     * @param name the name of the blogpost.
     * @param descripion a short summary of the content of the blogpost.
     * @param pubDate
     * @param blog
     * @throws MalformedURLException
     */
    public Blogpost(String blogpostURL, String name, String descripion, Date pubDate, Blog blog) throws MalformedURLException {
        this.blogpostURL = new URL(blogpostURL);
        this.name = name;
        this.description = descripion;
        this.blog = blog;
        this.pubDate = pubDate;
    }

    /**
     * Returns the descripion of the blogpost.
     *
     * @return the descripion of the blogpost.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the blogpost.
     *
     * @param description a short summary of the content of the post.
     */
    public void setDescription(String description) {
        this.description = description;
        this.setStatusFlag(DatabaseObjectStatus.MODIFIED);
    }

    /**
     * Returns the URL of the blogpost.
     *
     * @return the URL of the blogpost.
     */
    public URL getBlogpostURL() {
        return blogpostURL;
    }

    /**
     * Sets the blogpostURL.
     *
     * @param blogpostURL string representation of the URL of the blogpost.
     * @throws MalformedURLException if the given string cannot be parsed to a URL.
     */
    public void setBlogpostURL(String blogpostURL) throws MalformedURLException {
        this.blogpostURL = new URL(blogpostURL);
        this.setStatusFlag(DatabaseObjectStatus.MODIFIED);
    }

    /**
     * Returns the name of the blogpost.
     *
     * @return the name of the blogpost.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the blogpost.
     *
     * @param name a string representing the name of the blogpost.
     */
    public void setName(String name) {
        this.name = name;
        this.setStatusFlag(DatabaseObjectStatus.MODIFIED);
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    @Override
    public void writeToDatabase() {
        DatabaseObjectStatus oldStatusFlag = getStatusFlag();
        try {
            DBConnection.getDBCon().writeObjectToDatabase(this);
        } catch (SQLException ex) {
            Logger.getLogger(Blogpost.class.getName()).log(Level.WARNING, null, ex);
            this.setStatusFlag(oldStatusFlag);
        }
    }

    @Override
    public void deleteFromDatabase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return this.getName() + " at " + this.getPubDate() + " from Blog: (" + this.getBlog().toString() + ")";
    }
}
