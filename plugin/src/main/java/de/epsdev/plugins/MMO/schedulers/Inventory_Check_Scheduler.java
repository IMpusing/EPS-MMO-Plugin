package de.epsdev.plugins.MMO.schedulers;

import de.epsdev.plugins.MMO.MAIN.main;
import de.epsdev.plugins.MMO.data.DataManager;
import de.epsdev.plugins.MMO.data.output.Out;
import de.epsdev.plugins.MMO.data.player.User;
import de.epsdev.plugins.MMO.data.regions.cites.houses.House;
import de.epsdev.plugins.MMO.tools.Math;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;

public class Inventory_Check_Scheduler {
    public static void run() {
        BukkitScheduler scheduler = main.plugin.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(main.plugin, () -> {

            for (Player player : Bukkit.getOnlinePlayers()){

                if (player.getGameMode() != GameMode.CREATIVE){
                    User user = DataManager.onlineUsers.get(player.getUniqueId().toString());

                    if(user.playerInventory != null){
                        if(!Math.compareInventory(user.playerInventory, player.getInventory())){
                            player.getInventory().clear();
                            player.getInventory().setContents(user.playerInventory);
                        }
                    }
                }

            }

        }, 0L, 10L);
    }
}
