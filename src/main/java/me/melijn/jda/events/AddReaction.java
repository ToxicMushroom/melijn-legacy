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

package me.melijn.jda.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.melijn.jda.Melijn;
import me.melijn.jda.audio.MusicPlayer;
import me.melijn.jda.utils.Embedder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AddReaction extends ListenerAdapter {

    private final Melijn melijn;

    public AddReaction(Melijn melijn) {
        this.melijn = melijn;
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getGuild() == null || melijn.getVariables().blockedGuildIds.contains(event.getGuild().getIdLong()))
            return;
        Guild guild = event.getGuild();
        if (melijn.getVariables().blockedUserIds.contains(guild.getOwnerIdLong())) return;
        if (melijn.getVariables().userMessageToAnswer.containsKey(event.getUser().getIdLong()) &&
                melijn.getVariables().userMessageToAnswer.get(event.getUser().getIdLong()) == event.getMessageIdLong()) {
            MusicPlayer player = melijn.getLava().getAudioLoader().getPlayer(event.getGuild());
            if (event.getReactionEmote().getName().equalsIgnoreCase("✅")) {
                List<AudioTrack> tracks = melijn.getVariables().userRequestedSongs.get(event.getUser().getIdLong());
                StringBuilder songs = new StringBuilder();
                int i = player.getTrackManager().getTrackSize();
                int t = 0;
                for (AudioTrack track : tracks) {
                    i++;
                    player.queue(track);
                    songs.append("[#").append(i).append("](").append(track.getInfo().uri).append(") - ").append(track.getInfo().title).append("\n");
                    if (songs.length() > 1700) {
                        t++;
                        EmbedBuilder eb = new Embedder(melijn.getVariables(), event.getGuild());
                        eb.setTitle("Added part **#" + t + "**");
                        eb.setFooter(melijn.getHelpers().getFooterStamp(), melijn.getHelpers().getFooterIcon());
                        eb.setDescription(songs);
                        event.getChannel().sendMessage(eb.build()).queue();
                        songs = new StringBuilder();
                    }
                }
                if (t == 0) {
                    EmbedBuilder eb = new Embedder(melijn.getVariables(), event.getGuild());
                    eb.setTitle("Added");
                    eb.setFooter(melijn.getHelpers().getFooterStamp(), melijn.getHelpers().getFooterIcon());
                    eb.setDescription(songs);
                    event.getChannel().sendMessage(eb.build()).queue();
                } else {
                    t++;
                    EmbedBuilder eb = new Embedder(melijn.getVariables(), event.getGuild());
                    eb.setTitle("Added part **#" + t + "**");
                    eb.setFooter(melijn.getHelpers().getFooterStamp(), melijn.getHelpers().getFooterIcon());
                    eb.setDescription(songs);
                    event.getChannel().sendMessage(eb.build()).queue();
                }
                event.getChannel().getMessageById(melijn.getVariables().userMessageToAnswer.get(event.getUser().getIdLong())).queue(message ->
                        message.delete().queue(), f -> LoggerFactory.getLogger(this.getClass()).info("72")
                );
                melijn.getVariables().userMessageToAnswer.remove(event.getUser().getIdLong());
                melijn.getVariables().userRequestedSongs.remove(event.getUser().getIdLong());
            } else if (event.getReactionEmote().getName().equalsIgnoreCase("❎")) {
                event.getChannel().getMessageById(melijn.getVariables().userMessageToAnswer.get(event.getUser().getIdLong())).queue(message -> message.delete().queue(), f -> LoggerFactory.getLogger(this.getClass()).info("76"));
                melijn.getVariables().userMessageToAnswer.remove(event.getUser().getIdLong());
                melijn.getVariables().userRequestedSongs.remove(event.getUser().getIdLong());
            }
        }
        if (melijn.getVariables().usersFormToReply.containsKey(event.getUser().getIdLong()) && melijn.getVariables().usersFormToReply.get(event.getUser().getIdLong()) == event.getMessageIdLong()) {
            MusicPlayer player = melijn.getLava().getAudioLoader().getPlayer(event.getGuild());
            AudioTrack track;
            EmbedBuilder eb = new Embedder(melijn.getVariables(), event.getGuild());
            eb.setTitle("Added");
            eb.setFooter(melijn.getHelpers().getFooterStamp(), null);
            boolean wrongemote = false;
            switch (event.getReactionEmote().getName()) {
                case "\u0031\u20E3":
                    track = melijn.getVariables().userChoices.get(event.getUser().getIdLong()).get(0);
                    player.queue(track);
                    eb.setDescription("**[" + track.getInfo().title + "](" + track.getInfo().uri + ")** is queued at position **#" + player.getTrackManager().getTrackSize() + "**");
                    event.getChannel().getMessageById(event.getMessageId()).queue(s -> s.editMessage(eb.build()).queue());
                    break;
                case "\u0032\u20E3":
                    track = melijn.getVariables().userChoices.get(event.getUser().getIdLong()).get(1);
                    player.queue(track);
                    eb.setDescription("**[" + track.getInfo().title + "](" + track.getInfo().uri + ")** is queued at position **#" + player.getTrackManager().getTrackSize() + "**");
                    event.getChannel().getMessageById(event.getMessageId()).queue(s -> s.editMessage(eb.build()).queue());
                    break;
                case "\u0033\u20E3":
                    track = melijn.getVariables().userChoices.get(event.getUser().getIdLong()).get(2);
                    player.queue(track);
                    eb.setDescription("**[" + track.getInfo().title + "](" + track.getInfo().uri + ")** is queued at position **#" + player.getTrackManager().getTrackSize() + "**");
                    event.getChannel().getMessageById(event.getMessageId()).queue(s -> s.editMessage(eb.build()).queue());
                    break;
                case "\u0034\u20E3":
                    track = melijn.getVariables().userChoices.get(event.getUser().getIdLong()).get(3);
                    player.queue(track);
                    eb.setDescription("**[" + track.getInfo().title + "](" + track.getInfo().uri + ")** is queued at position **#" + player.getTrackManager().getTrackSize() + "**");
                    event.getChannel().getMessageById(event.getMessageId()).queue(s -> s.editMessage(eb.build()).queue());
                    break;
                case "\u0035\u20E3":
                    track = melijn.getVariables().userChoices.get(event.getUser().getIdLong()).get(4);
                    player.queue(track);
                    eb.setDescription("**[" + track.getInfo().title + "](" + track.getInfo().uri + ")** is queued at position **#" + player.getTrackManager().getTrackSize() + "**");
                    event.getChannel().getMessageById(event.getMessageId()).queue(s -> s.editMessage(eb.build()).queue());
                    break;
                case "\u274E":
                    melijn.getVariables().usersFormToReply.remove(event.getUser().getIdLong());
                    melijn.getVariables().userChoices.remove(event.getUser().getIdLong());
                    event.getChannel().getMessageById(event.getMessageId()).queue(message -> message.delete().queue());
                    wrongemote = true;
                    break;
                default:
                    wrongemote = true;
                    break;

            }
            if (!wrongemote) {
                event.getChannel().getMessageById(event.getMessageId()).queue((s) -> {
                    if (s.getGuild() != null && s.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE))
                        s.clearReactions().queue();
                }, f -> LoggerFactory.getLogger(this.getClass()).error("133"));
            }
        }
        if (melijn.getVariables().possibleDeletes.containsKey(event.getGuild().getIdLong())) {
            Map<Long, Long> messageChannel = melijn.getVariables().possibleDeletes.get(guild.getIdLong());
            if (melijn.getVariables().messageUser.keySet().contains(event.getMessageIdLong())
                    && melijn.getVariables().messageUser.get(event.getMessageIdLong()) == event.getUser().getIdLong()
                    && melijn.getHelpers().hasPerm(event.getGuild().getMember(event.getUser()), "clearChannel", 1)
                    && event.getReactionEmote().getEmote() != null)
                switch (event.getReactionEmote().getEmote().getId()) {
                    case "463250265026330634"://yes
                        TextChannel toDelete = event.getGuild().getTextChannelById(melijn.getVariables().possibleDeletes.get(event.getGuild().getIdLong()).get(event.getMessageIdLong()));
                        toDelete.createCopy().queue(s -> guild.getController().modifyTextChannelPositions().selectPosition((TextChannel) s).moveTo(toDelete.getPosition()).queue(done -> {
                            toDelete.delete().queue();
                            ((TextChannel) s).sendMessage("**#" + toDelete.getName() + "** has been cleared")
                                    .queue(message ->
                                            message.delete().queueAfter(3, TimeUnit.SECONDS, null, (failure) -> {
                                            }));
                        }));
                        removeMenu(event, guild, messageChannel);
                        break;
                    case "463250264653299713"://no
                        removeMenu(event, guild, messageChannel);
                        break;
                    default:
                        break;
                }
        }
        if (guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES) &&
                melijn.getVariables().selfRolesChannels.get(guild.getIdLong()) == event.getChannel().getIdLong())
            melijn.getTaskManager().async(() -> {
                Map<Long, String> roles = melijn.getMySQL().getSelfRoles(guild.getIdLong());
                roles.forEach((key, value) -> {
                    if (!value.equals(event.getReactionEmote().getId()) && !value.equals(event.getReactionEmote().getName()))
                        return;
                    Role role = guild.getRoleCache().getElementById(key);
                    if (role == null) return;

                    if (guild.getSelfMember().canInteract(role))
                        guild.getController().addSingleRoleToMember(event.getMember(), role).reason("SelfRole added").queue();
                });
            });
        if (melijn.getVariables().reactionLogChannelCache.get(guild.getIdLong()) != -1L) {
            TextChannel textChannel = guild.getTextChannelById(melijn.getVariables().reactionLogChannelCache.get(guild.getIdLong()));
            User user = event.getUser();
            MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
            if (textChannel != null && guild.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE)) {
                textChannel.sendMessage(new EmbedBuilder()
                        .setTitle("Reaction added in #" + event.getChannel().getName() + " ".repeat(80).substring(0, 55 - user.getName().length()) + "\u200B")
                        .setColor(Color.WHITE)
                        .setDescription("```LDIF" +
                                "\nUserID: " + user.getName() + "#" + user.getDiscriminator() +
                                "\nMessageID: " + event.getMessageIdLong() +
                                "\n" + (reactionEmote.isEmote() ? "Emote" : "Emoji") + ": " + event.getReactionEmote().getName() +
                                (reactionEmote.isEmote() ? "\nEmoteId: " + reactionEmote.getId() : "") +
                                "\nMoment: " + melijn.getMessageHelper().millisToDate(System.currentTimeMillis()) +
                                "\n```")

                        .setThumbnail(reactionEmote.isEmote() ? reactionEmote.getEmote().getImageUrl() + "?size=2048" : null)
                        .setFooter("Reacted by: " + user.getName() + "#" + user.getDiscriminator(), user.getEffectiveAvatarUrl())
                        .build()
                ).queue();
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        Guild guild = event.getGuild();
        if (guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES) &&
                melijn.getVariables().selfRolesChannels.get(guild.getIdLong()) == event.getChannel().getIdLong())
            melijn.getTaskManager().async(() -> {
                Map<Long, String> roles = melijn.getMySQL().getSelfRoles(guild.getIdLong());
                roles.forEach((key, value) -> {
                    if (!value.equals(event.getReactionEmote().getId()) && !value.equals(event.getReactionEmote().getName()))
                        return;
                    Role role = guild.getRoleCache().getElementById(key);
                    if (role == null) return;

                    if (guild.getSelfMember().canInteract(role))
                        guild.getController().removeSingleRoleFromMember(event.getMember(), role).reason("SelfRole added").queue();
                });
            });
    }

    private void removeMenu(GuildMessageReactionAddEvent event, Guild guild, Map<Long, Long> messageChannel) {
        event.getChannel().getMessageById(event.getMessageId()).queue(s -> {
            s.delete().queue();
            messageChannel.remove(event.getMessageIdLong());
            melijn.getVariables().possibleDeletes.put(guild.getIdLong(), messageChannel);
            melijn.getVariables().messageUser.remove(event.getMessageIdLong());
        }, f -> LoggerFactory.getLogger(this.getClass()).error("203"));
    }
}
