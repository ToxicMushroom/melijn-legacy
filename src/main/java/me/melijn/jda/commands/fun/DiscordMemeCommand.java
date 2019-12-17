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

public class DiscordMemeCommand extends Command {

    public DiscordMemeCommand() {
        this.commandName = "discordMeme";
        this.description = "Shows a discord meme";
        this.usage = PREFIX + commandName;
        this.aliases = new String[]{"dmeme"};
        this.category = Category.FUN;
        this.id = 30;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            if (event.getArgs().split("\\s+").length > 0 && event.getArgs().split("\\s+")[0].equalsIgnoreCase("everyone"))
                event.getWebUtils().getImageByTag("everyone", image -> event.getMessageHelper().sendFunText(null, image.getUrl() , event));
            else event.getWebUtils().getImage("discord_memes", image -> event.getMessageHelper().sendFunText(null, image.getUrl() , event));
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
