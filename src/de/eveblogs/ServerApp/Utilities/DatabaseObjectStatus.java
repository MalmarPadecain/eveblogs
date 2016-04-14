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

/**
 * This enum represents the relation of the object to the representing object in the database.
 *
 * @author Malmar Padecain
 */
public enum DatabaseObjectStatus {
    /**
     * The Object is new. There is no representing object in the Database.
     */
    NEW,
    /**
     * The Objet exists in the database and has been modified. It needs to be updated.
     */
    MODIFIED,
    /**
     * The Object extists in the Database and has not been modefied.
     */
    ORIGINAL,
    /**
     * The Object has been deleted from the database. There exists no representing object in the database.
     */
    DELETED
}
