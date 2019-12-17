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

package me.melijn.jda.commands;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;

import static me.melijn.jda.Melijn.PREFIX;

public class DonateCommand extends Command {

    public DonateCommand() {
        this.commandName = "donate";
        this.description = "Gives you information on how you can support the developer of Melijn ;)";
        this.usage = PREFIX + commandName;
        this.category = Category.DEFAULT;
        this.id = 90;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("You can donate to the development and server cost of me here: **https://paypal.me/PixelHamster**\n" +
                "You can then also request a donator role in my support discord\n" +
                "**Warning** donations will not be refunded **and** don't use someone else their money");
    }
}
