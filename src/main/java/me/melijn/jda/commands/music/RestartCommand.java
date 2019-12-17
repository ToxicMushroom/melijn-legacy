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
import me.melijn.jda.audio.Lava;
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;

import static me.melijn.jda.Melijn.PREFIX;

public class RestartCommand extends Command {

    public RestartCommand() {
        this.commandName = "restart";
        this.description = "Seeks to the starting position of the playing track";
        this.usage = PREFIX + commandName;
        this.category = Category.MUSIC;
        this.needs = new Need[]{Need.SAME_VOICECHANNEL};
        this.id = 110;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 0)) {
            Lava lava = event.getClient().getMelijn().getLava();
            AudioTrack track = lava.getAudioLoader().getPlayer(event.getGuild()).getAudioPlayer().getPlayingTrack();

            if (track == null) {
                event.reply("There are currently no tracks playing");
                return;
            }

            track.setPosition(0);
            event.reply("The position of the song has been changed to **" +
                    event.getMessageHelper().getDurationBreakdown(0) + "/" +
                    event.getMessageHelper().getDurationBreakdown(track.getDuration()) + "** by **" + event.getFullAuthorName() + "**");

        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
