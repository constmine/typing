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

import static org.bukkit.Bukkit.getLogger;

public class CommandTyping implements TabExecutor {

    private final Typing plugin;

    public CommandTyping(Plugin plugin) {
        this.plugin = (Typing) plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            getLogger().info("해당 커맨드는 Player만 입력할 수 있습니다.");
        }

        if (canSubCommand(args, "start")) {
            startGame();

        } else if (canSubCommand(args, "stop")) {
            stopGame();
        }

        return false;
    }

    private boolean canSubCommand(String[] args, String command) {
        if(args.length > 0) {
            return args[0].equalsIgnoreCase(command);
        }
        return false;
    }

    private void onClear() {
        AllPlayerAction.clearTitle();
        cf.initialize();
        Bukkit.getScheduler().cancelTasks(plugin);
    }

    private void startGame() {
        //이미 게임 진행중
        if(cf.isGameStart()) {
            AllPlayerAction.broadcast("\n[타자연습] ==자동으로 재시작 합니다.==\n");
            onClear();
        }

        new RandomPlayerPick(plugin);
        cf.broadcastGameState();
    }

    private void stopGame() {
        AllPlayerAction.broadcast("[타자연습] 게임을 중지했습니다.");
        onClear();
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
