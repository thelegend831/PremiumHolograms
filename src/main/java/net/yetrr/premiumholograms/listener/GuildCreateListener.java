package net.yetrr.premiumholograms.listener;

import net.yetrr.premiumguilds.api.event.GuildCreateEvent;
import net.yetrr.premiumguilds.api.structure.guild.Guild;
import net.yetrr.premiumholograms.config.Config;
import net.yetrr.premiumholograms.hologram.HologramManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GuildCreateListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onGuildCreate(GuildCreateEvent event) {
        Guild guild = event.getGuild();

        if (Config.CREATE_ON_GUILD_CREATE) {
            Location location = guild.getRegion()
                    .getCenter()
                    .clone()
                    .add(Config.DEFAULT_HOLOGRAM_X_OFFSET, Config.DEFAULT_HOLOGRAM_Y_OFFSET, Config.DEFAULT_HOLOGRAM_Z_OFFSET);

            HologramManager.INSTANCE.createHologram(guild, location);
        }
    }

}
