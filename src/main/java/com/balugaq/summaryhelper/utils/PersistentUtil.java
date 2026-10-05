/**
 * MIT License
 *
 * Copyright (c) 2024-2026 balugaq
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.balugaq.summaryhelper.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@UtilityClass
public class PersistentUtil {
    public static <T, Z> Z get(@Nullable ItemStack itemStack, @NotNull PersistentDataType<T, Z> dataType, @NotNull NamespacedKey key) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(key, dataType);
    }

    public static <T, Z> void set(@Nullable ItemStack itemStack, @NotNull PersistentDataType<T, Z> dataType, @NotNull NamespacedKey key, @NotNull Z value) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        meta.getPersistentDataContainer().set(key, dataType, value);
        itemStack.setItemMeta(meta);
    }

    public static void remove(@Nullable ItemStack itemStack, @NotNull NamespacedKey key) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        meta.getPersistentDataContainer().remove(key);
        itemStack.setItemMeta(meta);
    }

    public static <T, Z> boolean has(@Nullable ItemStack itemStack, @NotNull PersistentDataType<T, Z> dataType, @NotNull NamespacedKey key) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(key, dataType);
    }

    public static <T, Z> Z getOrDefault(ItemStack itemStack, @NotNull PersistentDataType<T, Z> dataType, @NotNull NamespacedKey key, Z defaultValue) {
        Z value = get(itemStack, dataType, key);
        return value == null ? defaultValue : value;
    }

    public static <T, Z> Z get(@Nullable PersistentDataContainer container, @NotNull PersistentDataType<T, Z> dataType, @NotNull NamespacedKey key) {
        if (container == null) {
            return null;
        }

        return container.get(key, dataType);
    }

    public static <T, Z> void set(@Nullable PersistentDataContainer container, @NotNull PersistentDataType<T, Z> dataType, @NotNull NamespacedKey key, @NotNull Z value) {
        if (container == null) {
            return;
        }

        container.set(key, dataType, value);
    }

    public static void remove(@Nullable PersistentDataContainer container, @NotNull NamespacedKey key) {
        if (container == null) {
            return;
        }

        container.remove(key);
    }

    public static <T, Z> boolean has(@Nullable PersistentDataContainer container, @NotNull PersistentDataType<T, Z> dataType, @NotNull NamespacedKey key) {
        if (container == null) {
            return false;
        }

        return container.has(key, dataType);
    }

    public static <T, Z> Z getOrDefault(PersistentDataContainer container, @NotNull PersistentDataType<T, Z> dataType, @NotNull NamespacedKey key, Z defaultValue) {
        Z value = get(container, dataType, key);
        return value == null ? defaultValue : value;
    }
}
