package me.talondev.reports.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

public abstract class CommandBase extends Command {
  
  public CommandBase(String name) {
    super(name);
    SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
    commandMap.register(name, "talonreports", this);
  }
  
  public static void makeCommands() {
    new ReportCommand();
    new ReportsCommand();
  }
}
