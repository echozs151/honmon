/*  
Copyright 2021 the original author or authors.

This file is part of Honmon.

Honmon is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Honmon is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Honmon.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.example.honmon.Models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "settings")
public class Settings {
    private String id;
    private String description;
    private String key;
    private String value;

    public Settings() {}

    public Settings(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }
    public String getId() {
        return id;
    }
    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Settings(id="+getId()+", key="+getKey()+", value="+getValue()+")";
    }
}
