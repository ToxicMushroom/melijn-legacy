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
import me.melijn.jda.audio.MusicPlayer;
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;
import net.dv8tion.jda.core.entities.VoiceChannel;

import static me.melijn.jda.Melijn.PREFIX;

public class ResumeCommand extends Command {

    public ResumeCommand() {
        this.commandName = "resume";
        this.description = "Resumes the paused track when paused";
        this.usage = PREFIX + commandName;
        this.aliases = new String[]{"unpause"};
        this.needs = new Need[]{Need.GUILD, Need.SAME_VOICECHANNEL_OR_DISCONNECTED};
        this.category = Category.MUSIC;
        this.id = 64;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getGuild().getMember(event.getAuthor()), commandName, 0)) {
            Lava lava = event.getClient().getMelijn().getLava();
            MusicPlayer player = lava.getAudioLoader().getPlayer(event.getGuild());
            VoiceChannel voiceChannel = event.getGuild().getMember(event.getAuthor()).getVoiceState().getChannel();
            if (voiceChannel == null) voiceChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
            if (voiceChannel != null) {
                if (!lava.tryToConnectToVC(event, event.getGuild(), voiceChannel)) return;

                player.resumeTrack();
                if (player.getAudioPlayer().getPlayingTrack() == null && player.getTrackManager().getTrackSize() > 0)
                    player.skipTrack();
                event.reply("Resumed by **" + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + "**");
            } else {
                event.reply("You or I have to be in a voice channel to resume");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
