package me.kermx.prismaXPStorage;

import org.bukkit.entity.Player;

public class XPUtils {

    public static int getTotalExperience(Player player){
        int level = player.getLevel();
        float progress = player.getExp();

        int xp = getExperienceForLevel(level);
        xp += Math.round(getExperienceToNext(level) * progress);

        return xp;
    }

    public static int getExperienceForLevel(int level) {
        if (level <= 16) {
            return (int) (level * level + 6 * level);
        } else if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
    }

    public static int getExperienceToNext(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    public static void setTotalExperience(Player player, int xp) {
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);

        if (xp <= 0) return;

        addExperience(player, xp);
    }

    public static void addExperience(Player player, int xp) {
        if (xp <= 0) return;

        int level = player.getLevel();
        int experienceToNextLevel = getExperienceToNext(level);
        float progress = player.getExp();

        int currentXp = Math.round(progress * experienceToNextLevel);
        int remainingXp = xp + currentXp;

        while (remainingXp >= experienceToNextLevel) {
            remainingXp -= experienceToNextLevel;
            level++;
            experienceToNextLevel = getExperienceToNext(level);
        }

        player.setLevel(level);
        if (experienceToNextLevel > 0) {
            player.setExp((float) remainingXp / experienceToNextLevel);
        } else {
            player.setExp(0);
        }
    }
}
