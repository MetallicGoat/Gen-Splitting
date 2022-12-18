package me.metallicgoat.gensplitter.config;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class ConfigValue {

    public static boolean splitterEnabled = true;

    public static List<Material> splitSpawners = Arrays.asList(Material.IRON_INGOT, Material.GOLD_INGOT);

    public static double splitRadius = 2.5;

    public static boolean antiVoidDrops = false;

    public static boolean autoCollectEnabled = true;
    public static int autoCollectPercentKept = 100;
    public static List<Material> autoCollectMessageMaterials = Arrays.asList(Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND, Material.EMERALD);
    public static String autoCollectMessage = "{item} &f+{amount}";

}
