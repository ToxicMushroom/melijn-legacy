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
import net.dv8tion.jda.core.entities.Role;

import static me.melijn.jda.Melijn.PREFIX;

public class SetJoinRoleCommand extends Command {


    public SetJoinRoleCommand() {
        this.commandName = "setJoinRole";
        this.description = "Sets the role that will be added to users when they join";
        this.usage = PREFIX + commandName + " [role | null]";
        this.aliases = new String[]{"sjr"};
        this.category = Category.MANAGEMENT;
        this.needs = new Need[]{Need.GUILD};
        this.id = 36;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            Guild guild = event.getGuild();
            String[] args = event.getArgs().split("\\s+");
            long role = event.getVariables().joinRoleCache.get(guild.getIdLong());
            if (args.length == 0 || args[0].isEmpty()) {
                if (role != -1 && guild.getRoleById(role) != null)
                    event.reply("Current JoinRole: **@" + guild.getRoleById(role).getName() + "**");
                else event.reply("Current JoinRole is unset");
            } else {
                if (args[0].equalsIgnoreCase("null")) {
                    event.async(() -> {
                        event.getMySQL().removeRole(guild.getIdLong(), RoleType.JOIN);
                        event.getVariables().joinRoleCache.invalidate(guild.getIdLong());
                    });
                    event.reply("JoinRole has been unset by **" + event.getFullAuthorName() + "**");
                } else {
                    Role joinRole = event.getHelpers().getRoleByArgs(event, args[0]);
                    if (joinRole == null) {
                        event.getMessageHelper().sendUsage(this, event);
                        return;
                    }

                    if (joinRole.getIdLong() == guild.getIdLong()) {
                        event.reply("The @everyone role cannot be set as the JoinRole ;-;");
                        return;
                    }

                    if (guild.getSelfMember().getRoles().size() == 0 || !guild.getSelfMember().getRoles().get(0).canInteract(joinRole)) {
                        event.reply("" + "The JoinRole hasn't been changed due: **@" + joinRole.getName() + "** is higher or equal in the role-hierarchy then my highest role.");
                        return;
                    }

                    event.async(() -> {
                        event.getMySQL().setRole(guild.getIdLong(), joinRole.getIdLong(), RoleType.JOIN);
                        event.getVariables().joinRoleCache.put(guild.getIdLong(), joinRole.getIdLong());
                    });
                    event.reply("JoinRole changed to **@" + joinRole.getName() + "** by **" + event.getFullAuthorName() + "**");
                }
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
