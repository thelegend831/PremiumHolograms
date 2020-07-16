package net.yetrr.premiumholograms.hologram;

import com.google.common.collect.ImmutableList;
import net.yetrr.premiumguilds.api.PremiumGuildsPlugin;
import net.yetrr.premiumguilds.api.structure.guild.Guild;
import net.yetrr.premiumguilds.api.structure.guild.GuildManager;
import net.yetrr.premiumguilds.api.system.setting.Setting;
import net.yetrr.premiumguilds.api.util.LocationUtil;
import net.yetrr.premiumguilds.api.util.common.Cooldown;
import net.yetrr.premiumholograms.HologramsPlugin;
import net.yetrr.premiumholograms.config.Config;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public enum HologramManager {

    INSTANCE;

    private final List<GuildHologram> hologramList = new ArrayList<>();

    private final Cooldown<UUID> relocateCooldown = new Cooldown<>();

    public void createDefaultHolograms() {
        PremiumGuildsPlugin plugin = HologramsPlugin
                .getInstance()
                .getPremiumGuildsPlugin();

        GuildManager guildManager = plugin.getGuildManager();

        for (Guild guild : guildManager.getGuilds()) {
            Location location;

            if (guild.getSetting("hologram-location") != null) {
                Setting<String> setting = guild.getSetting("hologram-location");

                if (setting.getValue().equalsIgnoreCase("null")) {
                    continue;
                }

                location = LocationUtil.locationFromString(setting.getValue());
            } else {
                location = guild.getRegion()
                        .getCenter()
                        .clone()
                        .add(Config.DEFAULT_HOLOGRAM_X_OFFSET, Config.DEFAULT_HOLOGRAM_Y_OFFSET, Config.DEFAULT_HOLOGRAM_Z_OFFSET);

            }

            createHologram(guild, location);
        }
    }

    public void createHologram(Guild guild, Location location) {
        GuildHologram hologram = new GuildHologram(guild, location);
        this.hologramList.add(hologram);
    }

    public void removeHologram(GuildHologram hologram) {
        hologram.getHologram().delete();
        this.hologramList.remove(hologram);
    }

    public boolean isGuildOnHologramCooldown(Guild guild) {
        return this.relocateCooldown.isOnCooldown(guild.getUniqueId());
    }

    public void addGuildOnCooldown(Guild guild) {
        this.relocateCooldown.putOnCooldown(guild.getUniqueId(), Config.HOLOGRAM_RELOCATE_COOLDOWN, TimeUnit.SECONDS);
    }

    public double getCooldownRemainingTime(Guild guild) {
        return this.relocateCooldown.getRemainingTime(guild.getUniqueId()) / 1000D;
    }

    public void relocateHologram(GuildHologram hologram, Location newLocation) {
        hologram.getHologram().teleport(newLocation);
    }

    public GuildHologram getHologram(Guild guild) {
        return this.hologramList.stream()
                .filter(hologram -> hologram.getGuild().equals(guild))
                .findFirst()
                .orElse(null);
    }

    public List<GuildHologram> getHolograms() {
        return ImmutableList.copyOf(this.hologramList);
    }

}
