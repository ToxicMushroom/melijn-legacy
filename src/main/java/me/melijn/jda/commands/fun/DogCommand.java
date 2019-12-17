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

package me.melijn.jda.commands.fun;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;

import static me.melijn.jda.Melijn.PREFIX;

public class DogCommand extends Command {

    public DogCommand() {
        this.commandName = "dog";
        this.description = "Shows you a dog";
        this.usage = PREFIX + commandName;
        this.category = Category.FUN;
        this.aliases = new String[]{"hond"};
        this.id = 77;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            event.getWebUtils().getImage("animal_dog", image ->
                    event.getMessageHelper().sendFunText("Enjoy your \uD83D\uDC36 ~woof~", image.getUrl(), event));
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
