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

import lavalink.client.player.LavalinkPlayer;
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;

import static me.melijn.jda.Melijn.PREFIX;

public class RewindCommand extends Command {

    public RewindCommand() {
        this.commandName = "rewind";
        this.description = "Rewinds inside the track";
        this.usage = PREFIX + commandName + " [hh:mm:ss]";
        this.extra = "e.g. >rewind 11 -> -11s | >rewind 1:01 -> -61s | >rewind 1:02:01 -> -3721s";
        this.needs = new Need[]{Need.GUILD, Need.SAME_VOICECHANNEL};
        this.category = Category.MUSIC;
        this.id = 14;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getGuild().getMember(event.getAuthor()), commandName, 0)) {
            String[] args = event.getArgs().replaceAll(":", " ").split("\\s+");
            LavalinkPlayer player = event.getClient().getMelijn().getLava().getAudioLoader().getPlayer(event.getGuild()).getAudioPlayer();
            if (player.getPlayingTrack() != null) {
                long millis = event.getHelpers().parseTimeFromArgs(args);
                if (millis == -1) {
                    event.sendUsage(this, event);
                    return;
                }
                millis = player.getTrackPosition() - millis;
                player.seekTo(millis);
                event.reply("The position of the song has been changed to **" +
                        event.getMessageHelper().getDurationBreakdown(Math.max(millis, 0)) + "/" +
                        event.getMessageHelper().getDurationBreakdown(player.getPlayingTrack().getDuration()) + "** by **" + event.getFullAuthorName() + "**");

            } else {
                event.reply("There are no songs playing at the moment.");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
