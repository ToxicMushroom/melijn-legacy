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

import me.melijn.jda.blub.*;
import net.dv8tion.jda.core.entities.Guild;

import static me.melijn.jda.Melijn.PREFIX;

public class SetLeaveChannelCommand extends Command {

    public SetLeaveChannelCommand() {
        this.commandName = "setLeaveChannel";
        this.description = "Sets the leave channel";
        this.usage = PREFIX + commandName + " [TextChannel | null]";
        this.aliases = new String[]{"sleavec"};
        this.needs = new Need[]{Need.GUILD};
        this.category = Category.MANAGEMENT;
        this.id = 112;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            Guild guild = event.getGuild();
            long leaveChannelId = event.getVariables().leaveChannelCache.get(guild.getIdLong());
            String[] args = event.getArgs().split("\\s+");
            if (args.length > 0 && !args[0].isEmpty()) {
                long id = event.getHelpers().getTextChannelByArgsN(event, args[0]);
                if (id == -1L) {
                    event.reply("Unknown TextChannel");
                } else if (id == 0L) {
                    event.async(() -> {
                        event.getMySQL().removeChannel(guild.getIdLong(), ChannelType.LEAVE);
                        event.getVariables().leaveChannelCache.invalidate(guild.getIdLong());
                    });
                    long oldChannel = event.getVariables().leaveChannelCache.get(guild.getIdLong());
                    event.reply("The LeaveChannel has been changed from " +
                            (oldChannel == -1L ? "nothing" : "<#" + oldChannel + ">")
                            + " to nothing by **" + event.getFullAuthorName() + "**"
                    );
                } else {
                    event.async(() -> {
                        event.getMySQL().setChannel(guild.getIdLong(), id, ChannelType.LEAVE);
                        event.getVariables().leaveChannelCache.put(guild.getIdLong(), id);

                        if (event.getVariables().leaveMessages.get(guild.getIdLong()).isEmpty()) {
                            event.getMySQL().setMessage(guild.getIdLong(), "**%USERNAME%** left us :C", MessageType.LEAVE);
                            event.getVariables().leaveMessages.put(guild.getIdLong(), "**%USERNAME%** left us :C");
                            event.reply("I've set the default leave message :beginner:");
                        }


                        String oldChannel = leaveChannelId == -1 ? "nothing" : "<#" + leaveChannelId + ">";
                        String newChannel = "<#" + id + ">";
                        event.reply("The LeaveChannel has been changed from " + oldChannel + " to " + newChannel + " by **" + event.getFullAuthorName() + "**");
                    });
                }
            } else {
                if (leaveChannelId != -1)
                    event.reply("Current LeaveChannel: <#" + leaveChannelId + ">");
                else
                    event.reply("Current LeaveChannel is unset");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
