package me.talondev.reports.commands;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.talondev.reports.TalonReports;
import me.talondev.reports.api.Report;
import me.talondev.reports.api.ReportType;

public class ReportsCommand extends CommandBase implements Listener {

  public ReportsCommand() {
    super("reports");
    this.setAliases(Arrays.asList("denuncias"));

    Bukkit.getServer().getPluginManager().registerEvents(this, TalonReports.getInstance());
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;

      if (!player.hasPermission("talonreports.cmd.reports")) {
        player.sendMessage("§cVocê não possui permissão suficiente para usar este comando!");
        return true;
      }

      Inventory inv = Bukkit.createInventory(null, 54, "Denuncias");
      int slot = 0;
      for (Report report : TalonReports.getInstance().getBackend().listReports()) {
		if (slot > 53) {
		  break; // ja esta no ultimo slot do bau :)
		}
		  
        inv.setItem(slot++,
            createSkull("§a" + report.getPlayerName() + " (" + report.getUniqueId().toString() + ")", report.getPlayerName(), "",
                "§fÚltimo report: " + new SimpleDateFormat("dd/MM/yyyy - hh:mm").format(report.getLastReport()), "",
                " §7* Hack §f(" + report.getReportLevel(ReportType.HACK) + ")",
                " §7* Ofensas §f(" + report.getReportLevel(ReportType.OFENSAS) + ")", "", " §e* Clique Esquerdo + Shift para rejeitar!"));
      }
      player.openInventory(inv);
    }

    sender.sendMessage("§cEsse comando e apenas para jogadores!");
    return true;
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getWhoClicked() instanceof Player) {
      Player player = (Player) evt.getWhoClicked();
      Inventory inv = evt.getInventory();
      ItemStack item = evt.getCurrentItem();

      if (inv.getTitle().equals("Denuncias")) {
        evt.setCancelled(true);

        if (item != null && item.hasItemMeta()) {
          if (item.getType() == Material.SKULL_ITEM) {
            String split = item.getItemMeta().getDisplayName().split(" ")[1];
            split = split.replace("(", "").replace(")", "");
            UUID uuid = UUID.fromString(split);

            if (evt.getClick() == ClickType.SHIFT_LEFT) {
              player.closeInventory();
              TalonReports.getInstance().getBackend().removeReportById(uuid);
              player.sendMessage("§aVocê rejeitou o report sobre o jogador §f"
                  + item.getItemMeta().getDisplayName().split(" ")[0].replace("§a", "") + "§a!");
            }
          }
        }
      }
    }
  }

  private ItemStack createSkull(String display, String owner, String... lore) {
    ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    meta.setOwner(owner);
    meta.setDisplayName(display);
    meta.setLore(Arrays.asList(lore));
    item.setItemMeta(meta);
    return item;
  }
}
