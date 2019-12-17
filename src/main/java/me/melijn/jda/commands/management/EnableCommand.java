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

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.melijn.jda.Melijn.PREFIX;

public class EnableCommand extends Command {

    public EnableCommand() {
        this.commandName = "enable";
        this.description = "Enables disabled commands";
        this.usage = PREFIX + commandName + " <commandName | category>";
        this.needs = new Need[]{Need.GUILD};
        this.category = Category.MANAGEMENT;
        this.id = 86;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            long guildId = event.getGuild().getIdLong();
            if (event.getArgs().isEmpty()) {
                event.sendUsage(this, event);
                return;
            }

            Map<Long, List<Integer>> map = new HashMap<>(event.getVariables().disabledGuildCommands);
            List<Integer> buffer = new ArrayList<>(map.containsKey(guildId) ? map.get(guildId) : new ArrayList<>());
            int sizeBefore = buffer.size();
            for (Command cmd : event.getClient().getCommands()) {
                if (cmd.isCommandFor(args[0])) {
                    if (buffer.contains(cmd.getId())) {
                        buffer.remove(Integer.valueOf(cmd.getId()));
                    } else {
                        event.reply("**" + cmd.getCommandName() + "** was already enabled");
                    }
                    break;
                }

                if (cmd.getCategory().toString().equalsIgnoreCase(args[0])) {
                    buffer.remove(Integer.valueOf(cmd.getId()));
                }

            }
            if (buffer.size() == sizeBefore) {
                event.reply("The given command or category was unknown");
            } else {
                event.reply("Successfully enabled **" + args[0] + "**");
                event.async(() -> {
                    event.getMySQL().removeDisabledCommands(guildId, buffer);
                    event.getVariables().disabledGuildCommands.put(guildId, buffer);
                });
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
