package com.github.constmine.plugin.typing.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;

public class AllPlayerAction {

    public static void showTitle(String title, String subtitle, long fadeIn, long stay, long fadeout) {
        for(Player players : Bukkit.getOnlinePlayers()) {
            players.showTitle(Title.title(
                    Component.text(title),
                    Component.text(subtitle),
                    Title.Times.of(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeout))));
        }
    }
    public static void showTitle(Component title, Component subtitle, long fadeIn, long stay, long fadeout) {
        for(Player players : Bukkit.getOnlinePlayers()) {
            players.showTitle(Title.title(
                    title,
                    subtitle,
                    Title.Times.of(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeout))));
        }
    }

    public static void clearTitle() {
        for(Player players : Bukkit.getOnlinePlayers()) {
            players.showTitle(Title.title(
                    Component.text(""),
                    Component.text("")
            ));
        }
    }

    public static void playSound(Sound sound) {
        for(Player players : Bukkit.getOnlinePlayers()) {
            players.playSound(players, sound, 1F, 1F);
        }
    }

    public static void stopSound(Sound sound) {
        for(Player players : Bukkit.getOnlinePlayers()) {
            players.stopSound(sound);
        }
    }

}
