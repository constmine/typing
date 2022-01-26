package com.github.constmine.plugin.typing.game;

import com.github.constmine.plugin.typing.Typing;
import com.github.constmine.plugin.typing.scheduler.RepeatingScheduler;
import com.github.constmine.plugin.typing.util.AllPlayerAction;
import com.github.constmine.plugin.typing.util.cf;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class TypingGame {

    private final Player player;
    private final Typing plugin;
    private String text;
    private final ArrayList<RepeatingScheduler> schedulers = new ArrayList<>();

    private int time;
    private int round;

    public TypingGame(Plugin plugin, String text, Player player) {
        this.plugin = (Typing) plugin;
        this.text = text;
        this.player = player;

        onStart(text);
    }

    public void onStart(String text) {
        this.text = text;
        time = cf.getTime();
        round = cf.getRound();

        AllPlayerAction.showTitle(ChatColor.YELLOW + "" + time, text, 0, 1000 * 2, 0);
        setTimer();
    }

    private void setTimer() {
        schedulers.add(new RepeatingScheduler(plugin, 20L, 20L) {
            @Override
            public void run() {
                time -= 1;
                if(time > 3) {
                    AllPlayerAction.showTitle(ChatColor.YELLOW + "" + time, text, 0, 1000 * 2, 0);
                } else if(time > 0) {
                    lastTimer();
                } else {                                                    //게임 종료 지점. (Time out)
                    cancelTypingGameScheduler();
                    timeOut();
                }

            }
        });
    }

    private void lastTimer() {
        final int[] tick = {10};
        schedulers.add(new RepeatingScheduler(plugin, 0L, 2L) {
            @Override
            public void run() {
                if(tick[0] == 0) {
                    cancel();
                } else if(tick[0] % 2 == 0) {
                    AllPlayerAction.showTitle(ChatColor.GOLD + "" + time, text, 0, (long) (1000 * 0.2), 0);
                } else if(tick[0] % 2 == 1){
                    AllPlayerAction.showTitle(ChatColor.RED + "" + time, text, 0, (long) (1000 * 0.2), 0);
                }
                tick[0] -= 1;
            }
        });
    }

    private void timeOut() {
        AllPlayerAction.showTitle(ChatColor.RED + "TIME OUT!", text, 0, 1000 * 3, 0);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
               new RandomPlayerPick(plugin, player);
            }
        }, 60);

        isGameEnd();
    }

    public void changeOwner(Player player) {
        cancelTypingGameScheduler();
        AllPlayerAction.showTitle(ChatColor.GOLD + player.getName(), "", 0, 1000 * 3, 0);

        isGameEnd();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            time = cf.getTime();
            cf.initialize();

            cf.setOwnerPlayer(player);
            cf.setGameStart(true);

            RandomPlayerPick.gameTitle(player);

        }, 60L);
    }

    private void isGameEnd() {
        round = round - 1;
        if (round == 0) {
            Bukkit.getScheduler().cancelTasks(plugin);
            cf.initialize();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                AllPlayerAction.showTitle(ChatColor.GREEN + "게임 종료!", "", 0, 1000 * 5, 0);
            }, 60L);
        } else {
            resetTime();
            Bukkit.broadcast(Component.text(
                    "==== [ 타자 연습 ] ====" +
                            "\n남은 라운드 : " + round
            ));
        }
    }


    public void cancelTypingGameScheduler() {
        for(RepeatingScheduler scheduler : schedulers) {
            scheduler.cancel();
            cf.initialize();
        }
    }

    private void resetTime() {
        time = cf.getTime();
    }

}
