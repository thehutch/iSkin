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
package me.thehutch.iskin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import me.thehutch.iskin.commands.CommandHandler;
import me.thehutch.iskin.listeners.PlayerListener;
import me.thehutch.iskin.managers.GroupManager;
import me.thehutch.iskin.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author thehutch
 */
public class Main extends JavaPlugin {
	private PlayerManager playerManager;
	private GroupManager groupManager;
	private FileConfiguration database;
	private File databaseFile;

	@Override
	public void onEnable() {
		// Check if the config.yml exists
		final File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			print("Generating new configuration file...");
			saveDefaultConfig();
		}

		// Initialise the database
		this.databaseFile = new File(getDataFolder(), "database.yml");
		this.database = YamlConfiguration.loadConfiguration(databaseFile);
		if (!databaseFile.exists()) {
			try {
				this.database.save(databaseFile);
				print("Generating new database file...");
			} catch (IOException ex) {
				throw new IllegalStateException("Unable to create database file", ex);
			}
		}

		// Initialise the managers
		this.playerManager = new PlayerManager(database);
		this.groupManager = new GroupManager(database, playerManager);

		// Register the player listener
		getServer().getPluginManager().registerEvents(new PlayerListener(getConfig(), database, playerManager), this);

		// Register the command handler
		getCommand("iskin").setExecutor(new CommandHandler(this, playerManager, groupManager));

		// Enable autosaving
		if (getConfig().getBoolean("General.Autosave")) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				@Override
				public void run() {
					saveDatabase();
				}
			}, 1200L, getConfig().getLong("General.Autosave-Interval") * 20L);
		}
	}

	@Override
	public void onDisable() {
		saveDatabase();
	}

	public void saveDatabase() {
		try {
			this.database.save(databaseFile);
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to save database", ex);
		}
	}

	/**
	 * Prints out the message with the iSkin prefix.
	 *
	 * @param msg The message to print
	 */
	private void print(String msg) {
		getLogger().log(Level.INFO, msg);
	}

	/**
	 * Attempts to get the SpoutPlayer instance from the name of the player.
	 *
	 * @param name The name of the player
	 *
	 * @return The SpoutPlayer
	 */
	public static SpoutPlayer getPlayer(String name) {
		return SpoutManager.getPlayer(Bukkit.getPlayer(name));
	}
}
