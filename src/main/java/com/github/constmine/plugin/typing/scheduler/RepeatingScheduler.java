package com.github.constmine.plugin.typing.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class RepeatingScheduler implements Runnable {

    private int taskId;

    public RepeatingScheduler(JavaPlugin plugin, long delay, long period) {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
