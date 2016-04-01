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

import java.util.HashMap;

/**
 * This class is a wrapper for a HashMap that maps the Elements used in the RSS
 * feed of a blog to the database columns.
 *
 * @author Malmar Padecain
 */
public class FeedElementToDBEntryMappingWrapper {

    private final HashMap<String, String> FeedElementToDBEntry;

    private FeedElementToDBEntryMappingWrapper() {
        this.FeedElementToDBEntry = new HashMap<>();
    }

    /**
     *
     * @param entries an array of strings in the form of Key, Value, Key,
     * Value...
     */
    public FeedElementToDBEntryMappingWrapper(String... entries) {
        this();
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("The method must be called with an even number of arguments");
        } else {
            for (int i = 0; i < entries.length; i++) {
                this.FeedElementToDBEntry.put(entries[i], entries[++i]);
            }
        }

    }

    public FeedElementToDBEntryMappingWrapper(HashMap<String, String> map) {
        this();
        this.FeedElementToDBEntry.putAll(map);
    }
    
    public String getEntry(String key) {
        return this.FeedElementToDBEntry.get(key);
    }
}
