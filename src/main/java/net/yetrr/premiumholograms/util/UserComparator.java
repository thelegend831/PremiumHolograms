package net.yetrr.premiumholograms.util;

import net.yetrr.premiumguilds.api.structure.user.User;

import java.util.Comparator;
import java.util.Set;

public enum UserComparator implements Comparator<User> {

    POINTS, KILLS, DEATHS, ASSISTS, KDR, KDRA;

    public User getBestUser(Set<User> users) {
        return users.stream()
                .sorted(this)
                .limit(1)
                .findFirst()
                .orElse(null);
    }

    @Override
    public int compare(User first, User second) {
        switch (this) {
            case POINTS:
                return first.getRank().getPoints() - second.getRank().getPoints();
            case KILLS:
                return first.getRank().getKills() - second.getRank().getKills();
            case DEATHS:
                return first.getRank().getDeaths() - second.getRank().getDeaths();
            case ASSISTS:
                return first.getRank().getAssists() - second.getRank().getAssists();
            case KDR:
                return (int) (first.getRank().getKDRatio() - second.getRank().getKDRatio());
            case KDRA:
                return (int) (first.getRank().getKDARatio() - second.getRank().getKDARatio());
        }

        return 0;
    }

}
