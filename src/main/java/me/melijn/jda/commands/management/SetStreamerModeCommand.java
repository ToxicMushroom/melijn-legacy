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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

import static me.melijn.jda.Melijn.PREFIX;

public class SetStreamerModeCommand extends Command {

    public SetStreamerModeCommand() {
        this.commandName = "setStreamerMode";
        this.description = "Sets the StreamerMode";
        this.usage = PREFIX + commandName + " [true/on | false/off]";
        this.aliases = new String[]{"ssm"};
        this.category = Category.MANAGEMENT;
        this.extra = "https://melijn.com/guides/guide-5/";
        this.needs = new Need[]{Need.GUILD};
        this.id = 83;
    }

    @Override
    protected void execute(CommandEvent event) {
        Guild guild = event.getGuild();
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            if (event.getVariables().musicChannelCache.get(guild.getIdLong()) == -1) {
                event.reply("You first have to set a MusicChannel.\n" + event.getVariables().prefixes.get(guild.getIdLong()) + "smc <channelId>");
                return;
            }
            VoiceChannel musicChannel = guild.getVoiceChannelById(event.getVariables().musicChannelCache.get(guild.getIdLong()));
            if (musicChannel != null) {
                String[] args = event.getArgs().split("\\s+");
                if (args.length == 0 || args[0].isEmpty()) {
                    String state = event.getVariables().streamerModeCache.get(guild.getIdLong()) ? "enabled" : "disabled";
                    event.reply("StreamerMode: **" + state + "**");
                } else if (args.length == 1) {
                    switch (args[0]) {
                        case "true":
                        case "on":
                        case "enabled":
                            if (guild.getSelfMember().hasPermission(musicChannel, Permission.VOICE_CONNECT)) {
                                event.getClient().getMelijn().getLava().openConnection(musicChannel);
                                event.async(() -> {
                                    event.getMySQL().setStreamerMode(guild.getIdLong(), true);
                                    event.getVariables().streamerModeCache.put(guild.getIdLong(), true);
                                });
                                event.reply("\uD83D\uDCF6 The StreamerMode has been **enabled** by **" + event.getFullAuthorName() + "**");
                            } else {
                                event.reply(String.format("I have no permission to connect to the configured MusicChannel: %s", musicChannel.getName()));
                            }
                            break;
                        case "false":
                        case "off":
                        case "disabled":
                            event.async(() -> {
                                event.getMySQL().setStreamerMode(guild.getIdLong(), false);
                                event.getVariables().streamerModeCache.put(guild.getIdLong(), false);
                            });
                            event.reply("The streamer mode has been **disabled** by **" + event.getFullAuthorName() + "**");
                            break;
                        default:
                            event.sendUsage(this, event);
                            break;
                    }
                } else {
                    event.sendUsage(this, event);
                }
            } else {
                event.getMySQL().removeChannel(guild.getIdLong(), ChannelType.MUSIC);
                event.getVariables().musicChannelCache.invalidate(guild.getIdLong());
                event.reply("You have to set a MusicChannel to enable this mode :p");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
