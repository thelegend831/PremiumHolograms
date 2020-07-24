package net.yetrr.premiumholograms.listener;

import net.yetrr.premiumguilds.api.event.guild.GuildRemoveEvent;
import net.yetrr.premiumguilds.api.structure.guild.Guild;
import net.yetrr.premiumholograms.hologram.GuildHologram;
import net.yetrr.premiumholograms.hologram.HologramManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GuildRemoveListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onGuildRemove(GuildRemoveEvent event) {
        Guild guild = event.getGuild();

        GuildHologram guildHologram = HologramManager.INSTANCE.getHologram(guild);
        if (guildHologram != null) {
            guildHologram.getHologram().delete();
            HologramManager.INSTANCE.removeHologram(guildHologram);
        }
    }

}
