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
import me.melijn.jda.utils.Embedder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

import static me.melijn.jda.Melijn.PREFIX;

public class AvatarCommand extends Command {

    public AvatarCommand() {
        this.commandName = "avatar";
        this.usage = PREFIX + commandName + " [user]";
        this.description = "Shows you an avatar with download link";
        this.aliases = new String[]{"profilepicture"};
        this.category = Category.UTILS;
        this.permissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.id = 53;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            String[] args = event.getArgs().split("\\s+");
            User user;
            if (args.length == 0 || args[0].isEmpty()) {
                user = event.getAuthor();
            } else {
                user = event.getHelpers().getUserByArgsN(event, args[0]);
            }
            if (user != null) {
                EmbedBuilder eb = new Embedder(event.getVariables(), event.getGuild());
                eb.setTitle(user.getName() + "#" + user.getDiscriminator() + "'s avatar");
                eb.setImage(user.getEffectiveAvatarUrl() + "?size=2048");
                eb.setDescription("[open](" + user.getEffectiveAvatarUrl() + "?size=2048)");
                 event.reply(eb.build());
            } else {
                event.reply("Unknown user");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
