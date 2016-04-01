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

import java.util.Objects;

/**
 *
 * @author Malmar Padecain
 */
public abstract class DatabaseObject {

    private DatabaseObjectStatus statusFlag;
    private Integer primaryKey;

    /**
     * Returns the current status of this Object.
     * @return the current status of this Object.
     */
    public DatabaseObjectStatus getStatusFlag() {
        return this.statusFlag;
    }
    
    /**
     * 
     * @return the primary key in the database. Null if not existing
     */
    public Integer getPrimaryKey() {
        return this.primaryKey;
    }
    
    /**
     * 
     * @param primaryKey the new primary key.
     */
    public void setPrimaryKey(Integer primaryKey) {
        this.primaryKey = primaryKey;
        this.statusFlag = statusFlag.ORIGINAL;
    }

    /**
     * The default contstructor creates a new instance with primaryKey = null
     * and statusFlag = NEW.
     */
    public DatabaseObject() {
        this.primaryKey = null;
        this.statusFlag = DatabaseObjectStatus.NEW;
    }

    /**
     * Creates a new instance with the given primary key an statusFlag =
     * ORIGINAL.
     *
     * @param primaryKey the primaryKey of the Obect in the database.
     */
    public DatabaseObject(int primaryKey) {
        this.primaryKey = primaryKey;
        this.statusFlag = DatabaseObjectStatus.ORIGINAL;
    }

    /**
     * Sets the new status flag.
     *
     * @param statusFlag the new statusFlag.
     */
    public void setStatusFlag(DatabaseObjectStatus statusFlag) {
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
        if (!Objects.equals(this.primaryKey, other.primaryKey)) {
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
