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

import static me.melijn.jda.Melijn.PREFIX;

public class UnicodeCommand extends Command {

    public UnicodeCommand() {
        this.commandName = "unicode";
        this.description = "Converts an input to unicode";
        this.usage = PREFIX + commandName + " <input>";
        this.category = Category.UTILS;
        this.id = 92;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            String arg = event.getArgs();
            if (arg.isEmpty()) {
                event.sendUsage(this, event);
                return;
            }

            if (arg.matches("<.?:.*:\\d+>")) {
                String id = arg.replaceAll("<.?:.*:(\\d+)>", "$1");
                String name = arg.replaceAll("<.?:(.*):\\d+>", "$1");
                event.reply("" +
                        "Name: **" + name + "**\n" +
                        "ID: **" + id + "**\n" +
                        "URL: **https://cdn.discordapp.com/emojis/" + id + ".png?size=2048**");
                return;
            }
            StringBuilder builder = new StringBuilder();
            arg.codePoints().forEachOrdered(code -> {
                char[] chars = Character.toChars(code);
                if (chars.length > 1) {
                    StringBuilder hex0 = new StringBuilder(Integer.toHexString(chars[0]).toUpperCase());
                    StringBuilder hex1 = new StringBuilder(Integer.toHexString(chars[1]).toUpperCase());
                    while (hex0.length() < 4)
                        hex0.insert(0, "0");
                    while (hex1.length() < 4)
                        hex1.insert(0, "0");
                    builder.append("`\\u").append(hex0).append("\\u").append(hex1).append("`   ");
                } else {
                    StringBuilder hex = new StringBuilder(Integer.toHexString(code).toUpperCase());
                    while (hex.length() < 4)
                        hex.insert(0, "0");
                    builder.append("`\\u").append(hex).append("`   ");
                }
                builder.append(String.valueOf(chars)).append("   _").append(Character.getName(code)).append("_\n");
            });
            event.getMessageHelper().sendSplitMessage(event.getTextChannel(), builder.toString());
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
