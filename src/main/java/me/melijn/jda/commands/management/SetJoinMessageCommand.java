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

public class SetJoinMessageCommand extends Command {



    public SetJoinMessageCommand() {
        this.commandName = "setJoinMessage";
        this.description = "Sets the message that will be sent in the WelcomeChannel when a user joins";
        this.usage = PREFIX + commandName + " [message | null]";
        this.extra = "Placeholders:" +
                " `%USER%` = joined user mention //" +
                " `%USERNAME%` = user name //" +
                " `%GUILDNAME%` = your discord server's name //" +
                " `%JOINPOSITION%` = member position";
        this.aliases = new String[]{"sjm"};
        this.category = Category.MANAGEMENT;
        this.needs = new Need[]{Need.GUILD};
        this.id = 37;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            Guild guild = event.getGuild();
            String oldMessage = event.getVariables().joinMessages.get(guild.getIdLong()).isEmpty() ?
                    "nothing" : ("'" + event.getVariables().joinMessages.get(guild.getIdLong()) + "'");
            String newMessage = event.getArgs();
            String[] args = event.getArgs().split("\\s+");
            if (args.length > 0 && !args[0].isEmpty()) {
                if (args.length == 1 && args[0].equalsIgnoreCase("null")) {
                    event.async(() -> {
            event.getMySQL().removeMessage(guild.getIdLong(), MessageType.JOIN);
                        event.getVariables().joinMessages.invalidate(guild.getIdLong());
                    });
                    event.getMessageHelper().sendSplitMessage(event.getTextChannel(), "JoinMessage has been changed from \n" +  oldMessage + "\n to nothing by **" + event.getFullAuthorName() + "**");
                } else {
                    event.async(() -> {
                        event.getMySQL().setMessage(guild.getIdLong(), newMessage, MessageType.JOIN);
                        event.getVariables().joinMessages.put(guild.getIdLong(), newMessage);
                    });
                    event.getMessageHelper().sendSplitMessage(event.getTextChannel(),
                            "JoinMessage has been changed from \n" + oldMessage + "\n to \n" + newMessage + "\nby **" + event.getFullAuthorName() + "**");
                }
            } else {
                event.getMessageHelper().sendSplitMessage(event.getTextChannel(), "JoinMessage is set to:\n" + oldMessage);
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
