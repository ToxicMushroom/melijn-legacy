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

package me.melijn.jda.audio;

import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLavalink;
import lavalink.client.player.LavalinkPlayer;
import me.melijn.jda.Melijn;
import me.melijn.jda.blub.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class Lava {

    private final AudioLoader audioLoader;
    private JdaLavalink lavalink = null;

    public Lava(Melijn melijn) {
        audioLoader = new AudioLoader(melijn);
    }

    public void init(JdaLavalink lavalink) {
        this.lavalink = lavalink;
    }

    public LavalinkPlayer createPlayer(long guildId) {
        return lavalink.getLink(String.valueOf(guildId)).getPlayer();
    }

    public void openConnection(VoiceChannel channel) {
        lavalink.getLink(channel.getGuild()).connect(channel);
    }

    /**
     * @param event              This will be used to send replies
     * @param guild              this will be used to check permissions
     * @param senderVoiceChannel this is the voice channel you want to join
     * @return returns true on success and false when failed
     */
    public boolean tryToConnectToVC(CommandEvent event, Guild guild, VoiceChannel senderVoiceChannel) {
        if (!guild.getSelfMember().hasPermission(senderVoiceChannel, Permission.VOICE_CONNECT)) {
            event.reply("I don't have permission to join your Voice Channel");
            return false;
        }
        if (senderVoiceChannel.getUserLimit() == 0 || ((senderVoiceChannel.getUserLimit() > senderVoiceChannel.getMembers().size()) || guild.getSelfMember().hasPermission(senderVoiceChannel, Permission.VOICE_MOVE_OTHERS))) {
            openConnection(senderVoiceChannel);
            return true;
        } else {
            event.reply("Your channel is full. I need the **Move Members** permission to join full channels");
            return false;
        }
    }

    public boolean tryToConnectToVCSilent(VoiceChannel voiceChannel) {
        Guild guild = voiceChannel.getGuild();
        if (!guild.getSelfMember().hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
            return false;
        }
        if (voiceChannel.getUserLimit() == 0 || ((voiceChannel.getUserLimit() > voiceChannel.getMembers().size()) || guild.getSelfMember().hasPermission(voiceChannel, Permission.VOICE_MOVE_OTHERS))) {
            openConnection(voiceChannel);
            return true;
        } else {
            return false;
        }
    }

    public void closeConnection(long guildId) {
        lavalink.getLink(String.valueOf(guildId)).disconnect();
    }

    public boolean isConnected(long guildId) {
        return lavalink.getLink(String.valueOf(guildId)).getState() == Link.State.CONNECTED;
    }

    public VoiceChannel getConnectedChannel(Guild guild) {
        return guild.getSelfMember().getVoiceState().getChannel();
    }

    public JdaLavalink getLavalink() {
        return lavalink;
    }

    public AudioLoader getAudioLoader() {
        return audioLoader;
    }
}
