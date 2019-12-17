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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.LavalinkPlayer;
import me.melijn.jda.Melijn;

public class MusicPlayer {

    private final LavalinkPlayer player;
    private final TrackManager manager;
    private final long guildId;
    private final Lava lava;

    public MusicPlayer(Melijn melijn, long guildId) {
        this.lava = melijn.getLava();
        this.player = lava.createPlayer(guildId);
        this.guildId = guildId;
        manager = new TrackManager(melijn, player, this);
        player.addListener(manager);
    }

    public LavalinkPlayer getAudioPlayer() {
        return player;
    }

    public long getGuildId() {
        return guildId;
    }

    public TrackManager getTrackManager() {
        return manager;
    }

    public synchronized void queue(AudioTrack track) {
        manager.queue(track);
    }

    public synchronized void resumeTrack() {
        player.setPaused(false);
    }

    public synchronized void stopTrack() {
        player.stopTrack();
        lava.getLavalink().getLink(String.valueOf(guildId)).disconnect();
    }

    public synchronized void skipTrack() {
        player.stopTrack();
        manager.nextTrack(getAudioPlayer().getPlayingTrack());
    }

    public synchronized boolean isPaused() {
        return player.isPaused();
    }

}
