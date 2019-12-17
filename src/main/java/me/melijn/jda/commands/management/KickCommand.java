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
import net.dv8tion.jda.core.entities.User;

import static me.melijn.jda.Melijn.PREFIX;

public class KickCommand extends Command {

    public KickCommand() {
        this.commandName = "kick";
        this.description = "Kicks a member from your server and sends a message with information about the kick to that member";
        this.usage = PREFIX + commandName + " <member> [reason]";
        this.category = Category.MANAGEMENT;
        this.extra = "the bot will dm the reason to the target if one is provided";
        this.needs = new Need[]{Need.GUILD, Need.ROLE};
        this.permissions = new Permission[]{Permission.KICK_MEMBERS, Permission.MESSAGE_HISTORY};
        this.id = 52;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            if (args.length == 0 || args[0].isEmpty()) {
                event.sendUsage(this, event);
                return;
            }

            User target = event.getHelpers().getUserByArgsN(event, args[0]);
            String reason = event.getArgs().replaceFirst(args[0] + "\\s+|" + args[0], "");
            if (target == null) {
                event.reply("This user isn't a member of this guild");
                return;
            }
            if (event.getGuild().getMember(target) != null) {
                event.async(() -> {
                    if (event.getHelpers().canNotInteract(event, target)) return;
                    if (reason.length() <= 1000 && event.getMySQL().addKick(event.getAuthor(), target, event.getGuild(), reason)) {
                        event.getMessage().addReaction("\u2705").queue();
                    } else {
                        event.getMessage().addReaction("\u274C").queue();
                    }
                });

            } else {
                event.reply("Unknown user");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
