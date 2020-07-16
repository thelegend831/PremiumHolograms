package net.yetrr.premiumholograms.command;

import net.yetrr.premiumguilds.api.PremiumGuildsPlugin;
import net.yetrr.premiumguilds.api.data.configuration.MessagesConfig;
import net.yetrr.premiumguilds.api.structure.guild.Guild;
import net.yetrr.premiumguilds.api.structure.guild.GuildDao;
import net.yetrr.premiumguilds.api.structure.user.User;
import net.yetrr.premiumguilds.api.structure.user.UserManager;
import net.yetrr.premiumguilds.api.system.setting.Setting;
import net.yetrr.premiumguilds.api.system.setting.SettingFactory;
import net.yetrr.premiumguilds.api.util.ChatUtil;
import net.yetrr.premiumguilds.api.util.LocationUtil;
import net.yetrr.premiumholograms.HologramsPlugin;
import net.yetrr.premiumholograms.config.Config;
import net.yetrr.premiumholograms.hologram.GuildHologram;
import net.yetrr.premiumholograms.hologram.HologramManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class HologramCommand extends Command {

    public HologramCommand() {
        super(Config.HOLOGRAM_COMMAND_NAME);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        PremiumGuildsPlugin premiumGuilds = HologramsPlugin.getInstance().getPremiumGuildsPlugin();
        MessagesConfig messages = premiumGuilds.getMessagesConfig();
        UserManager userManager = premiumGuilds.getUserManager();

        if (!(sender instanceof Player)) {
            ChatUtil.sendMessage(sender, messages.generalOnlyPlayer);
            return true;
        }

        Player player = (Player) sender;

        User user = userManager.getOrCreate(player);
        if (!user.hasGuild()) {
            ChatUtil.sendMessage(sender, messages.errorNotHaveGuild);
            return true;
        }

        if (args.length == 0) {
            ChatUtil.sendMessage(sender, messages.generalCorrectUsage.replace("%usage%", "/" + Config.HOLOGRAM_COMMAND_NAME.toLowerCase() + " <usun/ustaw>"));
            return true;
        }

        Guild guild = user.getGuild();
        if (!(guild.isOwner(user) || guild.isDeputy(user))) {
            ChatUtil.sendMessage(sender, messages.errorGuildNotPermitted);
            return true;
        }

        SettingFactory settingFactory = premiumGuilds.getSettingFactory();

        GuildDao guildDao = premiumGuilds.getPluginDatabase().getGuildDao();

        if (args.length == 1 &&
                args[0].equalsIgnoreCase("relocate") ||
                args[0].equalsIgnoreCase("ustaw") ||
                args[0].equalsIgnoreCase("set")) {
            Guild stay = premiumGuilds.getGuildManager().getGuildByLocation(player.getLocation());

            if (stay == null) {
                ChatUtil.sendMessage(sender, Config.HOLOGRAM_ONLY_AT_REGION);
                return true;
            } else if (!stay.getMembers().contains(user)) {
                ChatUtil.sendMessage(sender, Config.HOLOGRAM_ONLY_AT_OWN_REGION);
                return true;
            }

            if (HologramManager.INSTANCE.isGuildOnHologramCooldown(guild)) {
                ChatUtil.sendMessage(sender, Config.HOLOGRAM_COOLDOWN_MESSAGE
                        .replace("%time%", new DecimalFormat("#.##").format(HologramManager.INSTANCE.getCooldownRemainingTime(guild)))
                );

                return true;
            }

            GuildHologram hologram = HologramManager.INSTANCE.getHologram(guild);

            if (hologram == null) {
                HologramManager.INSTANCE.createHologram(guild, player.getLocation());
            } else {
                HologramManager.INSTANCE.relocateHologram(hologram, player.getLocation());
            }

            Setting<String> setting = guild.getSetting("hologram-location");
            if (setting != null) {
                setting.setValue(LocationUtil.locationToString(player.getLocation()));
            } else {
                setting = settingFactory.createSetting("hologram-location", LocationUtil.locationToString(player.getLocation()));
                guild.addSetting(setting);
            }

            guildDao.updateSettings(guild);
            HologramManager.INSTANCE.addGuildOnCooldown(guild);
            ChatUtil.sendMessage(sender, Config.HOLOGRAM_RELOCATE_MESSAGE);
        } else if (args.length == 1 &&
                args[0].equalsIgnoreCase("usun") ||
                args[0].equalsIgnoreCase("remove") ||
                args[0].equalsIgnoreCase("delete")) {
            GuildHologram hologram = HologramManager.INSTANCE.getHologram(guild);

            if (hologram == null) {
                ChatUtil.sendMessage(sender, Config.HOLOGRAM_NOT_EXISTS);
                return true;
            }

            Setting<String> setting = guild.getSetting("hologram-location");
            if (setting != null) {
                setting.setValue("null");
            } else {
                setting = settingFactory.createSetting("hologram-location", "null");
                guild.addSetting(setting);
            }

            guildDao.updateSettings(guild);
            HologramManager.INSTANCE.removeHologram(hologram);
            ChatUtil.sendMessage(sender, Config.HOLOGRAM_REMOVE_MESSAGE);
        } else {
            ChatUtil.sendMessage(sender, messages.generalCorrectUsage.replace("%usage%", "/" + Config.HOLOGRAM_COMMAND_NAME.toLowerCase() + " <usun/ustaw>"));
            return true;
        }

        return false;
    }

}
