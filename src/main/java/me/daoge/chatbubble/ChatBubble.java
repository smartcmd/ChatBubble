package me.daoge.chatbubble;

import org.allaymc.api.debugshape.DebugText;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.player.PlayerChatEvent;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.server.Server;
import org.joml.Vector3f;

import java.awt.Color;

public class ChatBubble extends Plugin {

    private static final int BUBBLE_DURATION_TICKS = 60;
    private static final int BUBBLE_UPDATE_PERIOD_TICKS = 1;

    private static final float BUBBLE_HEIGHT_OFFSET = 1.0f;
    private static final float BUBBLE_SCALE = 1.0f;

    @Override
    public void onLoad() {
        this.pluginLogger.info("ChatBubble is loaded!");
    }

    @Override
    public void onEnable() {
        Server.getInstance().getEventBus().registerListener(new ChatBubbleListener(this));
        this.pluginLogger.info("ChatBubble is enabled!");
    }

    @Override
    public void onDisable() {
        this.pluginLogger.info("ChatBubble is disabled!");
    }

    private static Vector3f bubblePosition(EntityPlayer player) {
        var loc = player.getLocation();
        return new Vector3f(
                (float) loc.x(),
                (float) (loc.y() + player.getEyeHeight() + BUBBLE_HEIGHT_OFFSET),
                (float) loc.z()
        );
    }

    public static final class ChatBubbleListener {
        private final Plugin plugin;

        private ChatBubbleListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        private void onPlayerChat(PlayerChatEvent event) {
            if (event.isCancelled()) {
                return;
            }

            var player = event.getPlayer();
            var dimension = player.getDimension();
            if (dimension == null) {
                return;
            }

            var message = event.getMessage();
            if (message == null || message.isBlank()) {
                return;
            }

            var debugText = new DebugText(
                    bubblePosition(player),
                    Color.WHITE,
                    message,
                    BUBBLE_SCALE
            );

            dimension.addDebugShape(debugText);

            var world = dimension.getWorld();
            if (world == null) {
                dimension.removeDebugShape(debugText);
                return;
            }

            var expireTick = world.getTick() + BUBBLE_DURATION_TICKS;
            world.getScheduler().scheduleRepeating(plugin, () -> {
                if (player.isDead() || !player.isSpawned() || player.getDimension() != dimension) {
                    dimension.removeDebugShape(debugText);
                    return false;
                }

                if (world.getTick() >= expireTick) {
                    dimension.removeDebugShape(debugText);
                    return false;
                }

                debugText.setPosition(bubblePosition(player));
                return true;
            }, BUBBLE_UPDATE_PERIOD_TICKS);
        }
    }
}
