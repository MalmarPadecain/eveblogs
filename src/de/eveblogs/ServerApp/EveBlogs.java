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

import de.eveblogs.ServerApp.Fetcher.RSSParser;
import de.eveblogs.ServerApp.Maker.RSSMaker;
import de.eveblogs.ServerApp.Utilities.Blog;
import de.eveblogs.ServerApp.Utilities.Blogpost;
import de.eveblogs.ServerApp.Utilities.Configuration;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
     * Starts the Application. The Application will terminate imediately with
     * exit status 1 if the properties file cannot be opend or read.
     *
     * @param args the comand line arguments. "-fetch" to fetch the RSS feeds
     * without creating new feed. "-create" to create a new feed without
     * fetching first.
     */
    public static void main(String[] args) {
        
        //reads the default configuration from file. If this fais the Application is terminated.
        try {
            defaultConfig = new Configuration("eveblogs.properties");
        } catch (IOException ex) {
            Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        if (args.length > 0) {
            String arg = args[0];
            switch (arg) {
                case "-fetch":
                    break;
                case "-create":
                    break;
            }
        }

        ArrayList<Blog> blogList = new ArrayList<>(1);
        try {
            blogList.add(new Blog("gsc", "Jezaja", "http://giantsecurecontainer.de/", "http://giantsecurecontainer.de/feed/"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(EveBlogs.class.getName()).log(Level.INFO, null, ex);
        }
        RSSParser testParser = new RSSParser(blogList);
        ArrayList<Blogpost> list = testParser.getBlogpostList();
        RSSMaker maker = new RSSMaker(list);
        try {
            maker.createFeed();
        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(EveBlogs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *
     * @return the defaultConfiguration
     */
    public static Configuration getDefaultConfig() {
        return defaultConfig;
    }
}
