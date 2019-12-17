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
import me.melijn.jda.utils.Embedder;

import java.util.Arrays;
import java.util.List;

import static me.melijn.jda.Melijn.PREFIX;

public class TextToEmojiCommand extends Command {

    private static final List<String> numbers = Arrays.asList("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine");

    public TextToEmojiCommand() {
        this.commandName = "t2e";
        this.description = "Converts input text and numbers to emotes";
        this.usage = PREFIX + commandName + " [%spaces%] <text>";
        this.aliases = new String[]{"TextToEmojis"};
        this.extra = "%spaces% will put a space after each emoji so they don't change into flags when copied and pasted";
        this.category = Category.UTILS;
        this.id = 69;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            String[] args = event.getArgs().split("\\s+");
            if (event.getArgs().isEmpty()) {
                event.sendUsage(this, event);
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (char c : event.getArgs().replaceFirst("%spaces%", "").toCharArray()) {
                if (Character.isLetter(c)) {
                    sb.append(":regional_indicator_").append(Character.toLowerCase(c)).append(":");
                    if (args[0].equalsIgnoreCase("%spaces%")) {
                        sb.append(" ");
                    }
                } else if (Character.isDigit(c)) {
                    sb.append(":").append(numbers.get(Character.getNumericValue(c))).append(":");
                    if (args[0].equalsIgnoreCase("%spaces%")) {
                        sb.append(" ");
                    }
                } else {
                    sb.append(c);
                }
                if (sb.length() > 1900) {
                    event.reply(sb.toString());
                    sb = new StringBuilder();
                }
            }
            event.reply(new Embedder(event.getVariables(), event.getGuild()).setDescription(sb.toString()).build());

        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
