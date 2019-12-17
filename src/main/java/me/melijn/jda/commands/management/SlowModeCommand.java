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
import net.dv8tion.jda.core.Permission;

import static me.melijn.jda.Melijn.PREFIX;

public class SlowModeCommand extends Command {

    public SlowModeCommand() {
        this.commandName = "slowMode";
        this.usage = PREFIX + commandName + " <seconds (0-120)>";
        this.description = "Sets the SlowMode in the TextChannel";
        this.permissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.needs = new Need[]{Need.GUILD};
        this.category = Category.MANAGEMENT;
        this.id = 91;
    }
    
    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            if (args.length > 0 && args[0].matches("[0-9]{1,2}|[0-1]{1,2}[0-9]|[0-1][0-2]0") && args[0].length() < 4) {
                int seconds = Integer.parseInt(args[0]);
                if (seconds == 0) {
                    event.getTextChannel().getManager().setSlowmode(0).queue();
                    event.reply("SlowMode has been **Disabled** for this TextChannel");
                } else if (seconds <= 120) {
                    event.getTextChannel().getManager().setSlowmode(seconds).queue();
                    event.reply("SlowMode has been set to **" + seconds + "** for this TextChannel");
                }
            } else {
                event.sendUsage(this, event);
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
