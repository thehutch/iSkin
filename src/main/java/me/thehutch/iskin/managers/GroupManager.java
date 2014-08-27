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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * @author thehutch
 */
public class GroupManager extends Manager {
	private final Manager playerManager;

	public GroupManager(FileConfiguration database, PlayerManager playerManager) {
		super(database, "groups");
		this.playerManager = playerManager;
	}

	@Override
	public void update(Attribute attrib, String key, String value) {
		for (OfflinePlayer oPlayer : Bukkit.getOfflinePlayers()) {
			final Player player = oPlayer.getPlayer();
			if (player.hasPermission("iskin.group." + key) && (!player.hasPermission("*") || !player.isOp())) {
				this.playerManager.update(attrib, player.getDisplayName(), value);
			}
		}
		this.database.set(tableName + "." + key + "." + attrib.getLabel(), value);
	}
}
