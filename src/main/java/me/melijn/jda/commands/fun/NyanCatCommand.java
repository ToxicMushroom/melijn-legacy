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
import me.melijn.jda.utils.Embedder;
import net.dv8tion.jda.core.Permission;

import static me.melijn.jda.Melijn.PREFIX;

public class NyanCatCommand extends Command {

    public NyanCatCommand() {
        this.commandName = "nyancat";
        this.description = "Shows a nyancat";
        this.usage = PREFIX + commandName;
        this.category = Category.FUN;
        this.aliases = new String[]{"nyan"};
        this.id = 11;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            String emoji = (event.getGuild() == null || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EXT_EMOJI)) ?
                    "<:normie_nyan:490976816018751503>" :
                    "nyan";
            if (event.getGuild() == null || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EMBED_LINKS))
                event.reply(new Embedder(event.getVariables(), event.getGuild())
                        .setDescription("Enjoy your " + emoji + " ~meow!~")
                        .setImage("https://github.com/ToxicMushroom/nyan-cats/raw/master/cat%20(" + event.getMessageHelper().randInt(2, 33) + ").gif")
                        .build());
            else
                event.reply("Enjoy your " + emoji + " ~meow!~\n"
                        + "https://github.com/ToxicMushroom/nyan-cats/raw/master/cat%20(" + event.getMessageHelper().randInt(2, 33) + ").gif");
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
