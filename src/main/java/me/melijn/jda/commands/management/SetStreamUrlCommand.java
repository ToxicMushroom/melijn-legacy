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

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

import static me.melijn.jda.Melijn.PREFIX;

public class SetStreamUrlCommand extends Command {

    private Map<String, String> linkjes = new HashMap<>();

    public SetStreamUrlCommand() {
        this.commandName = "setStreamUrl";
        this.description = "Sets the StreamUrl";
        this.usage = PREFIX + commandName + " [list | url]";
        this.aliases = new String[]{"ssu"};
        this.needs = new Need[]{Need.GUILD};
        this.extra = "https://melijn.com/guides/guide-5/";
        this.category = Category.MANAGEMENT;
        this.id = 84;

        linkjes.put("slam-nonstop", "http://stream.radiocorp.nl/web10_mp3");
        linkjes.put("radio538", "http://18973.live.streamtheworld.com/RADIO538.mp3");
        linkjes.put("Joe-fm", "http://icecast-qmusic.cdp.triple-it.nl/JOEfm_be_live_128.mp3");
        linkjes.put("mnm", "http://icecast.vrtcdn.be/mnm-high.mp3");
        linkjes.put("mnm-hits", "http://icecast.vrtcdn.be/mnm_hits-high.mp3");
        linkjes.put("Q-music", "http://icecast-qmusic.cdp.triple-it.nl/Qmusic_be_live_64.aac");
        linkjes.put("Nostalgie", "http://nostalgiewhatafeeling.ice.infomaniak.ch/nostalgiewhatafeeling-128.mp3");
        linkjes.put("Radio1", "http://icecast.vrtcdn.be/radio1-high.mp3");
        linkjes.put("Radio2", "http://icecast.vrtcdn.be/ra2wvl-high.mp3");
        linkjes.put("Studio-Brussel", "http://icecast.vrtcdn.be/stubru-high.mp3");
        linkjes.put("BBC_Radio_1", "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio1_mf_p");
        linkjes.put("BBC_Radio_4FM", "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio4fm_mf_p");
        linkjes.put("BBC_Radio_6_Music", "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_6music_mf_p");
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            Guild guild = event.getGuild();
            String[] args = event.getArgs().split("\\s+");
            String url = event.getMySQL().getStreamUrl(guild.getIdLong());
            if ("".equals(url)) url = "nothing";
            if (args.length == 0 || "".equals(args[0])) {
                event.reply("StreamURL: " + url);
            } else if (args.length == 1) {
                if (args[0].contains("http://") || args[0].contains("https://")) {
                    event.async(() -> event.getMySQL().setStreamUrl(guild.getIdLong(), args[0]));
                    event.reply("Changed the url from **" + url + "** to **" + args[0] + "**");
                    return;
                }
                if (args[0].equalsIgnoreCase("list")) {
                    event.reply("**Radio**\n" + linkjes.keySet().toString().replaceAll("(,\\s+|,)", "\n+ ").replaceFirst("\\[", "+ ").replaceFirst("]", ""));
                    return;
                }
                boolean match = false;
                for (String key : linkjes.keySet()) {
                    if (key.equalsIgnoreCase(args[0])) {
                        event.async(() -> event.getMySQL().setStreamUrl(guild.getIdLong(), linkjes.get(key)));
                        event.reply("Changed the url from **" + url + "** to **" + linkjes.get(key) + "**");
                        match = true;
                    }
                }
                if (!match) event.sendUsage(this, event);
            } else {
                event.sendUsage(this, event);
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
