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

import java.util.concurrent.TimeUnit;

import static me.melijn.jda.Melijn.PREFIX;

public class DiceCommand extends Command {

    public DiceCommand() {
        this.commandName = "dice";
        this.description = "Rolls a dice";
        this.usage = PREFIX + commandName + " [diceSize (default 6, max 999999)] [dice rolls (default 1, max 20)]";
        this.category = Category.UTILS;
        this.id = 103;
    }


    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            String[] args = event.getArgs().split("\\s+");
            int size = 6;
            int rolls = 1;
            if (args.length > 0 && args[0].matches("\\d{1,6}")) size = Integer.parseInt(args[0]);
            if (args.length > 1 && args[1].matches("[1-9]|1\\d|20")) rolls = Integer.parseInt(args[1]);
            if (rolls == 1) {
                final int finalSize = size;
                event.reply("Rolling dice .. \uD83C\uDFB2", message ->
                        message.editMessage("Result: **" + event.getMessageHelper().randInt(1, finalSize) + "**").queueAfter(1, TimeUnit.SECONDS));
            } else {
                StringBuilder sb = new StringBuilder("**Results** \uD83C\uDFB2");
                for (int i = 1; i <= rolls; i++) {
                    sb.append("\nroll #").append(i).append(".  **").append(event.getMessageHelper().randInt(1, size)).append("**");
                }
                event.getMessageHelper().sendSplitMessage(event.getTextChannel(), sb.toString());
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
