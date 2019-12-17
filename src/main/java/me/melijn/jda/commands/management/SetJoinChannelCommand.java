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

public class SetJoinChannelCommand extends Command {


    public SetJoinChannelCommand() {
        this.commandName = "setJoinChannel";
        this.description = "Sets a TextChannel where users will be welcomed or bid farewell";
        this.usage = PREFIX + commandName + " <TextChannel | null>";
        this.aliases = new String[]{"sjc", "sjoinchannel"};
        this.category = Category.MANAGEMENT;
        this.needs = new Need[]{Need.GUILD};
        this.id = 35;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            Guild guild = event.getGuild();
            long joinChannelId = event.getVariables().joinChannelCache.get(guild.getIdLong());
            String[] args = event.getArgs().split("\\s+");
            if (args.length > 0 && !args[0].isEmpty()) {
                long id = event.getHelpers().getTextChannelByArgsN(event, args[0]);
                if (id == -1L) {
                    event.reply("Unknown TextChannel");
                } else if (id == 0L) {
                    event.async(() -> {
                        event.getMySQL().removeChannel(guild.getIdLong(), ChannelType.JOIN);
                        event.getVariables().joinChannelCache.invalidate(guild.getIdLong());
                    });
                    long oldChannel = event.getVariables().joinChannelCache.get(guild.getIdLong());
                    event.reply("The JoinChannel has been changed from " +
                            (oldChannel == -1L ? "nothing" : "<#" + oldChannel + ">")
                            + " to nothing by **" + event.getFullAuthorName() + "**"
                    );
                } else {
                    event.async(() -> {
                        event.getMySQL().setChannel(guild.getIdLong(), id, ChannelType.JOIN);
                        event.getVariables().joinChannelCache.put(guild.getIdLong(), id);

                        if (event.getVariables().joinMessages.get(guild.getIdLong()).isEmpty()) {
                            event.getMySQL().setMessage(guild.getIdLong(), "Welcome **%USER%** to our awesome discord server :D", MessageType.JOIN);
                            event.getVariables().joinMessages.put(guild.getIdLong(), "Welcome %USER% to the %GUILDNAME% discord server");
                            event.reply("I've set the default join message :beginner:");
                        }



                        String oldChannel = joinChannelId == -1 ? "nothing" : "<#" + joinChannelId + ">";
                        String newChannel = "<#" + id + ">";
                        event.reply("The JoinChannel has been changed from " + oldChannel + " to " + newChannel + " by **" + event.getFullAuthorName() + "**");
                    });
                }
            } else {
                if (joinChannelId != -1)
                    event.reply("Current JoinChannel: <#" + joinChannelId + ">");
                else
                    event.reply("Current JoinChannel is unset");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
