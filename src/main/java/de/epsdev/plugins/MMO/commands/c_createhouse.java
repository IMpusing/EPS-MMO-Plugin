package de.epsdev.plugins.MMO.commands;

import de.epsdev.plugins.MMO.data.DataManager;
import de.epsdev.plugins.MMO.data.output.Err;
import de.epsdev.plugins.MMO.data.output.Out;
import de.epsdev.plugins.MMO.data.player.User;
import de.epsdev.plugins.MMO.data.regions.cites.City;
import de.epsdev.plugins.MMO.data.regions.cites.houses.House;
import de.epsdev.plugins.MMO.events.OnBreak;
import de.epsdev.plugins.MMO.events.OnPlace;
import de.epsdev.plugins.MMO.tools.Vec3i;
import de.epsdev.plugins.MMO.tools.WorldTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class c_createhouse implements CommandExecutor {

    private OnPlace deff_blocks = e -> {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        User user = DataManager.onlineUsers.get(player.getUniqueId().toString());
        user.temp_house.processBlock(new Vec3i(block.getX(),block.getY(),block.getZ()), false);
    };

    private OnBreak dest_blocks = e -> {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        User user = DataManager.onlineUsers.get(player.getUniqueId().toString());
        user.temp_house.processBlock(new Vec3i(block.getX(),block.getY(),block.getZ()), true);
    };

    private Next stepOneFinish = user -> {
        WorldTools.fillBlocks(user.temp_house.blocksInside, Material.AIR);
        user.next = null;
        user.onBreak = null;
        user.onPlace = null;
        Out.printToBroadcast("sssssssssssss");
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;
            User user = DataManager.onlineUsers.get(player.getUniqueId().toString());

            if(user.rank.canManageHouses){
                if(args.length >= 1){
                    City city = DataManager.getCityByName(args[0]);
                    if(city != null){

                        Out.printToPlayer( player, ChatColor.DARK_GREEN + "Please fill the house.");
                        Out.printToPlayer( player, ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "NOTE: Remove all chests before filling the house.");

                        Inventory inventory = player.getInventory();
                        inventory.clear();
                        ItemStack greenConcrete = new ItemStack(Material.CONCRETE, 1, (byte) 5);
                        ItemStack[] items = new ItemStack[]{greenConcrete};
                        inventory.addItem(items);
                        player.getInventory().setContents(items);

                        DataManager.onlineUsers.get(player.getUniqueId().toString()).temp_house = new House();
                        DataManager.onlineUsers.get(player.getUniqueId().toString()).onPlace = deff_blocks;
                        DataManager.onlineUsers.get(player.getUniqueId().toString()).onBreak = dest_blocks;
                        DataManager.onlineUsers.get(player.getUniqueId().toString()).next = stepOneFinish;
                    }else {
                        Err.cityNotFoundError(player);
                    }
                }else {
                    Err.notEnoughArgumentsError(player);
                    Err.correctUsage(player, new String[]{"cityname"});
                }
            }else {
                Err.rankError(player);
            }

        }

        return true;
    }
}
