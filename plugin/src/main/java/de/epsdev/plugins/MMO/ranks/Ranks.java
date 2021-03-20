package de.epsdev.plugins.MMO.ranks;

import org.bukkit.ChatColor;

public class Ranks {
    public static Rank Admin = new Rank("admin", ChatColor.DARK_RED + "" + ChatColor.BOLD + "Admin" ,new boolean[]{
            true, //CanBuild
            true, //CanHandleMoney
            true, //CanChangeRanks
            true, //CanMuteChat
            true, //CanSpeakWhenChatMuted
            true, //CanOpenCheatMenu
            true, //CanReloadServer
            true, //CanChangeGamemode
            true, //CanManageRegions
            true, //CanManageHouses
            true,  //CanManageNPC
            true,  //CanManageStaticEffects
            true  //CanManageItems
    },
        new  int[]{
            999,     //MaxHousesOwn
            20       //MaxCharacters
    });
    public static Rank Builder = new Rank("builder", ChatColor.BLUE + "Builder", new boolean[]{
            true, //CanBuild
            false,//CanHandleMoney
            false,//CanChangeRanks
            false,//CanMuteChat
            true, //CanSpeakWhenChatMuted
            false, //CanOpenCheatMenu
            false,  //CanReloadServer
            true, //CanChangeGamemode
            true, //CanManageRegions
            true, //CanManageHouses
            true,  //CanManageNPC
            true,  //CanManageStaticEffects
            true  //CanManageItems
    },
        new  int[]{
                999,     //MaxHousesOwn
                20       //MaxCharacters
    });
    public static Rank Player = new Rank("player", "Player",new  boolean[]{
            false, //CanBuild
            false, //CanHandleMoney
            false, //CanChangeRanks
            false, //CanMuteChat
            false, //CanSpeakWhenChatMuted
            false,  //CanOpenCheatMenu
            false,  //CanReloadServer
            false, //CanChangeGamemode
            false, //CanManageRegions
            false , //CanManageHouses
            false ,  //CanManageNPC
            false,  //CanManageStaticEffects
            false  //CanManageItems
    },
        new  int[]{
                1,     //MaxHousesOwn
                5       //MaxCharacters
    });

    public static Rank Mod = new Rank("mod", ChatColor.DARK_BLUE +"Mod",new  boolean[]{
            false, //CanBuild
            false, //CanHandleMoney
            false, //CanChangeRanks
            true,  //CanMuteChat
            true,  //CanSpeakWhenChatMuted
            false,  //CanOpenCheatMenu
            false,  //CanReloadServer
            true, //CanChangeGamemode
            true, //CanManageRegions
            false, //CanManageHouses
            false,  //CanManageNPC
            false,  //CanManageStaticEffects
            false  //CanManageItems
    },
        new  int[]{
                999,     //MaxHousesOwn
                20       //MaxCharacters
    });

    public static Rank VIP = new Rank("vip", ChatColor.GOLD + "VIP",new  boolean[]{
            false, //CanBuild
            false, //CanHandleMoney
            false, //CanChangeRanks
            false, //CanMuteChat
            false, //CanSpeakWhenChatMuted
            false,  //CanOpenCheatMenu
            false,  //CanReloadServer
            false, //CanChangeGamemode
            false, //CanManageRegions
            false, //CanManageHouses
            false,  //CanManageNPC
            false,  //CanManageStaticEffects
            false  //CanManageItems

    },
            new  int[]{
                    5,     //MaxHousesOwn
                    10       //MaxCharacters
    });

    public static Rank getRank(String name){
        switch (name.toLowerCase()){
            default:
                return null;
            case "player":
                return Player;
            case "admin":
                return Admin;
            case "builder":
                return Builder;
            case "mod":
                return Mod;
            case "vip":
                return VIP;

        }
    }

}
