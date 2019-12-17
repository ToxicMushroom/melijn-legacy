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

package me.melijn.jda.commands.util;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.NotificationType;
import net.dv8tion.jda.core.entities.User;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.melijn.jda.Melijn.PREFIX;


public class SetNotifications extends Command {

    public SetNotifications() {
        this.commandName = "setNotifications";
        this.description = "configures vote notification events";
        this.usage = PREFIX + commandName + " [nextVote] [user/info]";
        this.extra = "arg1 -> notification type\narg2 -> either view all notifications of the type or toggle a user on and off";
        this.category = Category.UTILS;
        this.aliases = new String[]{"sn"};
        this.id = 32;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length > 0 && !args[0].isEmpty()) {
            if (!"nextvote".equalsIgnoreCase(args[0])) {
                event.sendUsage(this, event);
                return;
            }
            if (args.length == 1 || args[1].equalsIgnoreCase("info")) {
                List<Long> list = event.getMySQL().getNotifications(event.getAuthorId(), NotificationType.NEXTVOTE);
                final StringBuilder contentBuilder = new StringBuilder("```INI\n");
                AtomicInteger progress = new AtomicInteger(0);
                AtomicInteger contentRowsCount = new AtomicInteger(0);
                if (list.size() == 0) return;
                for (long s : list) {
                    event.getJDA().retrieveUserById(s).queue(user -> {
                        progress.set(progress.get() + 1);
                        if (user != null)
                            contentBuilder.append(contentRowsCount.incrementAndGet() + 1).append(". ").append("[").append(user.getName()).append("#").append(user.getDiscriminator()).append("]\n");
                        if (progress.get() == list.size()) {
                            if (contentRowsCount.get() == 0) contentBuilder.append(";none");
                            contentBuilder.append("```");
                            event.reply("**" + event.getFullAuthorName() + "'s** nextVote notifications\n" + contentBuilder.toString());
                        }
                    });
                }
            } else {
                User user = event.getHelpers().getUserByArgs(event, args[1]);
                List<Long> list = event.getMySQL().getNotifications(user.getIdLong(), NotificationType.NEXTVOTE);
                boolean shouldRemove = false;
                for (long s : list) {
                    if (!shouldRemove && user.getIdLong() == s) {
                        shouldRemove = true;
                    }
                }
                if (shouldRemove) {
                    event.async(() -> {
                        event.getMySQL().removeNotification(event.getAuthorId(), user.getIdLong(), NotificationType.NEXTVOTE);
                        event.reply("NextVote notifications for **" + user.getName() + "#" + user.getDiscriminator() + "** have been **disabled**");
                    });
                } else {
                    event.async(() -> {
                        event.getMySQL().putNotification(event.getAuthorId(), user.getIdLong(), NotificationType.NEXTVOTE);
                        event.reply("NextVote notifications for **" + user.getName() + "#" + user.getDiscriminator() + "** have been **enabled**");
                    });
                }
            }
        } else {
            event.sendUsage(this, event);
        }
    }
}