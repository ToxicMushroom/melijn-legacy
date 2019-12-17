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

public class BirdCommand extends Command {

    public BirdCommand() {
        this.commandName = "bird";
        this.description = "Shows a bird";
        this.usage = PREFIX + commandName;
        this.aliases = new String[]{"vogel"};
        this.category = Category.FUN;
        this.id = 0;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            String url = event.getWebUtils().getBirdUrl();
            if (event.getGuild() == null || event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS))
                if (url != null)
                    event.reply(new Embedder(event.getVariables(), event.getGuild())
                            .setDescription("Enjoy your \uD83D\uDC26 ~tweet~")
                            .setImage(url)
                            .build());
                else {
                    event.reply("some-random-api.ml/img/birb is down :/");
                }
            else
                event.reply("Enjoy your \uD83D\uDC31 ~meow~\n" + url);
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
