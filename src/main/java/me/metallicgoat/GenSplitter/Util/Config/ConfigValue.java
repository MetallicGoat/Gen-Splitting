package me.metallicgoat.gensplitter.Util.Config;

import org.bukkit.Material;

import java.util.List;

public class ConfigValue {

    public static boolean splitterEnabled = true;

    public static List<Material> splitSpawners = null;

    public static double splitRadius = 2.5;

    public static boolean antiVoidDrops = false;

    public static boolean autoCollectEnabled = true;
    public static int autoCollectPercentKept = 100;
    public static List<Material> autoCollectMessageMaterials = null;
    public static String autoCollectMessage = "{item} &f+{amount}";

}
