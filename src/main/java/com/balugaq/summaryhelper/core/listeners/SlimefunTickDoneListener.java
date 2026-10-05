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
package com.balugaq.summaryhelper.core.listeners;

import com.balugaq.summaryhelper.api.CachedRequest;
import com.balugaq.summaryhelper.implementation.SummaryHelper;
import com.balugaq.summaryhelper.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unchecked", "unused"})
public class SlimefunTickDoneListener {
    public final AtomicInteger tickPassed = new AtomicInteger(0);

    public SlimefunTickDoneListener() {
        Bukkit.getScheduler().runTaskTimer(SummaryHelper.getInstance(), () -> {
            AtomicInteger tickPassed = (AtomicInteger) ReflectionUtil.getValue(Slimefun.getProfiler(), "ticksPassed");
            if (tickPassed == null) {
                SummaryHelper.getInstance().getLogger().warning("Failed to get ticksPassed from Slimefun's Profiler.");
                return;
            }
            if (this.tickPassed.get() != tickPassed.get()) {
                this.tickPassed.set(tickPassed.get());
                onSlimefunTickDone(tickPassed.get());
            }
        }, 20, 2);
    }

    public void onSlimefunTickDone(int tickPassed) {
        Object timingsObject = ReflectionUtil.getValue(Slimefun.getProfiler(), "timings");
        Map<Object, Long> timingsMap = (Map<Object, Long>) timingsObject;
        assert timingsMap != null;
        Map<Location, Long> timings = new HashMap<>();
        for (Map.Entry<Object, Long> entry : timingsMap.entrySet()) {
            Object block = entry.getKey();
            long time = entry.getValue();
            Long position = (Long) ReflectionUtil.getValue(block, "position");
            assert position != null;
            World world = (World) ReflectionUtil.getValue(block, "world");
            int x = (int) (position >> 38);
            int y = (int) (position & 4095L);
            int z = (int) (position << 26 >> 38);
            Location location = new Location(world, x, y, z);
            timings.put(location, time);
        }

        int handledRequests = 0;
        while (SummaryHelper.getInstance().getRequestCount() > 0) {
            CachedRequest request = SummaryHelper.getInstance().pollRequest();
            if (request != null) {
                request.getCallback().accept(timings);
                handledRequests++;
            }

            if (handledRequests > 4000) {
                SummaryHelper.getInstance().getLogger().warning("Too many requests handled in one tick.");
                SummaryHelper.getInstance().getLogger().warning("Left requests: " + SummaryHelper.getInstance().getRequestCount());
                break;
            }
        }
    }
}
