/*
 * This file is part of iSkin.
 *
 * Copyright (c) 2014 thehutch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.thehutch.iskin.managers;

import me.thehutch.iskin.Attribute;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author thehutch
 */
public abstract class Manager {
	protected final FileConfiguration database;
	protected final String tableName;

	public Manager(FileConfiguration database, String tableName) {
		this.database = database;
		this.tableName = tableName;
	}

	public abstract void update(Attribute attrib, String name, String value);

	public String get(Attribute attrib, String name) {
		return database.getString(tableName + "." + name + "." + attrib.getLabel());
	}

	public boolean contains(String name) {
		return database.contains(tableName + "." + name);
	}

	public void load(String name) {
		if (!database.contains(tableName + "." + name)) {
			for (Attribute attrib : Attribute.values()) {
				update(attrib, name, get(attrib, name));
			}
		}
	}

	protected boolean validURL(String url) {
		return (url != null) && (url.endsWith(".png"));
	}
}
