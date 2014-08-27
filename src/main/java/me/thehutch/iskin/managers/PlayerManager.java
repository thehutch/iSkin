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
import me.thehutch.iskin.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author thehutch
 */
public class PlayerManager extends Manager {
	private static final String RESET_SKIN_URL = "http://s3.amazonaws.com/MinecraftSkins/%s.png";

	public PlayerManager(FileConfiguration database) {
		super(database, "players");
	}

	@Override
	public void update(Attribute attrib, String key, String value) {
		final SpoutPlayer player = Main.getPlayer(key);
		switch (attrib) {
			case SKIN:
				if (validURL(value)) {
					player.setSkin(value);
				}
				break;
			case CAPE:
				if (validURL(value)) {
					player.setCape(value);
				}
				break;
			case TITLE:
				player.setTitle(value);
				break;
		}
		this.database.set(tableName + "." + key + "." + attrib.getLabel(), value);
	}

	public void reset(String name) {
		if (name != null) {
			// Reset the player's skin
			update(Attribute.SKIN, name, String.format(RESET_SKIN_URL, name));
			// Reset the player's cape
			update(Attribute.CAPE, name, "");
			// Reset the player's title
			update(Attribute.TITLE, name, name);
		}
	}
}
