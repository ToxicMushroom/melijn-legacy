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

package me.melijn.jda.commands.music;

import me.melijn.jda.audio.Lava;
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

import static me.melijn.jda.Melijn.PREFIX;

public class SPlayCommand extends Command {

    public SPlayCommand() {
        this.commandName = "splay";
        this.description = "Gives you search results to pick from";
        this.usage = PREFIX + commandName + " [sc] [songname]";
        this.aliases = new String[]{"search", "searchplay", "sp"};
        this.category = Category.MUSIC;
        this.needs = new Need[]{Need.GUILD, Need.SAME_VOICECHANNEL_OR_DISCONNECTED};
        this.permissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.VOICE_CONNECT};
        this.id = 45;
    }

    @Override
    protected void execute(CommandEvent event) {
        Guild guild = event.getGuild();
        boolean access = event.hasPerm(guild.getMember(event.getAuthor()), commandName + ".*", 1);
        VoiceChannel senderVoiceChannel = guild.getMember(event.getAuthor()).getVoiceState().getChannel();
        String[] args = event.getArgs().split("\\s+");
        if (args.length == 0 || args[0].isEmpty()) {
            event.sendUsage(this, event);
            return;
        }
        Lava lava = event.getClient().getMelijn().getLava();
        String songName;
        songName = event.getMessageHelper().argsToSongName(args,  event.getVariables().providers);
        switch (args[0].toLowerCase()) {
            case "sc":
            case "soundcloud":
                if (event.hasPerm(guild.getMember(event.getAuthor()), commandName + ".sc", 0) || access) {
                    if (!lava.tryToConnectToVC(event, guild, senderVoiceChannel)) return;
                    lava.getAudioLoader().searchTracks(event.getTextChannel(), "scsearch:" + songName, event.getAuthor());
                } else {
                    event.reply("You need the permission `" + commandName + ".sc` to execute this command.");
                }
                break;
            default:
                if (event.hasPerm(guild.getMember(event.getAuthor()), commandName + ".yt", 0) || access) {
                    if (!lava.tryToConnectToVC(event, guild, senderVoiceChannel)) return;
                    lava.getAudioLoader().searchTracks(event.getTextChannel(), "ytsearch:" + songName, event.getAuthor());
                } else {
                    event.reply("You need the permission `" + commandName + ".yt` to execute this command.");
                }
                break;

        }
    }
}
