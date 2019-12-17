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

import me.melijn.jda.audio.MusicPlayer;
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;

import static me.melijn.jda.Melijn.PREFIX;
import static me.melijn.jda.blub.Need.GUILD;
import static me.melijn.jda.blub.Need.SAME_VOICECHANNEL;

public class VolumeCommand extends Command {

    public VolumeCommand() {
        this.commandName = "volume";
        this.usage = PREFIX + commandName + " <0-1000>";
        this.description = "Changes the volume of tracks";
        this.aliases = new String[]{"vol"};
        this.extra = "default: 100 (over 100 will cause distortion)";
        this.needs = new Need[]{GUILD, SAME_VOICECHANNEL};
        this.category = Category.MUSIC;
        this.id = 65;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getGuild().getMember(event.getAuthor()), commandName, 0)) {
            String[] args = event.getArgs().split("\\s+");
            MusicPlayer player = event.getClient().getMelijn().getLava().getAudioLoader().getPlayer(event.getGuild());
            int volume;
            if (args.length == 0 || args[0].isEmpty()) {
                event.reply("Current volume: **" + player.getAudioPlayer().getVolume() + "**");
            } else if (!event.getHelpers().voteChecks || event.getMySQL().getVotesObject(event.getAuthorId()).getLong("streak") > 0) {
                if (!args[0].matches("[0-9]{1,3}|1000")) {
                    event.sendUsage(this, event);
                    return;
                }
                    volume = Integer.parseInt(args[0]);
                    player.getAudioPlayer().setVolume(volume);
                    event.reply("Volume has been set to **" + volume + "**");
            } else {
                event.reply("Sorry this command takes a lot of CPU usage\nYou can still use this command if you support me by voting each day `>vote`\nor you can just right click my name and use the volume slider");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}

