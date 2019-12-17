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
import net.dv8tion.jda.core.entities.Member;

import java.text.SimpleDateFormat;
import java.util.Date;

import static me.melijn.jda.Melijn.PREFIX;

public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        this.commandName = "userinfo";
        this.description = "Shows information about a user/member";
        this.usage = PREFIX + commandName + " <user>";
        this.aliases = new String[]{"profile", "memberinfo"};
        this.category = Category.UTILS;
        this.permissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.id = 67;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm ss");
            String[] args = event.getArgs().split("\\s+");
            event.getHelpers().retrieveUserByArgs(event, args[0], user -> {
                EmbedBuilder eb = new Embedder(event.getVariables(), event.getGuild());
                eb.setTitle(user.getName() + "#" + user.getDiscriminator() + "'s profile");
                eb.setThumbnail(user.getEffectiveAvatarUrl() + "?size=2048");
                if (event.getGuild() == null || event.getGuild().getMember(user) == null) {
                    eb.addField("ID:", user.getId(), true);
                    eb.addField("Avatar:", "**[link](" + user.getEffectiveAvatarUrl() + "?size=2048)**", true);
                    eb.addField("Discord join date:", simpleDateFormat.format(Date.from(user.getCreationTime().toInstant())) + "s", true);
                    eb.addField("Is Member:", "no", true);
                    eb.addField("Is Bot:", String.valueOf(user.isBot()), false);
                } else {
                    Member member = event.getGuild().getMember(user);
                    String nickname = member.getNickname() == null ? "None" : member.getNickname();
                    eb.addField("ID:", user.getId(), false);
                    eb.addField("Nickname:", nickname, true);
                    eb.addField("Avatar:", "**[link](" + user.getEffectiveAvatarUrl() + "?size=2048)**", true);
                    eb.addField("Status:", member.getOnlineStatus().name().toLowerCase(), false);
                    eb.addField("Discord join date:", simpleDateFormat.format(Date.from(user.getCreationTime().toInstant())) + "s", true);
                    eb.addField("Guild join date:", simpleDateFormat.format(Date.from(member.getJoinDate().toInstant())) + "s", true);
                    eb.addField("Is Owner:", member.isOwner() ? "yes" : "no", true);
                    eb.addField("Is Member:", "yes", true);
                    eb.addField("Is Bot:", user.isBot() ? "yes" : "no", false);
                }
                event.reply(eb.build());
            });
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
