package net.skillz.init;

import java.io.FileWriter;
import java.io.IOException;

import net.skillz.screen.LevelScreen;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class KeyInit {
    public static KeyBinding screenKey = new KeyBinding("id.skillz.openskillscreen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.skillz.keybind");
    public static KeyBinding devKey = new KeyBinding("id.skillz.dev", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8, "category.skillz.keybind");

    public static void init() {
        // Registering
        KeyBindingHelper.registerKeyBinding(screenKey);
        // Callback
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (screenKey.wasPressed()) {
                client.setScreen(new LevelScreen());
                return;
            }
        });
    }

    public static void writeId(String string) {
        try (FileWriter idFile = new FileWriter("idlist.json", true)) {
            idFile.append("\"" + string + "\",");
            idFile.append(System.lineSeparator());
        } catch (IOException e) {
        }
    }

}