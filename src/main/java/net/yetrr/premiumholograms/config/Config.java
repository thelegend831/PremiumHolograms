package net.yetrr.premiumholograms.config;

import net.yetrr.premiumholograms.HologramsPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public static double DEFAULT_HOLOGRAM_X_OFFSET;
    public static double DEFAULT_HOLOGRAM_Y_OFFSET;
    public static double DEFAULT_HOLOGRAM_Z_OFFSET;
    public static boolean CREATE_ON_GUILD_CREATE;
    public static String HOLOGRAM_COMMAND_NAME;
    public static int HOLOGRAM_SCROLL_TIME;
    public static int HOLOGRAM_RELOCATE_COOLDOWN;
    public static String HOLOGRAM_RELOCATE_MESSAGE;
    public static String HOLOGRAM_ONLY_AT_REGION;
    public static String HOLOGRAM_ONLY_AT_OWN_REGION;
    public static String HOLOGRAM_COOLDOWN_MESSAGE;
    public static String HOLOGRAM_NOT_EXISTS;
    public static String HOLOGRAM_REMOVE_MESSAGE;
    public static List<List<String>> HOLOGRAM_PAGES;

    public static void load(HologramsPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();

        for (Field field : Config.class.getFields()) {
            try {
                if (field.getType().equals(List.class)) {
                    continue;
                }

                field.set(null, configuration.get(field.getName().toLowerCase().replace("_", "-")));
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }

        HOLOGRAM_PAGES = new ArrayList<>();

        configuration.getConfigurationSection("hologram-pages").getKeys(false).forEach(string -> {
            HOLOGRAM_PAGES.add(configuration.getStringList("hologram-pages." + string));
        });
    }

}
