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
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;
import me.melijn.jda.utils.Embedder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

import static me.melijn.jda.Melijn.PREFIX;

public class NowPlayingCommand extends Command {

    public NowPlayingCommand() {
        this.commandName = "np";
        this.description = "Shows you the current track";
        this.usage = PREFIX + commandName;
        this.aliases = new String[]{"nowplaying", "playing", "nplaying", "nowp"};
        this.category = Category.MUSIC;
        this.needs = new Need[]{Need.GUILD, Need.VOICECHANNEL};
        this.permissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.id = 72;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getGuild().getMember(event.getAuthor()), commandName, 0)) {
            Guild guild = event.getGuild();
            LavalinkPlayer audioPlayer = event.getClient().getMelijn().getLava().getAudioLoader().getPlayer(guild).getAudioPlayer();
            AudioTrack track = audioPlayer.getPlayingTrack();
            String s = audioPlayer.isPaused() ? "paused" : "playing";
            if (track == null) {
                event.reply("There are no tracks being played");
                return;
            }

            String loopedQueue = event.getVariables().loopedQueues.contains(guild.getIdLong()) ? " \uD83D\uDD01" : "";
            String looped = event.getVariables().looped.contains(guild.getIdLong()) ? " \uD83D\uDD04" : "";
            event.reply(new Embedder(event.getVariables(), event.getGuild())
                    .setTitle("Now " + s)
                    .setThumbnail(event.getMessageHelper().getThumbnailURL(track.getInfo().uri))
                    .setDescription("[**" + event.getMessageHelper().escapeMarkDown(track.getInfo().title) + "**](" + track.getInfo().uri + ")")
                    .addField("status:", (s.equalsIgnoreCase("playing") ? "\u25B6" : "\u23F8") + looped + loopedQueue, false)
                    .addField("progress:", event.getMessageHelper().progressBar(audioPlayer), false)
                    .setFooter(event.getHelpers().getFooterStamp(), event.getHelpers().getFooterIcon()).build());

        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
