package net.yetrr.premiumholograms.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.yetrr.premiumguilds.api.structure.guild.Guild;
import net.yetrr.premiumholograms.HologramsPlugin;
import org.bukkit.Location;

public class GuildHologram {

    private final Hologram hologram;

    private final Guild guild;

    private int page = 0;

    public GuildHologram(Guild guild, Location location) {
        this.hologram = HologramsAPI.createHologram(HologramsPlugin.getInstance(), location);
        this.guild = guild;
    }

    public Hologram getHologram() {
        return this.hologram;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public int getPage() {
        return this.page;
    }

    public void addPage() {
        this.page++;
    }

    public void setPage(int page) {
        this.page = page;
    }

}