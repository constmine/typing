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


/**
 * 구조
 * 처음으로 PlayerChatEvent를 통하여 객체 생성을 함.
 * TypingGame() -> onStart() -> setTimer() ->
 *
 * TimeOut되는 경우
 * (Timer시간이 3초 남았을때) lastTimer() -> (시간이 지남) timeOut() -> (새롭게 시작) RandomPlayerPick객체 생성.
 *
 * 중간에 다른 Player가 맞추는 경우
 * PlayerChatEvent에서 changeOwner()를 호출.
 * 이미 진행중인 다른 scheduler들이 있으므로 중간에 끊어줌 -> cancelTypingGameScheduler();
 *
 * ----
 * Round가 끝나는 경우
 * finish()를 통해 조건 확인. Round는 TimeOut이 되는 경우와 다른 Player가 맞추는 경우에 줄어들게 함. -> GameEnd()
 *
 */
public class TypingGame {

    private final Typing plugin;        //Scheduler를 실행하기 위함.
    private String text;                //PlayerChatEvent를 통해 받아온 문장.
    private final ArrayList<RepeatingScheduler> schedulers = new ArrayList<>();

    private int time = cf.getTime();
    private int round = cf.getRound();

    private final int THREE_SECOND = 3000;

    public TypingGame(Plugin plugin, String text) {
        this.plugin = (Typing) plugin;
        this.text = text;

        onStart(text);
    }

    public void onStart(String text) {
        this.text = text;

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
        roundEnd();

        AllPlayerAction.showTitle(ChatColor.RED + "TIME OUT!", text, 0, THREE_SECOND, 0);

        if(!isGameEnd()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> new RandomPlayerPick(plugin), 60);
        } else {
            finish();
        }
    }

    public void changeOwner(Player player) {
        roundEnd();
        cancelTypingGameScheduler();

        AllPlayerAction.showTitle(ChatColor.GOLD + player.getName(), "", 0, THREE_SECOND, 0);
        if(!isGameEnd()) {

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                resetTime();
                cf.setOwnerPlayer(player);
                RandomPlayerPick.gameTitle(player);

            }, 60L);

        } else {
            finish();
        }
    }

    private void finish() {
        Bukkit.getScheduler().cancelTasks(plugin);
        cf.initialize();

        Bukkit.getScheduler().runTaskLater(plugin, () -> AllPlayerAction.showTitle(ChatColor.GREEN + "게임 종료!", "", 0, 1000 * 5, 0), 60L);
        round = cf.getRound();
    }


    public void cancelTypingGameScheduler() {
        for(RepeatingScheduler scheduler : schedulers) {
            scheduler.cancel();
            cf.initialize();
        }
    }

    private void resetTime() { time = cf.getTime(); }


    /**
     * GameEnd == Round <= 0
     * @return 남은 Round가 있다면 true 없다면 false
     */
    private boolean isGameEnd() { return round <= 0; }

    /**
     * 한 라운드가 끝났을때, round값을 줄임.
     */
    private void roundEnd() {
        round -= 1;
        remaining_Round();
        resetTime();
    }

    private void remaining_Round() {
        Bukkit.broadcast(Component.text(
                "==== [ 타자 연습 ] ====" +
                        "\n남은 라운드 : " + round + "\n"
        ));
    }

}
