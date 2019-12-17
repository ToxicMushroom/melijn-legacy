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
import me.melijn.jda.blub.Need;
import me.melijn.jda.utils.Embedder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static me.melijn.jda.Melijn.PREFIX;

public class GuildInfoCommand extends Command {

    public GuildInfoCommand() {
        this.commandName = "guildInfo";
        this.description = "Shows information about the guild";
        this.usage = PREFIX + commandName + " [guildId]";
        this.aliases = new String[]{"serverInfo"};
        this.extra = "Viewing another guild their info only works if they have Melijn as member";
        this.category = Category.UTILS;
        this.permissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.needs = new Need[]{Need.GUILD};
        this.id = 74;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getGuild().getMember(event.getAuthor()), commandName, 0)) {
            String[] args = event.getArgs().split("\\s+");
            Guild guild = event.getGuild();
            if (args.length == 1 && args[0].matches("\\d+") && event.getJDA().asBot().getShardManager().getGuildById(args[0]) != null)
                guild = event.getJDA().asBot().getShardManager().getGuildById(args[0]);
            event.reply(new Embedder(event.getVariables(), event.getGuild())
                    .setAuthor(guild.getName(), null, guild.getIconUrl() == null ? null : guild.getIconUrl() + "?size=2048")
                    .addField("ID", guild.getId(), true)
                    .addField("Icon", guild.getIconUrl() == null ? "none" : "[Download](" + guild.getIconUrl() + "?size=2048)", true)
                    .addField("Creation date", guild.getCreationTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)), false)
                    .addField("Region", guild.getRegion().getName(), true)
                    .addField("Vip servers", String.valueOf(guild.getRegion().isVip()), true)
                    .addField("Owner", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), true)
                    .addField("Members", String.valueOf(guild.getMemberCache().size()), true)
                    .addField("Roles", String.valueOf(guild.getRoleCache().size()), true)
                    .addBlankField(true)
                    .addField("TextChannels", String.valueOf(guild.getTextChannelCache().size()), true)
                    .addField("VoiceChannels", String.valueOf(guild.getVoiceChannelCache().size()), true)
                    .addField("Categories", String.valueOf(guild.getCategoryCache().size()), true)
                    .setFooter(event.getHelpers().getFooterStamp(), event.getHelpers().getFooterIcon())
                    .build());

        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
