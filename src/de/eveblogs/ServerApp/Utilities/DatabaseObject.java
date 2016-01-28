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
 *
 * @author Malmar Padecain
 */
public abstract class DatabaseObject {
    private Status statusFlag;
    private final int primaryKey;

    /**
     * This enum represents the relation of the object to the representing object in the database.
     */
    public enum Status { 

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
        DELETED};
    
    /**
     *
     * @return the current status of this Object.
     */
    public Status getStatusFlag() {
        return this.statusFlag;
    }
    
    /**
     *
     * @param primaryKey
     * @param statusFlag
     */
    public DatabaseObject(int primaryKey, Status statusFlag) {
        this.primaryKey = primaryKey;
        this.statusFlag = statusFlag;
    }
    
    /**
     * Sets the new status flag.
     * @param statusFlag the new statusFlag.
     */
    protected void setStatusFlag(Status statusFlag) {
        this.statusFlag = statusFlag;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.primaryKey;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DatabaseObject other = (DatabaseObject) obj;
        if (this.primaryKey != other.primaryKey) {
            return false;
        }
        if (this.statusFlag != other.statusFlag) {
            return false;
        }
        return true;
    }
    
    /**
     *
     */
    public abstract void writeToDatabase();

    /**
     *
     */
    public abstract void deleteFromDatabase();
}
