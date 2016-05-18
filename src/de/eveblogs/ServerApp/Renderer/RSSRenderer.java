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
package de.eveblogs.ServerApp.Renderer;

import de.eveblogs.ServerApp.EveBlogs;
import de.eveblogs.ServerApp.Utilities.Blogpost;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Malmar Padecain
 */
public class RSSRenderer {

    /**
     * Outputs the RSS feed to the location specified in the config.properties with the header specefied at the same location.
     *
     * @param blogpostList
     * @param targetLocation
     * @throws IOException in case the RSS Header could not be copied to the target location.
     * @throws XMLStreamException
     */
    public static void createFeed(LinkedList<Blogpost> blogpostList, Path targetLocation) throws IOException, XMLStreamException {
        /*
         * copy header to output and append.
         */
        Path locationOfRSSHeader = Paths.get(EveBlogs.getDefaultConfig().getProperty("locationOfRSSHeader"));
        Files.copy(locationOfRSSHeader, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        XMLEventWriter writer = XMLOutputFactory.newFactory().createXMLEventWriter(new FileOutputStream(targetLocation.toFile(), true));

        for (Blogpost blogpost : blogpostList) {
            parseBlogpost(blogpost, writer);
        }
        writer.flush();
        writer.close();

        //Adding closing tags to file.
        FileWriter fileWriter = new FileWriter(targetLocation.toFile(), true);
        fileWriter.write("</channel></rss>");
        fileWriter.flush();
        fileWriter.close();
    }

    private static void parseBlogpost(Blogpost blogpost, XMLEventWriter writer) throws XMLStreamException {
        XMLEventFactory factory = XMLEventFactory.newFactory();
        writer.add(factory.createStartElement(new QName("item"), null, null));

        writer.add(factory.createStartElement(new QName("title"), null, null));
        writer.add(factory.createCharacters(blogpost.getName()));
        writer.add(factory.createEndElement(new QName("title"), null));

        writer.add(factory.createStartElement(new QName("link"), null, null));
        writer.add(factory.createCharacters(blogpost.getBlogpostURL().toString()));
        writer.add(factory.createEndElement(new QName("link"), null));

        writer.add(factory.createStartElement(new QName("description"), null, null));
        writer.add(factory.createCharacters(blogpost.getDescription()));
        writer.add(factory.createEndElement(new QName("description"), null));

        writer.add(factory.createEndElement(new QName("item"), null));

        writer.flush();
    }
}
