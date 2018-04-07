package me.talondev.reports.commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.talondev.reports.TalonReports;
import me.talondev.reports.api.Report;
import me.talondev.reports.api.ReportType;

public class ReportCommand extends CommandBase {

  private CooldownStorage cooldown;

  public ReportCommand() {
    super("report");
    this.setAliases(Arrays.asList("denunciar"));
    this.cooldown = new CooldownStorage();
    // Tarefa para limpar a HashMap.
    this.cooldown.runTaskTimer(TalonReports.getInstance(), 0, 200);
  }

  static class CooldownStorage extends BukkitRunnable {
    private Map<String, Long> cooldown = new ConcurrentHashMap<>();

    public void setCooldown(String player, int seconds) {
      cooldown.put(player, System.currentTimeMillis() + (seconds * 1000));
    }

    public String getCooldown(String player) {
      long millis = cooldown.containsKey(player) ? cooldown.get(player) : 0;
      if (millis <= 0) {
        return "";
      }
      
      long current = System.currentTimeMillis();
      int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millis - current);
      if (seconds <= 0) {
        return "";
      }

      int m = seconds / 60;
      int s = seconds % 60;
      // apenas usado caso possua algum minuto restando
      // estilo: 10m 40s
      String time = (m > 0 ? m + "m " : "") + (s < 10 ? "0" + s : "" + s);
      if (!time.contains("m")) {
        // não possui minutos
        // vamos detalhar o tempo de segundos para números quebrados:
        // estilo: 1.3s
        time = String.valueOf(Math.ceil(millis - current) / 1000);
        time = time.substring(0, time.indexOf(".") + 2) + "s";
      }
      
      return time;
    }

    @Override
    public void run() {
      for (Iterator<Long> itr = cooldown.values().iterator(); itr.hasNext();) {
        if (itr.next() <= System.currentTimeMillis()) {
          itr.remove();
        }
      }
    }
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;

      if (args.length == 0) {
        player.sendMessage("§cUse: /report <jogador> <motivo>");
        return true;
      }
      
      String cl = cooldown.getCooldown(player.getName());
      if (!cl.isEmpty()) {
        // ja usou esse comando com sucesso a pouco tempo.
        player.sendMessage("§cVocê precisa aguardar mais " + cl + " para executar este comando novamente.");
        return true;
      }

      Player target = Bukkit.getPlayerExact(args[0]);
      if (target == null) {
        player.sendMessage("§cO jogador " + args[0] + " não está online.");
        return true;
      }

      if (args.length == 1) {
        player.sendMessage("§cMotivos de report disponíveis: §eHack, Ofensas");
        return true;
      }

      ReportType type = ReportType.getFromName(args[1]);
      if (type == null) {
        player.sendMessage("§cO motivo " + args[1] + " não é aceitavel.");
        return true;
      }

      Report report = TalonReports.getInstance().getBackend().getReportById(target.getUniqueId());
      if (report == null) {
        report = TalonReports.getInstance().getBackend().createReport(target.getUniqueId(), target.getName());
      } else if (report.hasSubReportById(player.getUniqueId())) {
        player.sendMessage("§cVocê já reportou esse jogador alguma vez.");
        return true;
      }

      // Sucesso, adicionando cooldown.
      // Default: 5 minutos
      cooldown.setCooldown(player.getName(), 300);
      report.increaseByType(type, player.getUniqueId());
      player.sendMessage("§aVocê reportou o jogador §f" + target.getName() + " §apor §f" + type.getReadable() + "§a.");
      return true;
    }

    sender.sendMessage("§cEsse comando e apenas para jogadores!");
    return true;
  }
}
