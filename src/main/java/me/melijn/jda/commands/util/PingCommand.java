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

import java.time.temporal.ChronoUnit;

import static me.melijn.jda.Melijn.PREFIX;

public class PingCommand extends Command {

    public PingCommand() {
        this.commandName = "ping";
        this.description = "Shows the bot's ping";
        this.usage = PREFIX + commandName;
        this.category = Category.UTILS;
        this.id = 57;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            event.getChannel().sendMessage("Pinging... ").queue((m) ->
                    m.editMessage("\uD83C\uDFD3 Ping: " +
                            event.getMessage().getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS) + "ms | " + "Websocket: " + event.getJDA().getPing() + "ms").queue());
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
