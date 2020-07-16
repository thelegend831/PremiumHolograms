package net.yetrr.premiumholograms;

import net.yetrr.premiumguilds.api.PremiumGuildsAPI;
import net.yetrr.premiumguilds.api.PremiumGuildsPlugin;
import net.yetrr.premiumguilds.api.module.Module;
import net.yetrr.premiumguilds.api.module.ModuleInfo;
import net.yetrr.premiumguilds.api.module.impl.ModuleInitializer;
import net.yetrr.premiumholograms.command.HologramCommand;
import net.yetrr.premiumholograms.config.Config;
import net.yetrr.premiumholograms.hologram.GuildHologramTask;
import net.yetrr.premiumholograms.hologram.HologramManager;
import net.yetrr.premiumholograms.listener.GuildCreateListener;
import net.yetrr.premiumholograms.listener.GuildRemoveListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;

@ModuleInfo(name = "PremiumHolograms", version = "1.0")
public class HologramsPlugin extends JavaPlugin implements Module {

    private static HologramsPlugin instance;

    private PremiumGuildsPlugin premiumGuildsPlugin;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PremiumGuilds")) {
            this.getLogger().warning("Nie znaleziono pluginu PremiumGuilds, dodatek nie moze dzialac prawidlowo!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            this.getLogger().warning("Nie znaleziono pluginu HolographicDisplays, dodatek nie moze dzialac prawidlowo!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.premiumGuildsPlugin = PremiumGuildsAPI.getPremiumGuildsPlugin();
        ModuleInitializer.registerModule(this);
        Config.load(this);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new GuildCreateListener(), this);
        pluginManager.registerEvents(new GuildRemoveListener(), this);

        HologramManager.INSTANCE.createDefaultHolograms();
        registerCommands(new HologramCommand());

        new GuildHologramTask().runTaskTimer(this, Config.HOLOGRAM_SCROLL_TIME * 20, Config.HOLOGRAM_SCROLL_TIME * 20);
    }

    public PremiumGuildsPlugin getPremiumGuildsPlugin() {
        return this.premiumGuildsPlugin;
    }

    public static HologramsPlugin getInstance() {
        return instance;
    }

    private void registerCommands(Command... commands) {
        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer().getPluginManager());
            Arrays.stream(commands).forEach(command -> commandMap.register("premiumholograms:" + command.getName(), command));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        HologramManager.INSTANCE.getHolograms().forEach(hologram -> hologram.getHologram().delete());
    }

}