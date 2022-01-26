package com.github.constmine.plugin.typing;

import com.github.constmine.plugin.typing.command.CommandTyping;
import com.github.constmine.plugin.typing.event.PlayerChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Typing extends JavaPlugin {

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        Bukkit.getPluginManager().registerEvents(new PlayerChatEvent(this), this);
        getCommand("typing").setExecutor(new CommandTyping(this));
    }

    @Override
    public void onDisable() {

    }
}
