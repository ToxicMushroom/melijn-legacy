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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

import static me.melijn.jda.Melijn.PREFIX;

public class SetVerificationChannelCommand extends Command {



    public SetVerificationChannelCommand() {
        this.commandName = "setVerificationChannel";
        this.usage = PREFIX + commandName + " [TextChannel | null]";
        this.description = "Sets the channel in which the members will have to prove that they are not a bot by entering the VerificationCode";
        this.aliases = new String[]{"svc"};
        this.extra = "You can manually approve users by using the verify command";
        this.category = Category.MANAGEMENT;
        this.needs = new Need[]{Need.GUILD};
        this.id = 8;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            Guild guild = event.getGuild();
            long verificationChannelId = event.getVariables().verificationChannelsCache.get(guild.getIdLong());
            String[] args = event.getArgs().split("\\s+");
            if (args.length > 0 && !args[0].isEmpty()) {
                long id = event.getHelpers().getTextChannelByArgsN(event, args[0]);
                if (id == -1L) {
                    event.reply("Unknown TextChannel");
                } else if (id == 0L) {
                    long oldChannel = event.getVariables().verificationChannelsCache.get(guild.getIdLong());
                    event.reply("VerificationChannel has been changed from " + (oldChannel == -1L ? "nothing" : "<#" + oldChannel + ">") + " to nothing");
                    event.async(() -> {
                        event.getMySQL().removeChannel(guild.getIdLong(), ChannelType.VERIFICATION);
                        event.getVariables().verificationChannelsCache.invalidate(guild.getIdLong());
                    });
                } else {
                    if (event.getGuild().getSelfMember().hasPermission(guild.getTextChannelById(id), Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE)) {
                        event.async(() -> {
                            event.getMySQL().setChannel(guild.getIdLong(), id, ChannelType.VERIFICATION);
                            event.getVariables().verificationChannelsCache.put(guild.getIdLong(), id);
                        });
                        String oldChannel = verificationChannelId == -1 ? "nothing" : "<#" + verificationChannelId + ">";
                        String newChannel = "<#" + id + ">";
                        event.reply("VerificationChannel has been changed from " + oldChannel + " to " + newChannel);
                    } else {
                        event.reply("I need to be able to attach files and manage messages in <#" + verificationChannelId + "> in order to set it as my VerificationChannel\nYou might also want to give me kick permissions so bot's get kicked after 3 tries/incorrect answers");
                    }
                }
            } else {
                if (verificationChannelId != -1)
                    event.reply("Current VerificationChannel: <#" + verificationChannelId + ">");
                else
                    event.reply("Current VerificationChannel is unset");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
