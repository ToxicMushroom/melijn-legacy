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
import org.jetbrains.annotations.NotNull;

import static me.melijn.jda.Melijn.PREFIX;

public class FilterCommand extends Command {


    public FilterCommand() {
        this.commandName = "filter";
        this.description = "Manages the chat filter of the bot";
        this.usage = PREFIX + commandName + " <allowed | denied> <add | remove | list> [word]";
        this.category = Category.MANAGEMENT;
        this.extra = "e.g. if blub is in the denied list and blubber is in the allowed list then they can say blubber but can't say other word containing blub";
        this.needs = new Need[]{Need.GUILD};
        this.id = 42;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            Guild guild = event.getGuild();
            String[] args = event.getArgs().split("\\s+");
            if (args.length >= 2) {
                String content = event.getArgs().replaceFirst(args[0] + "\\s+" + args[1], "").replaceFirst("\\s+", "");
                switch (args[0]) {
                    case "allowed":
                        switch (args[1]) {
                            case "add":
                                if (args.length > 2) {
                                    event.getMySQL().addFilter(guild.getIdLong(), "allowed", content);
                                    event.reply("`" + content + "` has been added to the allowed list.");
                                } else {
                                    event.sendUsage(this, event);
                                }
                                break;
                            case "remove":
                                if (args.length > 2) {
                                    event.getMySQL().removeFilter(guild.getIdLong(), "allowed", content);
                                    event.reply("`" + content + "` has been removed from the allowed list.");
                                } else {
                                    event.sendUsage(this, event);
                                }
                                break;
                            case "list":
                                int filterNumber = 0;
                                StringBuilder partBuilder = new StringBuilder();
                                partBuilder.append("**Allowed List**\n```Markdown\n");
                                for (String s : event.getMySQL().getFilters(guild.getIdLong(), "allowed")) {
                                    partBuilder = addListParts(event, partBuilder, s);
                                    partBuilder.append(++filterNumber).append(". ").append(s.replaceAll("`", "´")).append("\n");
                                }
                                partBuilder.append("```");
                                event.reply(partBuilder.toString());
                                break;
                            default:
                                event.sendUsage(this, event);
                                break;
                        }
                        break;
                    case "denied":
                        switch (args[1]) {
                            case "add":
                                if (args.length > 2) {
                                    event.getMySQL().addFilter(guild.getIdLong(), "denied", content);
                                    event.reply("`" + content + "` has been added to the denied list.");
                                } else {
                                    event.sendUsage(this, event);
                                }
                                break;
                            case "remove":
                                if (args.length > 2) {
                                    event.getMySQL().removeFilter(guild.getIdLong(), "denied", content);
                                    event.reply("`" + content + "` has been removed from the denied list.");
                                } else {
                                    event.sendUsage(this, event);
                                }
                                break;
                            case "list":
                                int filterNumber = 0;
                                StringBuilder partBuilder = new StringBuilder();
                                partBuilder.append("**Denied List**\n```Markdown\n");
                                for (String s : event.getMySQL().getFilters(guild.getIdLong(), "denied")) {
                                    partBuilder = addListParts(event, partBuilder, s);
                                    partBuilder.append(++filterNumber).append(". ").append(s.replaceAll("`", "´")).append("\n");
                                }
                                partBuilder.append("```");
                                event.reply(partBuilder.toString());
                                break;
                            default:
                                event.sendUsage(this, event);
                                break;
                        }
                        break;
                    default:
                        event.sendUsage(this, event);
                        break;
                }
            } else {
                event.sendUsage(this, event);
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }

    @NotNull
    private StringBuilder addListParts(CommandEvent event, StringBuilder partBuilder, String s) {
        StringBuilder stringBuilder = partBuilder;
        if (stringBuilder.length() + s.length() > 1900) {
            stringBuilder.append("```");
            event.reply(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("```Markdown\n");
        }
        return stringBuilder;
    }
}
