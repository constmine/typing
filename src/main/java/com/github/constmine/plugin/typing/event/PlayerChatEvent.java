package com.github.constmine.plugin.typing.event;

import com.github.constmine.plugin.typing.Typing;
import com.github.constmine.plugin.typing.game.TypingGame;
import com.github.constmine.plugin.typing.util.cf;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PlayerChatEvent implements Listener {

    private final Typing plugin;
    private TypingGame game = null;

    public PlayerChatEvent(Plugin plugin) {
        this.plugin = (Typing) plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {

        if (!cf.isGameStart()) { return; }

        Player player = event.getPlayer();
        String text = changeTextType(event.message());

        if (isOverTyping(text, player)) {
            chatEventCancel(event);
        }

        if (equalOwner(player) && !cf.hasText()) {                                   //player가 Owner이고, 진행중인 text가 없을떄
            cf.setText(text);

            if (!hasTypingGame()) {
                game = new TypingGame(plugin, text);
            } else {
                game.onStart(text);
            }
            event.setCancelled(true);

        } else if (equalOwner(player) && cf.hasText()) {                             //player가 Owner이고, 진행중인 text가 있을때
            player.sendMessage(Component.text("이미 진행자 입니다."));
            event.setCancelled(true);

        } else if (!equalOwner(player) && cf.hasText()) {                            //player가 Owner가 아니고, 진행중인 text가 있을때
            if (equalText(text)) {
                game.changeOwner(player);
            }
        }


    }

    public boolean equalText(String text) {
        return cf.getText().equalsIgnoreCase(text);
    }

    public boolean equalOwner(Player player) {
        return player.equals(cf.getOwnerPlayer());
    }

    public String changeTextType(Component text) {
        return ((TextComponent) text).content();
    }

    public boolean hasTypingGame() {
        return game != null;
    }

    public boolean isOverTyping(String text, Player target) {
        if(text.length() > 16) {
            target.sendMessage(Component.text(ChatColor.RED + "16자 이하로 입력해주세요!"));
            return true;
        }
        return false;
    }

    public void chatEventCancel(AsyncChatEvent event) { event.setCancelled(true); }
}
