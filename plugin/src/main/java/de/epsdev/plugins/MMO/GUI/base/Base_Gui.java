package de.epsdev.plugins.MMO.GUI.base;

import de.epsdev.plugins.MMO.GUI.dev.OnClick;
import de.epsdev.plugins.MMO.data.DataManager;
import de.epsdev.plugins.MMO.tools.HiddenItemData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Base_Gui {

    public List<ItemStack> items = new ArrayList<ItemStack>();
    public List<Integer> pos = new ArrayList<Integer>();

    public String name = "";
    public int rows = 1;

    public void addItem(ItemStack material, int amount, String name, ArrayList<String> lore, OnClick onClick, int x, int y){
        ItemMeta meta = material.getItemMeta();

        meta.setDisplayName(name);

        ArrayList<String> newLore = new ArrayList<String>();

        newLore.add(HiddenItemData.encodeString("{ID: "+ DataManager.funs.size() +"}"));
        newLore.addAll(lore);

        meta.setLore(newLore);
        material.setItemMeta(meta);
        items.add(material);

        if(onClick == null){
            DataManager.funs.put(DataManager.funs.size(), (player, itemStack1, inventory) -> {});
        }else {
            DataManager.funs.put(DataManager.funs.size(), onClick);
        }

        pos.add(x);
        pos.add(y);
    }

    public void addItem(Material material, int amount,String name, ArrayList<String> lore, OnClick onClick, int x, int y){
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(name);

        ArrayList<String> newLore = new ArrayList<String>();

        newLore.add(HiddenItemData.encodeString("{ID: "+ DataManager.funs.size() +"}"));

        if(!lore.isEmpty()){
            newLore.add(1, lore.get(0));
        }

        meta.setLore(newLore);
        itemStack.setItemMeta(meta);
        items.add(itemStack);

        if(onClick == null){
            DataManager.funs.put(DataManager.funs.size(), (player, itemStack1, inventory) -> {});
        }else {
            DataManager.funs.put(DataManager.funs.size(), onClick);
        }

        pos.add(x);
        pos.add(y);
    }

    public void adjustRowCount(int amount){
        this.rows = amount/9;

        if(this.rows < 1) this.rows = 1;

    }

    public Base_Gui(String name){
        this.name = "[MMO] " + name;
    }

    public void show(Player player){

        ItemStack[] array = new ItemStack[rows * 9];

        ItemStack filler = DataManager.GUI_FILLER;
        ItemMeta filler_meta = filler.getItemMeta();
        filler_meta.setDisplayName(" ");
        filler.setItemMeta(filler_meta);

        for(int y = 0; y < rows; y++){
            for(int x = 0; x < 9; x++){

                ItemStack itemStack = filler;

                for(int i = 0; i < pos.size()/2; i++){
                    if(pos.get(i * 2) == x && pos.get(i * 2 + 1) == y){
                        itemStack = items.get(i);
                    }
                }
                array[y * 9 + x] = itemStack;
            }
        }

        Inventory gui = Bukkit.createInventory(player,
                9 * rows,
                    name);

        gui.setContents(array);

        player.openInventory(gui);
    }
}
