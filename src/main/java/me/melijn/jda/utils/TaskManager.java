/*
 *     Melijn, a discord bot
 *     Copyright (C) 2019  Merlijn Verstraete
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.melijn.jda.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;
import java.util.function.Function;

public class TaskManager {

    private final MessageHelper messageHelper;
    private final Function<String, ThreadFactory> threadFactory = name -> new ThreadFactoryBuilder().setNameFormat("[" + name + "-Pool-%d] ").build();
    private final ExecutorService executorService = Executors.newCachedThreadPool(threadFactory.apply("Task"));
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10, threadFactory.apply("Rep"));

    public TaskManager(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    public void scheduleRepeating(final Runnable runnable, final long period) {
        scheduledExecutorService.scheduleAtFixedRate(new Task(messageHelper, runnable), 0, period, TimeUnit.MILLISECONDS);
    }

    public void scheduleRepeating(final Runnable runnable, final long initialDelay, final long period) {
        scheduledExecutorService.scheduleAtFixedRate(new Task(messageHelper, runnable), initialDelay, period, TimeUnit.MILLISECONDS);
    }

    public void async(final Runnable runnable) {
        executorService.submit(new Task(messageHelper, runnable));
    }

    public void async(final Runnable runnable, final long after) {
        scheduledExecutorService.schedule(new Task(messageHelper, runnable), after, TimeUnit.MILLISECONDS);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
