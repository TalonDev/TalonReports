package me.talondev.reports;

import org.bukkit.plugin.java.JavaPlugin;

import me.talondev.reports.backend.Backend;
import me.talondev.reports.backend.FileBackend;
import me.talondev.reports.commands.CommandBase;
import me.talondev.reports.utilities.ModuleLogger;

/**
 * Plugin de Reports criado em SpeedCode no video:<br>
 * <link>https://www.youtube.com/watch?v=_ri6XU5sX3M<link>
 * <br><br>
 * Se você está usando essa SourceCode, não é pedir muito deixar essa anotação.
 * ela não irá aparecer em lugar nenhum, não se preocupe. Mas se não quiser
 * apenas delete :)
 * 
 * @author TalonDev
 */
public class TalonReports extends JavaPlugin {

  private static TalonReports instance;
  public static final ModuleLogger LOGGER = new ModuleLogger("TalonReports");

  private Backend backend;

  public TalonReports() {
    instance = this;
  }

  @Override
  public void onEnable() {
    this.backend = new FileBackend();

    CommandBase.makeCommands();

    LOGGER.info("O plugin foi ativado.");
  }

  @Override
  public void onDisable() {
    instance = null;

    LOGGER.info("O plugin foi desativado.");
  }

  public Backend getBackend() {
    return backend;
  }

  public static TalonReports getInstance() {
    return instance;
  }
}
