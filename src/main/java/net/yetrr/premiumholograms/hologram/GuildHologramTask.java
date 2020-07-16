package net.yetrr.premiumholograms.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import net.yetrr.premiumguilds.api.structure.guild.Guild;
import net.yetrr.premiumguilds.api.util.ChatUtil;
import net.yetrr.premiumguilds.api.util.MessageReplacer;
import net.yetrr.premiumholograms.HologramsPlugin;
import net.yetrr.premiumholograms.config.Config;
import net.yetrr.premiumholograms.util.UserComparator;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class GuildHologramTask extends BukkitRunnable {

    @Override
    public void run() {
        HologramManager.INSTANCE
                .getHolograms()
                .forEach(this::update);
    }

    private void update(GuildHologram guildHologram) {
        if (guildHologram.getPage() + 1 > Config.HOLOGRAM_PAGES.size()) {
            guildHologram.setPage(0);
        }

        Guild guild = guildHologram.getGuild();

        MessageReplacer replacer = new MessageReplacer()
                .register("tag", guild.getTag())
                .register("name", guild.getName())
                .register("kills", guild.getRank().getKills())
                .register("deaths", guild.getRank().getDeaths())
                .register("points", guild.getRank().getPoints())
                .register("assists", guild.getRank().getAssists())
                .register("top-points-player", UserComparator.POINTS.getBestUser(guild.getMembers()).getName())
                .register("top-kills-player", UserComparator.KILLS.getBestUser(guild.getMembers()).getName())
                .register("top-assists-player", UserComparator.ASSISTS.getBestUser(guild.getMembers()).getName())
                .register("top-deaths-player", UserComparator.DEATHS.getBestUser(guild.getMembers()).getName())

                .register("position", HologramsPlugin.getInstance()
                        .getPremiumGuildsPlugin()
                        .getRankManager()
                        .getGuildPosition(guild)
                );

        Hologram hologram = guildHologram.getHologram();
        hologram.clearLines();

        AtomicInteger index = new AtomicInteger(0);

        Config.HOLOGRAM_PAGES
                .get(guildHologram.getPage())
                .forEach(string -> hologram.insertTextLine(index.getAndIncrement(), ChatUtil.fixColor(replacer.replace(string))));

        guildHologram.addPage();
    }

}
