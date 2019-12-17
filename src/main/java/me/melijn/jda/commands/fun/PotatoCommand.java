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

public class PotatoCommand extends Command {

    public PotatoCommand() {
        this.commandName = "potato";
        this.description = "Shows a potato";
        this.usage = PREFIX + commandName;
        this.category = Category.FUN;
        this.id = 43;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            if (event.getGuild() == null || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EMBED_LINKS))
                event.reply(new Embedder(event.getVariables(), event.getGuild())
                        .setDescription("Enjoy your delicious \uD83E\uDD54")
                        .setImage(event.getPrivate().getWeebV1Url(event.getWebUtils(), "potato"))
                        .setFooter("Powered by weeb.sh", null)
                        .build());
            else
                event.reply("Enjoy your \uD83E\uDD54 \n" + event.getPrivate().getWeebV1Url(event.getWebUtils(),"potato"));
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
