package com.github.constmine.plugin.typing.command;

import com.github.constmine.plugin.typing.Typing;
import com.github.constmine.plugin.typing.game.RandomPlayerPick;
import com.github.constmine.plugin.typing.util.AllPlayerAction;
import com.github.constmine.plugin.typing.util.cf;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandTyping implements TabExecutor {

    private final Typing plugin;

    public CommandTyping(Plugin plugin) {
        this.plugin = (Typing) plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (cf.isGameStart()) {
                        cf.initialize();
                        Bukkit.broadcast(Component.text("\n[타자연습] ==자동으로 재시작 합니다.==\n"));
                    }
                    new RandomPlayerPick(plugin, player);
                    cf.broadcastGameState();

                } else if (args[0].equalsIgnoreCase("stop")) {
                    Bukkit.broadcast(Component.text("[타자연습] 게임을 중지했습니다."));

                    AllPlayerAction.clearTitle();
                    cf.changeGameStart();
                    cf.initialize();
                    Bukkit.getScheduler().cancelTasks(plugin);

                }
            }
        } else {
            Bukkit.getLogger().info("해당 커맨드는 Player만 입력할 수 있습니다.");
        }
        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            List<String> tab = new ArrayList<>();
            tab.add("start");
            tab.add("stop");
            return tab;
        }
        return null;
    }
}
