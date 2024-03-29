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

package me.melijn.jda.commands.management;

import com.google.common.collect.Sets;
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;
import net.dv8tion.jda.core.Permission;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static me.melijn.jda.Melijn.PREFIX;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        this.commandName = "purge";
        this.description = "Deletes messages";
        this.usage = PREFIX + commandName + " <1 - 1000>";
        this.category = Category.MANAGEMENT;
        this.aliases = new String[]{"prune", "spurge", "sprune"};
        this.permissions = new Permission[]{
                Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY
        };
        this.extra = "Spurge and Sprune will be silent";
        this.needs = new Need[]{Need.GUILD};
        this.id = 55;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            if (event.getArgs().isEmpty() || !args[0].matches("^([1-9][0-9]{0,2}|1000)$")) {
                event.sendUsage(this, event);
                return;
            }

            int toPurgeAmount = Integer.parseInt(args[0]);
            event.getTextChannel().getIterableHistory().takeAsync(toPurgeAmount + 1).thenAccept(messages -> {
                messages.forEach(message -> event.getVariables().purgedMessageDeleter.put(message.getIdLong(), event.getAuthorId()));
                event.getTextChannel().purgeMessages(messages);

                Set<String> silent = Sets.newHashSet("sprune", "spurge");
                if (silent.contains(event.getExecutor().toLowerCase())) return;
                event.getTextChannel().sendMessage("**Done**").queue(m -> {
                    m.delete().queueAfter(3, TimeUnit.SECONDS);
                    event.getVariables().botDeletedMessages.add(m.getIdLong());
                });
            });
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
