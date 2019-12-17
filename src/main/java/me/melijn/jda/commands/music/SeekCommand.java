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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.LavalinkPlayer;
import me.melijn.jda.audio.Lava;
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;
import net.dv8tion.jda.core.entities.Guild;

import static me.melijn.jda.Melijn.PREFIX;

public class SeekCommand extends Command {

    public SeekCommand() {
        this.commandName = "seek";
        this.description = "Seeks the part of the track you desire";
        this.usage = PREFIX + commandName + " [hh:mm:ss]";
        this.aliases = new String[]{"skipx", "position"};
        this.needs = new Need[]{Need.SAME_VOICECHANNEL};
        this.category = Category.MUSIC;
        this.id = 70;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 0)) {
            Guild guild = event.getGuild();
            String[] args = event.getArgs().replaceAll(":", " ").split("\\s+");
            Lava lava = event.getClient().getMelijn().getLava();
            LavalinkPlayer player = lava.getAudioLoader().getPlayer(guild).getAudioPlayer();
            AudioTrack track = player.getPlayingTrack();
            if (track == null) {
                event.reply("There are currently no tracks playing");
                return;
            }

            if (args.length == 0 || args[0].isEmpty()) {
                event.reply("The current position is **" + event.getMessageHelper().getDurationBreakdown(player.getTrackPosition()) + "/" + event.getMessageHelper().getDurationBreakdown(track.getDuration()) + "**");
                return;
            }

            long millis = event.getHelpers().parseTimeFromArgs(args);
            if (millis == -1) event.sendUsage(this, event);
            else {
                player.seekTo(millis);
                event.reply("The position of the song has been changed to **" +
                        event.getMessageHelper().getDurationBreakdown(Math.min(millis, track.getDuration())) + "/" +
                        event.getMessageHelper().getDurationBreakdown(track.getDuration()) + "** by **" + event.getFullAuthorName() + "**");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
