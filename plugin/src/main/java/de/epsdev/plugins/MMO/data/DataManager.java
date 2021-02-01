package de.epsdev.plugins.MMO.data;

import de.epsdev.plugins.MMO.GUI.dev.City_GUI;
import de.epsdev.plugins.MMO.GUI.dev.Dev_Houses_Gui;
import de.epsdev.plugins.MMO.GUI.dev.OnClick;
import de.epsdev.plugins.MMO.GUI.dev.Regions_GUI;
import de.epsdev.plugins.MMO.data.money.Money;
import de.epsdev.plugins.MMO.data.mysql.mysql;
import de.epsdev.plugins.MMO.data.output.Err;
import de.epsdev.plugins.MMO.data.output.Out;
import de.epsdev.plugins.MMO.data.player.Character;
import de.epsdev.plugins.MMO.data.player.User;
import de.epsdev.plugins.MMO.data.regions.Region;
import de.epsdev.plugins.MMO.data.regions.cites.City;
import de.epsdev.plugins.MMO.data.regions.cites.houses.House;
import de.epsdev.plugins.MMO.npc.NPC_Manager;
import de.epsdev.plugins.MMO.tools.Colors;
import de.epsdev.plugins.MMO.tools.Vec2f;
import de.epsdev.plugins.MMO.tools.Vec3f;
import de.epsdev.plugins.MMO.tools.Vec3i;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class DataManager {
    public static final ItemStack GUI_FILLER = new ItemStack(Material.STAINED_GLASS_PANE, 1, Colors.LIGHT_GREY);
    public static final int SECS_PER_RENT = 60 * 60;
    public static final Location spawnLocation = new Location(Bukkit.getWorld("world"), 75,72,166);

    public static Map<String,User> onlineUsers = new HashMap<String, User>();

    public static List<Region> regions = new ArrayList<>();

    public static boolean chatMuted = false;

    public static int max_id_cities = 0;
    public static int max_id_houses = 1;

    public static Map<Integer, OnClick> funs = new HashMap<>();

    public static void performClickFunction(Player player, int i, ItemStack item, Inventory inventory){
        if (funs.containsKey(i)){
            funs.get(i).click(player, item, inventory);
        }else {
            Err.funNotRegisteredError();
        }
    }

    public static User getUser(Player player, boolean online) throws SQLException {
        return new User(player, online);
    }

    public static User getUserByName(String name){
        User user = null;

        for(Map.Entry<String, User> entry : onlineUsers.entrySet()) {
            String key = entry.getKey();
            User value = entry.getValue();

            if(value.displayName.equalsIgnoreCase(name)){
                user = value;
                break;
            }

        }

        return user;
    }

    public static void saveUser(User user){
        user.save();
        onlineUsers.remove(user.UUID);
        onlineUsers.put(user.UUID, user);
    }
    //-----------------------------------------------------------------
    //Region Stuff
    //-----------------------------------------------------------------

    public static void reloadRegions(){
        regions = new ArrayList<>();
        loadAllRegions();

        loadAllCities();
        loadAllHouses();

        Regions_GUI.init();

    }

    public static boolean createRegion(String name, Player executor){
        if(!isRegionAlreadyExisting(name)){
            Region region = new Region(name, 1);
            region.save();
            reloadRegions();
            return true;
        }else {

            Err.regionAlreadyExistsError(executor);
            return false;
        }


    }

    public static boolean isRegionAlreadyExisting(String name){
        for(Region region : regions){
            if(region.name.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    public static Region getRegionByName(String name){
        for(Region region : regions){
            if(region.name.toLowerCase().equalsIgnoreCase(name.toLowerCase())){
                return region;
            }
        }

        return null;
    }

    public static void loadAllRegions() {
        try {
            ResultSet rs = mysql.query("SELECT * FROM `eps_regions`.`regions`");
            while(rs.next()){
                Region region = new Region(rs.getString("NAME"), rs.getInt("LEVEL"));
                region.id = rs.getInt("ID");
                regions.add(region);
            }
        }catch(SQLException exception){
            exception.printStackTrace();
        }
    }

    public static void deleteRegion(Region region){
        region.delete();
    }

    //-----------------------------------------------------------------
    //City Stuff
    //-----------------------------------------------------------------

    public static void createCity(String name, Region region){
        mysql.query("INSERT INTO `eps_regions`.`cities` (`ID`, `NAME`, `REGION_ID`) VALUES (NULL, '"+ name + "', '" + region.id + "');");
        regions = new ArrayList<>();
        reloadRegions();
    }

    public static City getCityByName(String name){

        City city = null;

        for(Region region : regions){
            for(City c : region.cities){
                if(c.name.equalsIgnoreCase(name)) return c;
            }
        }

        return city;

    }

    public static Boolean isCityExisting(String name){
        if (getCityByName(name) != null){
            return true;
        }else{
            return false;
        }

    }

    public static void reloadCityGUI(){
        for (Region region : regions){
            region.city_gui = new City_GUI(region);
        }
    }

    public static void loadAllCities(){
        try {
            ResultSet rs = mysql.query("SELECT * FROM `eps_regions`.`cities`");
            int regionID = 0;
            City city = null;
            while(rs.next()){
                city = new City(rs.getInt("ID"), rs.getString("NAME"));
                regionID = rs.getInt("REGION_ID");

                for(Region region : regions){
                    if (region.id == regionID){
                        city.initGui(region);
                        region.cities.add(city);
                        region.cities.sort(Comparator.comparing(City::getId));
                    }
                }
            }

            reloadCityGUI();

        }catch(SQLException exception){
            exception.printStackTrace();
        }
    }

    public static City getCityByID(int id){
        for(Region region : regions){
            for(City city : region.cities){
                if(city.id == id) return city;
            }
        }

        return null;
    }
    //-----------------------------------------------------------------
    //House Stuff
    //-----------------------------------------------------------------

    public static int getNextHouseID(){
        max_id_houses++;
        return max_id_houses;
    }

    public static House getHouseBySign(Vec3i pos){
        for(Region region : regions){
            for (City city : region.cities){
                for(House house : city.houses){
                    if(house.shield.pos.equals(pos)){
                        return house;
                    }
                }
            }
        }
        return null;
    }

    public static House getHouseByDoor(Vec3i pos){
        for(Region region : regions){
            for (City city : region.cities){
                for(House house : city.houses){
                    for(Vec3i vec3i : house.doors){
                        if(vec3i.equals(pos) || pos.equals(new Vec3i(vec3i.x,vec3i.y + 1, vec3i.z))){
                            return house;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static House getHouseByID(int id)
    {
        for(Region region : regions){
            for (City city : region.cities){
                for(House house : city.houses){
                    if(house.id == id){
                        return house;
                    }
                }
            }
        }
        return null;
    }

    public static ArrayList<House> getHousesOwnedByPlayer(String uuid){
        ArrayList<House> houses = new ArrayList<>();

        for(House house : getSoldHouses()){
            if(house.currentOwner_UUID.equalsIgnoreCase(uuid)) houses.add(house);
        }

        return houses;
    }

    public static ArrayList<House> getHousesOwnedByPlayer(Player player){
        ArrayList<House> houses = new ArrayList<>();

        String uuid = player.getUniqueId().toString();

        for(House house : getSoldHouses()){
            if(house.currentOwner_UUID.equalsIgnoreCase(uuid)) houses.add(house);
        }

        return houses;
    }

    public static ArrayList<House> getSoldHouses(){
        
        ArrayList<House> rented = new ArrayList<House>();
                
        for(Region region : regions){
            for (City city : region.cities){
                for(House house : city.houses){
                    if(!house.currentOwner_UUID.equals("0")){
                        rented.add(house);
                    }
                }
            }
        }
        return rented;
    }

    public static void saveAllHouses(){
        for(Region region : regions){
            for (City city : region.cities){
                for(House house : city.houses){
                    house.save(false, false);
                }
            }
        }
    }

    public static void loadAllHouses(){
        try {
            ResultSet rs = mysql.query("SELECT * FROM `eps_regions`.`houses`");

            while(rs.next()){

                int house_id = rs.getInt("ID");
                String house_name = rs.getString("NAME");
                Money house_cost = new Money(rs.getInt("COSTS"));
                String house_current_ownerUUID = rs.getString("OWNER_UUID");

                ArrayList<Vec3i> house_blocksinside = new ArrayList<Vec3i>();
                ArrayList<Vec3i> house_doors = new ArrayList<Vec3i>();

                String temp_string = "";

                int i = 0;
                Vec3i temp_vec3i = new Vec3i();
                temp_string = rs.getString("BLOCKS_INSIDE");
                for(String string : temp_string.split(">>")){
                    i++;
                    if(i == 1){
                        temp_vec3i.x = Integer.parseInt(string);
                    }else if(i == 2){
                        temp_vec3i.y = Integer.parseInt(string);
                    }else{
                        temp_vec3i.z = Integer.parseInt(string);
                        i = 0;
                        house_blocksinside.add(temp_vec3i);
                        temp_vec3i = new Vec3i();
                    }
                }

                i = 0;
                temp_string = rs.getString("DOORS");

                for(String string : temp_string.split(">>")){
                    i++;
                    if(i == 1){
                        temp_vec3i.x = Integer.parseInt(string);
                    }else if(i == 2){
                        temp_vec3i.y = Integer.parseInt(string);
                    }else{
                        temp_vec3i.z = Integer.parseInt(string);
                        i = 0;
                        house_doors.add(temp_vec3i);
                        temp_vec3i = new Vec3i();

                    }
                }


                temp_string = rs.getString("SPAWN_POS");

                Vec3i house_spawnpoint = new Vec3i(Integer.parseInt(temp_string.split(">>")[0]),
                        Integer.parseInt(temp_string.split(">>")[1]),
                        Integer.parseInt(temp_string.split(">>")[2]));

                temp_string = rs.getString("SHIELD_POS");

                Vec3i house_shield = new Vec3i(Integer.parseInt(temp_string.split(">>")[0]),
                        Integer.parseInt(temp_string.split(">>")[1]),
                        Integer.parseInt(temp_string.split(">>")[2]));

                City city = DataManager.getCityByID(rs.getInt("CITY_ID"));

                int renttime = rs.getInt("RENTTIME");

                House house = new House(house_cost,
                        house_id ,
                        house_current_ownerUUID,
                        house_name,
                        house_blocksinside,
                        house_doors,
                        house_shield,
                        house_spawnpoint,
                        city,
                        renttime);

                city.houses.add(house);
                city.gui.houses_gui = new Dev_Houses_Gui(city);
                house.updateSign();

                if(house_id > max_id_houses){
                    max_id_houses = house_id;
                }

                Out.printToConsole("Loaded: " + house.name);
            }

            }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------
    //NPC Stuff
    //-----------------------------------------------------------------

    public static void loadAllNPC() {
        try {
            for(Player player : Bukkit.getOnlinePlayers()){
                NPC_Manager.unloadAllNPC(player);
            }

            ResultSet rs = mysql.query("SELECT * FROM `eps_regions`.`npc`");

            while (rs.next()) {
                String name = rs.getString("NAME");
                String skin = rs.getString("SKIN");
                String script = rs.getString("SCRIPT");

                int id = rs.getInt("ID");

                String tmp = rs.getString("POS");
                String[] _tmp = tmp.split(">>");

                Vec3f pos = new Vec3f(Float.parseFloat(_tmp[0]),
                        Float.parseFloat(_tmp[1]),
                        Float.parseFloat(_tmp[2]));

                tmp = rs.getString("ROTATION");
                _tmp = tmp.split(">>");

                Vec2f rot = new Vec2f(Float.parseFloat(_tmp[0]),
                        Float.parseFloat(_tmp[1]));

                EntityPlayer npc_e = NPC_Manager.createNPC_ENTITY(name, pos, rot, skin);
                NPC_Manager.addNPCPacket(npc_e, NPC_Manager.getSkin(skin), script, id);


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------
    //Character Stuff
    //-----------------------------------------------------------------

    public static List<Character> getPlayerCharacters(String uuid){
        List<Character> characters = new ArrayList<>();

        try {
            ResultSet rs = mysql.query("SELECT * FROM `eps_users`.`characters` WHERE UUID='" + uuid + "';");

            while (rs.next()) {
                String name = rs.getString("NAME");
                int id = rs.getInt("ID");
                int exp = rs.getInt("EXP");
                int level = rs.getInt("LEVEL");

                String tmp = rs.getString("LAST_POS");
                String[] _tmp = tmp.split(">>");

                Vec3f pos = new Vec3f(Float.parseFloat(_tmp[0]),
                        Float.parseFloat(_tmp[1]),
                        Float.parseFloat(_tmp[2]));

                characters.add(new Character(name, exp, level, id, pos, uuid));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return characters;
    }

    public static boolean doesCharWithNameExist(String name){

        try {
            ResultSet rs = mysql.query("SELECT * FROM `eps_users`.`characters` WHERE NAME = '" + name + "';");
            if(rs.next()) return true;

        }catch (Exception e){
            return true;
        }

        return false;
    }

}