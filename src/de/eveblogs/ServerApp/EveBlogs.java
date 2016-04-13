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
package de.eveblogs.ServerApp;

import de.eveblogs.ServerApp.Database.DBConnection;
import de.eveblogs.ServerApp.Fetcher.RSSParser;
import de.eveblogs.ServerApp.Utilities.Blog;
import de.eveblogs.ServerApp.Utilities.Blogpost;
import de.eveblogs.ServerApp.Utilities.Configuration;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Malmar Padecain
 */
public class EveBlogs {

    private static Configuration defaultConfig;
    private static DBConnection connection;

    /**
     * Starts the Application. The Application will terminate imediately with
     * exit status 1 if the properties file cannot be opend or read.
     *
     * @param args the comand line arguments. "-fetch" to fetch the RSS feeds
     * without creating new feed. "-create" to create a new feed without
     * fetching first. Leave empty to do both.
     */
    public static void main(String[] args) {
        /*
        reads the default configuration from file. If this fais the Application is terminated.
         */
        try {
            defaultConfig = new Configuration("eveblogs.properties");
        } catch (IOException ex) {
            Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, "Faild to read eveblogs.properties.", ex);
            System.exit(1);
        }

        /*
        establishes the connection to the database
         */
        try {
            connection = DBConnection.getInstance();
        } catch (MalformedURLException | SQLException ex) {
            Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, "Faild to connect to the database.", ex);
            System.exit(2);
        }

        if (args.length > 0) {
            String arg = args[0];
            switch (arg) {
                case "-fetch":
                    try {
                        fetchFeed();
                    } catch (SQLException ex) {
                        Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "-create":
                    createFeed();
                    break;
            }
        } else {
            try {
                fetchFeed();
            } catch (SQLException ex) {
                Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        createFeed();

        /*
        the following is code for testing purposes.
         */
 /*
        ArrayList<Blog> blogList = new ArrayList<>(1);
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("xmlBlogpostName", "title");
            map.put("xmlBlogpostLink", "link");
            map.put("xmlPublicationDateTime", "pubDate");
            map.put("xmlDescription", "description");

            blogList.add(new Blog("gsc", "Jezaja", "http://giantsecurecontainer.de/", "http://giantsecurecontainer.de/feed/", map));
        } catch (MalformedURLException ex) {
            Logger.getLogger(EveBlogs.class.getName()).log(Level.INFO, null, ex);
        }
        for (Blog blog : blogList) {
            try {
                DBConnection.getDBCon().writeObjectToDatabase(blog);
            } catch (SQLException ex) {
                Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        RSSParser testParser = new RSSParser(blogList);
        ArrayList<Blogpost> list = testParser.getBlogpostList();
        for (Blogpost blogpost : list) {
            try {
                DBConnection.getDBCon().writeObjectToDatabase(blogpost);
            } catch (SQLException ex) {
                Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         */
    }

    private static void fetchFeed() throws SQLException {
        LinkedList<Blog> blogList = connection.getAllActiveBlogs();
        RSSParser parser = new RSSParser(blogList);
        LinkedList<Blogpost> blogpostList = parser.getBlogpostList();
        for (Blogpost blogpost : blogpostList) {
            try {
                connection.writeObjectToDatabase(blogpost);
            } catch (SQLException ex) {
                Logger.getLogger(EveBlogs.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void createFeed() {
        // TODO write this
    }

    /**
     *
     * @return the defaultConfiguration
     */
    public static Configuration getDefaultConfig() {
        return defaultConfig;
    }
}
