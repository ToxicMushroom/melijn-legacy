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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.melijn.jda.Melijn.PREFIX;

public class ForceRoleCommand extends Command {

    public ForceRoleCommand() {
        this.commandName = "forceRole";
        this.description = "Forces a role to a user in your server (user keeps role after rejoining)";
        this.usage = PREFIX + commandName + " <add | remove | list> <user> [role]";
        this.aliases = new String[]{"fr"};
        this.needs = new Need[]{Need.GUILD};
        this.category = Category.MANAGEMENT;
        this.id = 111;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.hasPerm(event.getMember(), commandName, 1)) {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if (args.length < 2) {
            event.sendUsage(this, event);
            return;
        }

        Guild guild = event.getGuild();

        event.getHelpers().retrieveUserByArgsN(event, args[1], target -> {
            if (target == null) {
                event.reply("Unknown user");
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (args.length < 3) {
                    event.reply(event.getVariables().prefixes.get(event.getGuildId()) + " add <user> <roles>");
                    return;
                }
                Role role = event.getHelpers().getRoleByArgs(event, args[2]);
                if (role == null) {
                    event.reply("unknown role");
                    return;
                }

                List<Long> roles = event.getMySQL().getForcedRoles(event.getGuildId(), target.getIdLong());
                if (roles.contains(role.getIdLong())) {
                    event.reply("The role `@" + role.getName() + "` is already forced upon **" + target.getName() + "#" + target.getDiscriminator() + "**");
                    return;
                }

                if (!guild.getSelfMember().canInteract(role)) {
                    event.reply("The role hasn't been forced upon **" + target.getName() + "#" + target.getDiscriminator() + "**, cause: **@" + role.getName() + "** is higher or equal in the role-hierarchy then my highest role.");
                    return;
                }

                Member member = guild.getMember(target);
                if (member != null && !guild.getSelfMember().canInteract(member)) {
                    event.reply("The role hasn't been forced upon **" + target.getName() + "#" + target.getDiscriminator() + "**, cause: The target has a higher or equal role in the role-hierarchy then my highest role.");
                    return;
                }

                event.getMySQL().addForceRole(event.getGuildId(), target.getIdLong(), role.getIdLong());
                event.reply("Forced the `@" + role.getName() + "` role upon **" + target.getName() + "#" + target.getDiscriminator() + "**");

                if (member != null) {
                    guild.getController().addSingleRoleToMember(member, role).queue();
                }

            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length < 3) {
                    event.reply(event.getVariables().prefixes.get(event.getGuildId()) + " remove <user> <roles>");
                    return;
                }

                Role role = event.getHelpers().getRoleByArgs(event, args[2]);
                if (role == null) {
                    event.reply("unknown role");
                    return;
                }

                List<Long> roles = event.getMySQL().getForcedRoles(event.getGuildId(), target.getIdLong());
                if (!roles.contains(role.getIdLong())) {
                    event.reply("The role `@" + role.getName() + "` isn't forced upon **" + target.getName() + "#" + target.getDiscriminator() + "**");
                    return;
                }

                event.getMySQL().removeForceRole(event.getGuildId(), target.getIdLong(), role.getIdLong());
                event.reply("Unforced the `@" + role.getName() + "` role from **" + target.getName() + "#" + target.getDiscriminator() + "**");

                Member member = guild.getMember(target);
                if (member != null && guild.getSelfMember().canInteract(member) && guild.getSelfMember().canInteract(role)) {
                    guild.getController().removeSingleRoleFromMember(member, role).queue();
                }

            } else if (args[0].equalsIgnoreCase("list")) {
                List<Long> roles = event.getMySQL().getForcedRoles(event.getGuildId(), target.getIdLong());
                StringBuilder builder = new StringBuilder("**Forced roles of " + target.getName() + "#" + target.getDiscriminator() + "**```MARKDOWN\n");
                AtomicInteger i = new AtomicInteger(1);

                roles.forEach(roleId -> {
                    Role role = event.getGuild().getRoleById(roleId);
                    if (role != null)
                    builder.append(i.getAndIncrement()).append(". ").append(role.getName()).append("\n");
                });
                builder.append("```");
                event.reply(builder.toString());
            }
        });
    }
}
