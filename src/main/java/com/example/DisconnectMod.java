package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class DisconnectMod implements ClientModInitializer {
    
    private static KeyBinding disconnectKeybind;

    @Override
    public void onInitializeClient() {
        System.out.println("[DisconnectMod] Loaded! Press F2 to disconnect.");
        
        disconnectKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.disconnectmod.disconnect",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F2,
            "category.disconnectmod"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (disconnectKeybind.wasPressed()) {
                if (client.getCurrentServerEntry() != null) {
                    System.out.println("[DisconnectMod] Disconnecting from: " + client.getCurrentServerEntry().address);
                    
                    if (client.world != null) {
                        client.world.disconnect();
                    }
                    client.disconnect();
                    
                    client.execute(() -> {
                        try { Thread.sleep(50); } catch (Exception e) {}
                        client.execute(() -> {
                            client.setScreen(new net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen(null));
                        });
                    });
                    
                    if (client.player != null) {
                        client.player.sendMessage(Text.literal("§c[DisconnectMod]§r Disconnected! Reconnect menu opened."), false);
                    }
                }
            }
        });
    }
}
