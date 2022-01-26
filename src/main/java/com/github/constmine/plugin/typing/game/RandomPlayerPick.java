package com.github.constmine.plugin.typing.game;

import com.github.constmine.plugin.typing.Typing;
import com.github.constmine.plugin.typing.scheduler.RepeatingScheduler;
import com.github.constmine.plugin.typing.util.AllPlayerAction;
import com.github.constmine.plugin.typing.util.cf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class RandomPlayerPick {
    /*Todo
     * 모든 Online플레이어중에 하나를 고름
     *   + Player를 고르는 효과 (색깔 바꾸기, 플레이어 바꾸기, 소리 효과)
     */

    private final Typing plugin;
    private final Player player;
    private int count;

    public RandomPlayerPick(Plugin plugin, Player player) {
        this.plugin = (Typing) plugin;
        this.player = player;

        onRandom();
        onRollPlayer();
    }

    /**
     * 최소 10부터 시작 20까지 난수 생성.
     */
    private void onRandom() {
        Random random = new Random();

        count = random.nextInt(10) + 10;
        cf.set("Count", count);
    }

    /*
     * 쓰레드 지연이 안되서 직접 일일이 작업.
     */
    private void onRollPlayer() {
        List<Player> playerList = player.getWorld().getPlayers();
        List<Player> newlist = new ArrayList<>();
        for(int i = 0, k = 0; i < count; i++) {
            try {
                newlist.add(playerList.get(k));
                k += 1;
            } catch (IndexOutOfBoundsException e) {
                k = 0;
                newlist.add(playerList.get(k));
                k += 1;
            }
        }
        onshow(newlist);
    }

    private void onshow(List<Player> players) {
        final int[] i = {0};
        new RepeatingScheduler(plugin, 0L, 2L) {
            @Override
            public void run() {
                AllPlayerAction.stopSound(Sound.ENTITY_PLAYER_LEVELUP);
                if(i[0] < count - 1) {
                    AllPlayerAction.showTitle(players.get(i[0]).getName(), "", 0, 100,0);
                    i[0] += 1;

                } else {
                    lastTitle(players.get(i[0]));
                    cf.setOwnerPlayer(players.get(i[0]));
                    cancel();
                }
                AllPlayerAction.playSound(Sound.ENTITY_PLAYER_LEVELUP);
            }
        };

    }

    private void lastTitle(Player player) {
        final int[] tick = {10};
        new RepeatingScheduler(plugin, 0L, 2L) {
            @Override
            public void run() {
                Random random = new Random();

                float r = random.nextFloat();
                float g = random.nextFloat();
                float b = random.nextFloat();
                if (tick[0] > 0) {
                    AllPlayerAction.showTitle(Component.text(player.getName()).color(TextColor.color(r, g, b)), Component.text(""), 0, 100, 0);
                } else {
                    gameTitle(player);
                    cf.changeGameStart();
                    cancel();
                }
                tick[0] -= 1;
            }
        };
    }

    public static void gameTitle(Player player) {
        AllPlayerAction.showTitle(ChatColor.AQUA + player.getName(), "문장을 입력하세요!", 0L, 1000 * 60 * 60, 0L);
    }

}
