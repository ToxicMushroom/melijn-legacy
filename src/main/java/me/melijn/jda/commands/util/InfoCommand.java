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

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import me.duncte123.weebJava.WeebInfo;
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.utils.Embedder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

import static me.melijn.jda.Melijn.OWNERID;
import static me.melijn.jda.Melijn.PREFIX;

public class InfoCommand extends Command {

    public InfoCommand() {
        this.commandName = "info";
        this.usage = PREFIX + commandName;
        this.description = "Shows information about the bot";
        this.aliases = new String[]{"about", "botinfo", "author"};
        this.category = Category.UTILS;
        this.permissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.id = 66;
    }

    /* CREDITS TO DUNCTE123 FOR DESIGN */

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {

            event.reply(new Embedder(event.getVariables(), event.getGuild())
                    .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                    .addField("About", "" +
                            "\nI'm a powerful discord bot developed by **ToxicMushroom#2610**" +
                            "\nMore commands/features are still being added, you can even request them in the support server below" +
                            "\n\n**[Support Server](https://discord.gg/E2RfZA9)** • **[Invite](https://melijn.com/invite?perms=true)** • **[Website](https://melijn.com/)**" +
                            "\n\u200B", false)
                    .addField("Info", "" +
                            "\n**Operating System** " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version") +
                            "\n**Coded in** Java" +
                            "\n**Commands** " + event.getClient().getCommands().size() +
                            "\n\u200B", false)
                    .addField("Libraries", "" +
                            "**Java Version** " + System.getProperty("java.version") +
                            "\n**JDA Version** " + JDAInfo.VERSION +
                            "\n**Lavaplayer Version** " + PlayerLibrary.VERSION +
                            "\n**Weeb.java Version** " + WeebInfo.VERSION +
                            "\n**MySQL Version** " + event.getMySQL().getMySQLVersion() +
                            "\n**MySQL Connector Version** " + event.getMySQL().getConnectorVersion(), false)
                    .setFooter("Requested by " + event.getFullAuthorName(), event.getAvatarUrl())
                    .build());
            if (event.getAuthor().getIdLong() == OWNERID) {
                StringBuilder desc = new StringBuilder();
                desc.append("```Less\n");
                int blub = 0;
                for (Guild guild : event.getJDA().asBot().getShardManager().getGuildCache()) {
                    if (event.getClient().getMelijn().getLava().isConnected(guild.getIdLong()))
                        desc.append("#").append(++blub).append(" - ").append(guild.getName()).append("\n");
                }
                desc.append("```");
                if (desc.length() > 11)
                    event.getAuthor().openPrivateChannel().queue((channel) -> event.getMessageHelper().sendSplitMessage(channel, desc.toString()));
            }

        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
