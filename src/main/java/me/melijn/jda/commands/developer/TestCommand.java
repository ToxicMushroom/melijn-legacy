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

package me.melijn.jda.commands.developer;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;

import static me.melijn.jda.Melijn.PREFIX;

public class TestCommand extends Command {

    public TestCommand() {
        this.commandName = "test";
        this.description = "this command is for testing";
        this.usage = PREFIX + commandName;
        this.category = Category.DEVELOPER;
        this.id = 104;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Repeats: " + event.getVariables().timerAmount + "\nQueries: " + event.getVariables().queryAmount);
    }
}
