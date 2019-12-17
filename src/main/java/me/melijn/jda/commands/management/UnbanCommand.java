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
import net.dv8tion.jda.core.Permission;

import static me.melijn.jda.Melijn.PREFIX;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        this.commandName = "unban";
        this.description = "Unbans a banned user";
        this.usage = PREFIX + commandName + " <user>";
        this.category = Category.MANAGEMENT;
        this.needs = new Need[]{Need.GUILD};
        this.permissions = new Permission[]{
                Permission.MESSAGE_EMBED_LINKS,
                Permission.BAN_MEMBERS,
                Permission.MESSAGE_HISTORY
        };
        this.id = 82;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            if (event.getArgs().isEmpty()) {
                event.sendUsage(this, event);
                return;
            }
            event.getHelpers().retrieveUserByArgsN(event, args[0], user -> {
                if (user == null) {
                    event.reply("Unknown user");
                    return;
                }
                event.async(() -> {
                    String reason = args.length > 1 ? event.getArgs().replaceFirst(args[0], "") : "N/A";
                    if (reason.substring(0, 1).equalsIgnoreCase(" ")) reason = reason.replaceFirst("\\s+", "");
                    if (event.getMySQL().unban(user, event.getGuild(), event.getAuthor(), reason)) {
                        event.getMessage().addReaction("\u2705").queue();
                    } else {
                        event.getMessage().addReaction("\u274C").queue();
                    }
                });
            });
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
