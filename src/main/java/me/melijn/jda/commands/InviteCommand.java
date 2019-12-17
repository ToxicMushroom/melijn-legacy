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

package me.melijn.jda.commands;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import net.dv8tion.jda.core.Permission;

import static me.melijn.jda.Melijn.PREFIX;

public class InviteCommand extends Command {

    public InviteCommand() {
        this.commandName = "invite";
        this.description = "The bot will give an awesome link which you have to click";
        this.usage = PREFIX + commandName;
        this.category = Category.DEFAULT;
        this.id = 34;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null) {
            event.reply("With permissions included: https://melijn.com/invite?perms=true\n or without https://melijn.com/invite");
        } else {
            event.getAuthor().openPrivateChannel().queue(channel -> {
                channel.sendMessage("With permissions included: https://melijn.com/invite?perms=true\nor without: https://melijn.com/invite").queue();
                if (event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION)) {
                    event.getMessage().addReaction("\u2705").queue();
                } else if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE))
                    event.reply("Check your dm's");
            }, failed -> {
                event.reply("Your dm's are disabled");
            });
        }
    }
}
