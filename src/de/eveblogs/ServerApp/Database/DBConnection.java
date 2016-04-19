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
package de.eveblogs.ServerApp.Database;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import de.eveblogs.ServerApp.EveBlogs;
import de.eveblogs.ServerApp.Utilities.Blog;
import de.eveblogs.ServerApp.Utilities.Blogpost;
import de.eveblogs.ServerApp.Utilities.DatabaseObject;
import de.eveblogs.ServerApp.Utilities.DatabaseObjectStatus;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handels the connection to the database.
 *
 * @author Malmar Padecain
 */
public class DBConnection {
    
    private static DBConnection dbCon;

    private final int port;
    private final String dbName;
    private final Connection con;

    private DBConnection(String serverAddress, int port, String dbName, String user, String password) throws MalformedURLException, SQLException {
        this.port = port;
        this.dbName = dbName;
        String connectionURL = "jdbc:mysql://" + serverAddress + ":" + Integer.toString(port) + "/" + dbName;
        this.con = DriverManager.getConnection(connectionURL, user, password);
    }

    /**
     *
     * @param dbObject
     * @return the primary key of of the dbObject in the database.
     * @throws java.sql.SQLException
     */
    public Integer writeObjectToDatabase(DatabaseObject dbObject) throws SQLException {
        PreparedStatement statement;
        Integer primaryKey;
        if (dbObject.getClass().equals(Blog.class)) {
            Blog blog = (Blog) dbObject;
            switch (blog.getStatusFlag()) {
                case NEW:
                    statement = con.prepareStatement("INSERT INTO tblBlog (blogLink, blogName, feedLink, author, lastUpdate) VALUES (?, ?, ?, ?, ?)");
                    statement.setString(1, blog.getBlogURL().toExternalForm());
                    statement.setString(2, blog.getName());
                    statement.setString(3, blog.getFeedURL().toExternalForm());
                    statement.setString(4, blog.getAuthor());
                    statement.setTimestamp(5, new Timestamp(blog.getLastUpdate().getTime()));
                    statement.executeUpdate();
                    ResultSet rs = con.createStatement().executeQuery("SELECT LAST_INSERT_ID();");
                    rs.first();
                    primaryKey = rs.getInt(1);
                    blog.setPrimaryKey(primaryKey);
                    blog.setStatusFlag(DatabaseObjectStatus.ORIGINAL);
                    return primaryKey;
                case MODIFIED:
                    statement = con.prepareStatement("UPDATE tblBlog SET blogLink = ?, blogName = ?, feedLink = ?, author = ?, lastUpdate = ? WHERE PK_Blog = ?");
                    statement.setString(1, blog.getBlogURL().toExternalForm());
                    statement.setString(2, blog.getName());
                    statement.setString(3, blog.getFeedURL().toExternalForm());
                    statement.setString(4, blog.getAuthor());
                    statement.setTimestamp(5, new Timestamp(blog.getLastUpdate().getTime()));
                    statement.setInt(6, blog.getPrimaryKey());
                    statement.executeUpdate();
                    blog.setStatusFlag(DatabaseObjectStatus.ORIGINAL);
                    return blog.getPrimaryKey();
                case ORIGINAL:
                    return blog.getPrimaryKey();
                default:
                    return null;
            }
        } else if (dbObject.getClass().equals(Blogpost.class)) {
            Blogpost blogpost = (Blogpost) dbObject;
            switch (blogpost.getStatusFlag()) {
                case NEW:
                    /*
                     * TODO check for the last time the blog was processed. only write recent blogposts to db
                     */
                    try {
                        statement = con.prepareStatement("INSERT INTO tblBlogpost (blogpostLink, blogpostName, description, FK_Blog, publicationDateTime) VALUES (?, ?, ?, ?, ?)");
                        statement.setString(1, blogpost.getBlogpostURL().toExternalForm());
                        statement.setString(2, blogpost.getName());
                        statement.setString(3, blogpost.getDescription());
                        statement.setInt(4, blogpost.getBlog().getPrimaryKey());
                        statement.setTimestamp(5, new Timestamp(blogpost.getPubDate().getTime()));
                        statement.executeUpdate();
                        ResultSet rs = con.createStatement().executeQuery("SELECT LAST_INSERT_ID();");
                        rs.first();
                        primaryKey = rs.getInt(1);
                        blogpost.setPrimaryKey(primaryKey);
                        blogpost.setStatusFlag(DatabaseObjectStatus.ORIGINAL);
                        return primaryKey;
                    } catch (MySQLIntegrityConstraintViolationException ex) {
                        return null;
                    }
                case MODIFIED:
                    statement = con.prepareStatement("UPDATE tblBlogpost SET blogpostLink = ?, blogpostName = ?, description = ?, FK_Blog = ?, publicationDateTime = ?");
                    statement.setString(1, blogpost.getBlogpostURL().toExternalForm());
                    statement.setString(2, blogpost.getName());
                    statement.setString(3, blogpost.getDescription());
                    statement.setInt(4, blogpost.getBlog().getPrimaryKey());
                    statement.setTimestamp(5, new Timestamp(blogpost.getPubDate().getTime()));
                    statement.executeUpdate();
                    blogpost.setStatusFlag(DatabaseObjectStatus.ORIGINAL);
                    return blogpost.getPrimaryKey();
                case ORIGINAL:
                    return blogpost.getPrimaryKey();
                default:
                    return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * Initialises the Connection to the database.
     * Must be called before calling getDBCon(). If MalformedURLException is thrown check your entries in the properties file.
     * @throws MalformedURLException
     * @throws SQLException 
     */
    public static void initConnection() throws MalformedURLException, SQLException {
        if (dbCon == null) {
            int port = Integer.parseInt(EveBlogs.getDefaultConfig().getProperty("DataBaseServerPort"));
            String serverAddress = EveBlogs.getDefaultConfig().getProperty("DataBaseServerAddress");
            String dbName = EveBlogs.getDefaultConfig().getProperty("DataBaseName");
            String user = EveBlogs.getDefaultConfig().getProperty("DataBaseUser");
            String password = EveBlogs.getDefaultConfig().getProperty("DataBasePassword");
            dbCon = new DBConnection(serverAddress, port, dbName, user, password);
        }
    }
    
    /**
     * Returns the database connection. If none has been initialised it is created.
     *
     * @return the database connection
     */
    public static DBConnection getDBCon(){
        return dbCon;
    }

    public Blog getBlogFromDB(int primaryKey) {
        try {
            PreparedStatement statement = this.con.prepareStatement("SELECT feedLink, blogName, blogLink, author, xmlBlogpostName, xmlBlogpostLink, xmlPublicationDateTime, xmlDescription FROM tblBlog WHERE PK_Blog = ?;");
            statement.setInt(1, primaryKey);
            Blog blog;
            try (ResultSet rs = statement.executeQuery()) {
                rs.first();
                String feedLink = rs.getString("feedLink"); // the link to the feed of the blog
                String name = rs.getString("blogName"); // the name of the blog
                String blogLink = rs.getString("blogLink"); // the link to the blogs main page
                String author = rs.getString("author"); // the author of the blog. prefarably an e-mail address.
                HashMap<String, String> map = createMap(rs);
                blog = new Blog(name, author, blogLink, feedLink, map);
                blog.setPrimaryKey(primaryKey);
            }
            return blog;
        } catch (SQLException | MalformedURLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Blog getBlogFromDB(URL url) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery("SELECT PK_Blog FROM tblBlog WHERE blogLink = '" + url + "' OR feedLink = '" + url + "';");
        rs.first();
        return getBlogFromDB(rs.getInt("PK_Blog"));
    }

    public LinkedList<Blog> getAllActiveBlogs() throws SQLException {
        LinkedList<Blog> blogList = new LinkedList<>();
        Statement statement = con.createStatement();
        String query = "SELECT PK_Blog, blogName, blogLink, feedLink, author, lastUpdate, xmlBlogpostName, xmlBlogpostLink, xmlPublicationDateTime, xmlDescription FROM tblBlog WHERE active = 1";
        try (ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                try {
                    HashMap<String, String> map = createMap(rs);
                    
                    /*
                     * assigning the return values of the result set to local variables.
                     */
                    Integer primaryKey = rs.getInt("PK_Blog");
                    String blogName = rs.getString("blogName");
                    String author = rs.getString("author");
                    String blogLink = rs.getString("blogLink");
                    String feedLink = rs.getString("feedLink");
                    
                    blogList.add(new Blog(primaryKey, blogName, author, blogLink, feedLink, map));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return blogList;
    }

    /**
     * Creates a map that resolves the field names in the database to the coresponding tag names in the feed.
     * @param rs
     * @return
     * @throws SQLException 
     */
    private HashMap<String, String> createMap(ResultSet rs) throws SQLException{
        HashMap<String, String> map = new HashMap<>();
        map.put("blogpostName", rs.getString("xmlBlogpostName"));
        map.put("blogpostLink", rs.getString("xmlBlogpostLink"));
        map.put("publicationDateTime", rs.getString("xmlPublicationDateTime"));
        map.put("description", rs.getString("xmlDescription"));
        return map;
    }
}
