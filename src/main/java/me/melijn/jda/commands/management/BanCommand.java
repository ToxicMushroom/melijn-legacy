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


public class BanCommand extends Command {

    public BanCommand() {
        this.commandName = "ban";
        this.description = "Bans a user from your server and sends a message with information about the ban to that user";
        this.usage = PREFIX + commandName + " <user> [reason]";
        this.category = Category.MANAGEMENT;
        this.aliases = new String[]{"permban"};
        this.needs = new Need[]{Need.ROLE, Need.GUILD};
        this.permissions = new Permission[]{
                Permission.MESSAGE_EMBED_LINKS,
                Permission.BAN_MEMBERS,
                Permission.MESSAGE_HISTORY
        };
        this.id = 46;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            if (args.length == 0 || args[0].isEmpty()) {
                event.sendUsage(this, event);
                return;
            }
            event.getHelpers().retrieveUserByArgsN(event, args[0], target -> {
                if (target == null) {
                    event.reply("Unknown user");
                    return;
                }
                if (event.getGuild().isMember(target) && event.getHelpers().canNotInteract(event, target)) return;
                String reason = event.getArgs().replaceFirst(args[0] + "\\s+|" + args[0], "");
                if (reason.length() <= 1000 && event.getMySQL().setPermBan(event.getAuthor(), target, event.getGuild(), reason)) {
                    event.getMessage().addReaction("\u2705").queue();
                } else {
                    event.getMessage().addReaction("\u274C").queue();
                }
            });

        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
