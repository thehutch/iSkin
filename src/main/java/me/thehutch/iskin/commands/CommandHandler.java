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
package me.thehutch.iskin.commands;

import me.thehutch.iskin.Attribute;
import me.thehutch.iskin.Main;
import me.thehutch.iskin.managers.GroupManager;
import me.thehutch.iskin.managers.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author thehutch
 */
public class CommandHandler implements CommandExecutor {
	private static final ChatColor MESSAGE_COLOUR = ChatColor.WHITE;
	private static final ChatColor BRACKET_COLOUR = ChatColor.DARK_RED;
	private static final ChatColor TITLE_COLOUR = ChatColor.DARK_GRAY;
	private final PlayerManager playerManager;
	private final GroupManager groupManager;
	private final Main plugin;

	public CommandHandler(Main plugin, PlayerManager playerManager, GroupManager groupManager) {
		this.plugin = plugin;
		this.playerManager = playerManager;
		this.groupManager = groupManager;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
		if (args.length == 0) {
			message(cs, "Invalid number of arguments.");
			message(cs, "See '/iskin help' for more information.");
			return false;
		}
		final String cmd = args[0].toLowerCase();
		// Check if the command is /iskin help
		if (cmd.equals("help") && (cs.hasPermission("iskin.help") || cs.isOp())) {
			message(cs, "---------- iSkin Help ----------");
			message(cs, "/iskin player <skin/cape/title> [player name] <url/title>");
			message(cs, "/iskin group <skin/cape/title> <group name> <url/title>");
			message(cs, "/iskin reset [player name]");
			message(cs, "/iskin save");
			message(cs, "/iskin help");
			message(cs, "<> = Required, [] = Optional");
			message(cs, "------------------------------");
			return true;
		}
		// Check if the command is /iskin save
		if (cmd.equals("save") && (cs.hasPermission("iskin.save") || cs.isOp())) {
			message(cs, "Saving all groups and players.");
			this.plugin.saveDatabase();
			return true;
		}
		// Chec if the command is /iskin reset
		if (cmd.equals("reset") && (cs.hasPermission("iskin.reset") || cs.isOp())) {
			if (args.length == 1) {
				this.playerManager.reset(cs.getName());
				return true;
			} else if (args.length == 2) {
				this.playerManager.reset(args[1]);
				return true;
			} else {
				message(cs, "Invalid number of arguments.");
				message(cs, "See '/iskin help' for more information.");
				return false;
			}
		}
		// Check if the command changes a player attribute
		if (cmd.equals("player") && (cs.hasPermission("iskin.setplayer") || cs.isOp())) {
			if (args.length == 3) {
				if (!(cs instanceof Player)) {
					message(cs, "A player is required to perform this command.");
					return false;
				}
				updatePlayer(args[1], cs.getName(), args[2]);
				return true;
			} else if (args.length == 4) {
				updatePlayer(args[1], args[2], args[3]);
				return true;
			}
			message(cs, "Invalid number of arguments.");
			message(cs, "See '/iskin help' for more information.");
			return false;
		}
		// Check if the command changes a group attribute
		if (cmd.equals("group") && (cs.hasPermission("iskin.setgroup") || cs.isOp())) {
			if (args.length == 4) {
				updateGroup(args[1], args[2], args[3]);
				return true;
			}
			message(cs, "Invalid number of arguments.");
			message(cs, "See '/iskin help' for more information.");
			return false;
		}
		return false;
	}

	private void updatePlayer(String attrib, String name, String value) {
		this.playerManager.update(Attribute.valueOf(attrib.toUpperCase()), name, value);
	}

	private void updateGroup(String attrib, String name, String value) {
		this.groupManager.update(Attribute.valueOf(attrib.toUpperCase()), name, value);
	}

	private void message(CommandSender cs, String message) {
		cs.sendMessage(BRACKET_COLOUR
					   + "["
					   + TITLE_COLOUR
					   + "iSkin"
					   + BRACKET_COLOUR
					   + "] "
					   + MESSAGE_COLOUR
					   + message);
	}
}
