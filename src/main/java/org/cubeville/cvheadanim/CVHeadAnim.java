package org.cubeville.cvheadanim;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.cvloadouts.CVLoadouts;

import org.cubeville.commons.commands.CommandParser;

public class CVHeadAnim extends JavaPlugin {

    CommandParser commandParser;
    static CVHeadAnim instance;

    static CVHeadAnim getInstance() {
        return instance;
    }
    
    public void onEnable() {
	PluginManager pm = getServer().getPluginManager();

        commandParser = new CommandParser();
        commandParser.addCommand(new HeadanimCommand((CVLoadouts) pm.getPlugin("CVLoadouts")));

        this.instance = this;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("headanim")) {
            return commandParser.execute(sender, args);
        }
	return false;
    }
}
