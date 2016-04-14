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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Malmar Padecain
 */
public class Configuration {

    private final Properties defaultProps;

    /**
     * Creates a new Configuration object.
     *
     * @param location the location where the properties file can be found.
     * @throws java.io.IOException in case the file could not be opend or read.
     */
    public Configuration(String location) throws IOException {
        defaultProps = new Properties();
        try (FileInputStream in = new FileInputStream(location)) {
            defaultProps.load(in);
        }
    }

    /**
     * Gives access to the values in the Property. look java.util.Properties
     *
     * @param key the property key.
     * @return the value behind the given key. Null if there is no such key.
     */
    public String getProperty(String key) {
        return this.defaultProps.getProperty(key);
    }
}
