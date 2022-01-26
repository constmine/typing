package com.github.constmine.plugin.typing.util;

import com.github.constmine.plugin.typing.Typing;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Config 설정
 */
public class cf {
    static FileConfiguration config = Typing.config;

    /**
     * config파일의 Owner Player를 가져옵니다.
     * @return Player - (Owner) || null
     */
    public static @Nullable Player getOwnerPlayer() {
        String PlayerName = config.getString("Owner");

        for(Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerName != null && PlayerName.equalsIgnoreCase(player.getName())) {
                return player;
            }
        }

        return null;
    }

    /**
     * config파일의 Owner Player를 설정합니다
     * @param player (Owner)
     */
    public static void setOwnerPlayer(@Nullable Player player) {
        if (player != null) {
            config.set("Owner", player.getName());
        } else {
            config.set("Owner", null);
        }
    }

    /**
     * config파일의 게임 진행상태를 바꿉니다.
     * @param start true -> 진행 / false -> 중단
     */
    public static void setGameStart(boolean start) {
        config.set("Game", start);
    }

    /**
     * config파일의 Game진행 상태를 가져옵니다.
     * @return true / false
     */
    public static boolean isGameStart() {
        return config.getBoolean("Game");
    }

    public static void changeGameStart() {
        cf.setGameStart(!cf.isGameStart());
    }

    public static void setText(String text) {
        config.set("Text", text);
    }

    public static String getText() {
        return config.getString("Text");
    }

    public static boolean hasText() {
        return config.getString("Text") != null;
    }

    public static int getTime() {
        return config.getInt("Time");
    }

    //기본 1초부터 30초 사이만
    public static boolean setTime(int time) {
        if(time >= 1 && time <= 30) {
            config.set("Time", time);
            return true;
        }
        return false;
    }

    public static int getRound() {
        return config.getInt("Round");
    }

    //기본 1라운드 부터 20라운드 사이만
    public static boolean setRound(int round) {
        if(round >= 1 && round <= 20) {
            config.set("Round", round);
            return true;
        }
        return false;
    }

    public static void initialize() {
        setOwnerPlayer(null);
        setGameStart(false);
        setText(null);
    }

    public static Object get(String path) {
        return config.get(path);
    }


    public static void set(String path, Object value) {
        config.set(path, value);
    }

    public static void broadcastGameState() {
        Bukkit.broadcast(Component.text(
                "\n==== [ 타자 연습 ] ====" +
                        "\n현재 라운드 수 : " + getRound() +
                        "\n현재 제한 시간 : " + getTime() + "\n"
        ));
    }

}
