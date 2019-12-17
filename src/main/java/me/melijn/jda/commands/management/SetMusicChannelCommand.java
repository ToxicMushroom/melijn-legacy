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

import me.melijn.jda.blub.*;
import net.dv8tion.jda.core.entities.Guild;

import static me.melijn.jda.Melijn.PREFIX;

public class SetMusicChannelCommand extends Command {

    public SetMusicChannelCommand() {
        this.commandName = "setMusicChannel";
        this.description = "Sets the MusicChannel";
        this.usage = PREFIX + commandName + " [VoiceChannel | null]";
        this.aliases = new String[]{"smc"};
        this.needs = new Need[]{Need.GUILD};
        this.extra = "https://melijn.com/guides/guide-5/";
        this.category = Category.MANAGEMENT;
        this.id = 79;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            Guild guild = event.getGuild();
            String[] args = event.getArgs().split("\\s+");
            if (args.length == 0 || args[0].isEmpty()) {
                event.reply(event.getVariables().musicChannelCache.get(guild.getIdLong()) == -1 ? "The MusicChannel is unset" : "MusicChannel: <#" + event.getVariables().musicChannelCache.get(guild.getIdLong()) + ">");
            } else {
                long channelId = event.getHelpers().getVoiceChannelByArgsN(event, args[0]);
                if (channelId == -1) {
                    event.sendUsage(this, event);
                } else if (channelId == 0) {
                    event.async(() -> {
                        event.getMySQL().removeChannel(guild.getIdLong(), ChannelType.MUSIC);
                        event.getVariables().musicChannelCache.invalidate(guild.getIdLong());
                    });
                    event.reply("The MusicChannel has been unset by **" + event.getFullAuthorName() + "**");
                } else {
                    event.async(() -> {
                        event.getMySQL().setChannel(guild.getIdLong(), channelId, ChannelType.MUSIC);
                        event.getVariables().musicChannelCache.put(guild.getIdLong(), channelId);
                    });
                    event.reply("The MusicChannel has been set to <#" + args[0] + "> by **" + event.getFullAuthorName() + "**");
                }
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
