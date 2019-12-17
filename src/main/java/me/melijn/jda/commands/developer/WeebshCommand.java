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

import java.util.Arrays;

import static me.melijn.jda.Melijn.PREFIX;

public class WeebshCommand extends Command {

    public WeebshCommand() {
        this.commandName = "weebsh";
        this.description = "Uses weebsh api to do stuff";
        this.usage = PREFIX + commandName + " <tags | types | type | tag> [arg]";
        this.category = Category.DEVELOPER;
        this.id = 28;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "tags":
                    event.getWebUtils().getTags(tags -> event.reply(Arrays.toString(tags.toArray())));
                    break;
                case "types":
                    event.getWebUtils().getTypes(types -> event.reply(Arrays.toString(types.getTypes().toArray())));
                    break;
                case "type":
                    if (args.length > 1) {
                       event.getWebUtils().getImage(event.getArgs().replaceFirst(args[0] + "\\s+", ""), (image) -> event.reply(image.getUrl()));
                    } else {
                        event.sendUsage(this, event);
                    }
                    break;
                case "tag":
                    if (args.length > 1) {
                        event.getWebUtils().getImageByTag(event.getArgs().replaceFirst(args[0] + "\\s+", ""), (image) -> event.reply(image.getUrl()));
                    } else {
                        event.sendUsage(this, event);
                    }
                    break;
                default:
                    event.sendUsage(this, event);
                    break;
            }
        } else {
            event.sendUsage(this, event);
        }
    }
}
