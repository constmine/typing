package com.github.constmine.plugin.typing.game;

import com.github.constmine.plugin.typing.Typing;
import com.github.constmine.plugin.typing.scheduler.RepeatingScheduler;
import com.github.constmine.plugin.typing.util.AllPlayerAction;
import com.github.constmine.plugin.typing.util.cf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


public class RandomPlayerPick {

    private final Typing plugin;
    private List<Player> gamePlayers;
    private int count;

    public RandomPlayerPick(Plugin plugin) {
        this.plugin = (Typing) plugin;
        onPick();
    }

    /**
     * 실행부분
     */
    public void onPick() {
        onRandom();
        this.gamePlayers = sortPlayerList();
        onShow();
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
     * 같은 월드에 있는 Player -> player.getWorld().getPlayers() -> List<Player>
     * 같은 서버에 있는 Player -> Bukkit.getOnlinePlayers -> Collection<? extends Player>
     */
    private List<Player> sortPlayerList() {
        Collection<? extends Player> list = Bukkit.getOnlinePlayers();
        List<Player> playerList = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            if(!list.iterator().hasNext()) { list = Bukkit.getOnlinePlayers(); }
            playerList.add(list.iterator().next());
        }

        return playerList;
    }

    private void onShow() {
        final int[] i = {0};
        new RepeatingScheduler(plugin, 0L, 2L) {
            @Override
            public void run() {
                AllPlayerAction.stopSound(Sound.ENTITY_PLAYER_LEVELUP);
                if(i[0] < count - 1) {
                    AllPlayerAction.showTitle(gamePlayers.get(i[0]).getName(), "", 0, 100,0);
                    i[0] += 1;

                } else {
                    lastTitle(gamePlayers.get(i[0]));
                    cf.setOwnerPlayer(gamePlayers.get(i[0]));
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
                if (tick[0] <= 0) {
                    gameTitle(player);
                    cf.changeGameStart();
                    cancel();
                } else {
                    AllPlayerAction.showTitle(randomRGB(player.getName()), Component.text(""), 0, 100, 0);
                    tick[0] -= 1;
                }
            }
        };
    }

    private TextComponent randomRGB(String text) {
        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();

        return Component.text(text).color(TextColor.color(r, g, b));
    }

    public static void gameTitle(Player player) {
        AllPlayerAction.showTitle(ChatColor.AQUA + player.getName(), "문장을 입력하세요!", 0L, 1000 * 60 * 60, 0L);
    }

}
