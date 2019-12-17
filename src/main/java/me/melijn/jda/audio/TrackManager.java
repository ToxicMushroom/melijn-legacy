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

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.AudioEventAdapterWrapped;
import me.melijn.jda.Melijn;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackManager extends AudioEventAdapterWrapped {

    public final Queue<AudioTrack> tracks = new LinkedList<>();
    private final LavalinkPlayer player;
    private final MusicPlayer musicPlayer;
    private final Melijn melijn;

    public TrackManager(Melijn melijn, LavalinkPlayer player, MusicPlayer musicPlayer) {
        this.player = player;
        this.musicPlayer = musicPlayer;
        this.melijn = melijn;
    }

    public Queue<AudioTrack> getTracks() {
        return tracks;
    }

    public int getTrackSize() {
        return tracks.size();
    }

    public void nextTrack(AudioTrack lastTrack) {
        if (tracks.isEmpty()) {
            player.getLink().disconnect();
            return;
        }
        if (player == null) return;
        AudioTrack track = tracks.poll();
        if (track.equals(lastTrack))
            player.playTrack(track.makeClone());
        else player.playTrack(track);
    }

    public void queue(AudioTrack track) {
        if (player.getPlayingTrack() == null) {
            player.playTrack(track);
        } else {
            tracks.offer(track);
        }
    }

    public void shuffle() {
        Collections.shuffle((List<?>) tracks);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        melijn.getHelpers().postMusicLog(musicPlayer.getGuildId(), track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        long guildId = this.player.getLink().getGuildIdLong();
        if (melijn.getVariables().looped.contains(guildId)) {
            melijn.getLava().getAudioLoader().loadSimpleTrack(this.musicPlayer, track.getInfo().uri);
        } else if (melijn.getVariables().loopedQueues.contains(guildId)) {
            if (endReason.mayStartNext) nextTrack(track);
            melijn.getLava().getAudioLoader().loadSimpleTrack(this.musicPlayer, track.getInfo().uri);
        } else {
            if (endReason.mayStartNext) nextTrack(track);
        }
    }

    public void clear() {
        tracks.clear();
    }
}
