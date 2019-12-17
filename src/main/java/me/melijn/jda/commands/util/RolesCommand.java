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
import me.melijn.jda.blub.Need;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.utils.cache.SnowflakeCacheView;

import static me.melijn.jda.Melijn.PREFIX;

public class RolesCommand extends Command {

    public RolesCommand() {
        this.commandName = "roles";
        this.description = "Shows a list of all the roles with their id's";
        this.usage = PREFIX + commandName;
        this.aliases = new String[]{"rolelist"};
        this.category = Category.UTILS;
        this.needs = new Need[]{Need.GUILD};
        this.id = 75;
    }


    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 0)) {
            Guild guild = event.getGuild();
            SnowflakeCacheView<Role> roles = guild.getRoleCache();
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int count = 1;
            for (Role role : roles) {
                sb.append(count++).append(" - [").append(role.getName()).append("] - ").append(role.getId()).append("\n");
                if (sb.length() > 1850) {
                    event.reply("Roles of " + guild.getName() + " part **#" + i + "**\n```INI\n" + sb.toString() + "```");
                    sb = new StringBuilder();
                    i++;
                }
            }
            if (sb.length() != 0)
                event.reply("Roles of " + guild.getName() + " part **#" + i + "**\n```INI\n" + sb.toString() + "```");
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
