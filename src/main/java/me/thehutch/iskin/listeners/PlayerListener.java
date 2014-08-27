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
package me.thehutch.iskin.listeners;

import me.thehutch.iskin.Attribute;
import me.thehutch.iskin.managers.PlayerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author thehutch
 */
public class PlayerListener implements Listener {
	private final PlayerManager playerManager;
	private final FileConfiguration database;
	// First Join Values
	private final boolean overrideFirstJoin;
	private final String skinURL;
	private final String capeURL;
	private final String title;

	public PlayerListener(FileConfiguration config, FileConfiguration database, PlayerManager playerManager) {
		this.playerManager = playerManager;
		this.database = database;
		// Initialise First Join Values
		this.overrideFirstJoin = config.getBoolean("First-Join.Override");
		this.skinURL = config.getString("First-Join.Skin-URL");
		this.capeURL = config.getString("First-Join.Cape-URL");
		this.title = config.getString("First-Join.Title");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpoutcraftEnabled(SpoutCraftEnableEvent event) {
		final SpoutPlayer player = event.getPlayer();
		final String name = player.getName();
		// Check if the player exits
		if (!database.contains("players." + name)) {
			if (overrideFirstJoin) {
				this.playerManager.update(Attribute.SKIN, name, skinURL);
				this.playerManager.update(Attribute.CAPE, name, capeURL);
				this.playerManager.update(Attribute.TITLE, name, title);
			} else {
				this.playerManager.update(Attribute.SKIN, name, player.getSkin());
				this.playerManager.update(Attribute.CAPE, name, player.getCape());
				this.playerManager.update(Attribute.TITLE, name, player.getTitle());
			}
		}
		// Load the player data
		this.playerManager.load(name);
		// Update the player
		updatePlayer(name);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		updatePlayer(event.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		updatePlayer(event.getPlayer().getName());
	}

	private void updatePlayer(String name) {
		for (Attribute attrib : Attribute.values()) {
			final String value = playerManager.get(attrib, name);
			if (value != null) {
				this.playerManager.update(attrib, name, value);
			}
		}
	}
}
