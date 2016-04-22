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
import de.eveblogs.ServerApp.Renderer.RSSRenderer;
import de.eveblogs.ServerApp.Utilities.Blog;
import de.eveblogs.ServerApp.Utilities.Blogpost;
import de.eveblogs.ServerApp.Utilities.Configuration;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Malmar Padecain
 */
public class EveBlogs {

    private static Configuration defaultConfig;

    /**
     * Starts the Application. The Application will terminate imediately with exit status 1 if the properties file cannot be opend or read.
     *
     * @param args the comand line arguments. "-fetch" to fetch the RSS feeds without creating new feed. "-create" to create a new feed without fetching first.
     * Leave empty to do both.
     */
    public static void main(String[] args) {
        /*
         * reads the default configuration from file. If this fais the Application is terminated.
         */
        try {
            defaultConfig = new Configuration("eveblogs.properties");
        } catch (IOException ex) {
            Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, "Faild to read eveblogs.properties.", ex);
            System.exit(1);
        }

        /*
         * initialises the connection to the database
         */
        try {
            DBConnection.initConnection();
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
                    try {
                        createFeed();
                    } catch (Exception ex) {
                        Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
            }
        } else {
            try {
                fetchFeed();
            } catch (SQLException ex) {
                Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            createFeed();
        } catch (Exception ex) {
            Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void fetchFeed() throws SQLException {
        LinkedList<Blog> blogList = DBConnection.getDBCon().getAllActiveBlogs();
        RSSParser parser = new RSSParser(blogList);
        LinkedList<Blogpost> blogpostList = parser.getBlogpostList();
        for (Blogpost blogpost : blogpostList) {
            DBConnection.getDBCon().writeObjectToDatabase(blogpost);
        }
    }

    private static void createFeed() throws SQLException, IOException, XMLStreamException {
        LinkedList<Blogpost> list = DBConnection.getDBCon().getLatestBlogposts(50);
        RSSRenderer renderer = new RSSRenderer(list);
        renderer.createFeed();
    }

    /**
     *
     * @return the defaultConfiguration
     */
    public static Configuration getDefaultConfig() {
        return defaultConfig;
    }
}
