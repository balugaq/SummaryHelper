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

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@UtilityClass
public class SlimefunItemUtil {
    @CanIgnoreReturnValue
    public static @NotNull SlimefunItem registerItem(@NotNull SlimefunItem item, @NotNull SlimefunAddon addon) {
        item.register(addon);
        return item;
    }

    public static void unregisterItem(SlimefunItem item) {
        if (item instanceof Radioactive) {
            Slimefun.getRegistry().getRadioactiveItems().remove(item);
        }

        if (item instanceof GEOResource geor) {
            Slimefun.getRegistry().getGEOResources().remove(geor.getKey());
        }

        Slimefun.getRegistry().getTickerBlocks().remove(item.getId());
        Slimefun.getRegistry().getEnabledSlimefunItems().remove(item);

        Slimefun.getRegistry().getSlimefunItemIds().remove(item.getId());
        Slimefun.getRegistry().getAllSlimefunItems().remove(item);
        Slimefun.getRegistry().getMenuPresets().remove(item.getId());
        Slimefun.getRegistry().getBarteringDrops().remove(item.getItem());
    }
}
