package com.balugaq.summaryhelper.utils;

import com.balugaq.summaryhelper.implementation.SummaryHelper;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@UtilityClass
public class KeyUtil {

    public static @NotNull NamespacedKey newKey(@NotNull String key) {
        return new NamespacedKey(SummaryHelper.getInstance(), key);
    }

    public static @NotNull NamespacedKey newKey(@NotNull String pluginName, @NotNull String key) {
        return new NamespacedKey(pluginName, key);
    }

    public static @NotNull NamespacedKey newKey(@NotNull Plugin plugin, @NotNull String key) {
        return new NamespacedKey(plugin, key);
    }
}
