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

import static me.melijn.jda.Melijn.PREFIX;
import static me.melijn.jda.blub.ChannelType.SELF_ROLE;

public class SetSelfRoleChannelCommand extends Command {



    public SetSelfRoleChannelCommand() {
        this.commandName = "setSelfRoleChannel";
        this.usage = PREFIX + commandName + " [TextChannel]";
        this.description = "Sets the selfRoleChannel where members can select roles they want";
        this.aliases = new String[]{"ssrc"};
        this.needs = new Need[]{Need.GUILD};
        this.category = Category.MANAGEMENT;
        this.id = 99;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            if (args.length == 0 || args[0].isEmpty()) {
                String s = event.getVariables().selfRolesChannels.get(event.getGuild().getIdLong()) == -1 ?
                        "nothing" :
                        "<#" + event.getVariables().selfRolesChannels.get(event.getGuild().getIdLong()) + ">";
                event.reply("Current SelfRoleChannel: " + s);
            } else {
                long channel = event.getHelpers().getTextChannelByArgsN(event, args[0]);
                if (channel != -1) {
                    event.getMySQL().setChannel(event.getGuild().getIdLong(), channel, SELF_ROLE);
                    event.reply("The SelfRoleChannel has been changed to <#" + channel + "> by **" + event.getFullAuthorName() + "**");
                } else {
                    event.sendUsage(this, event);
                }
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
