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

package me.melijn.jda.blub;

import me.melijn.jda.Helpers;
import me.melijn.jda.db.MySQL;
import me.melijn.jda.db.Variables;
import me.melijn.jda.utils.ImageUtils;
import me.melijn.jda.utils.MessageHelper;
import me.melijn.jda.utils.Private;
import me.melijn.jda.utils.WebUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CommandEvent {

    private final MessageReceivedEvent event;
    private String args;
    private final CommandClient client;
    private String executor;
    private int offset;

    public CommandEvent(MessageReceivedEvent event, String args, CommandClient client, String executor) {
        this.event = event;
        this.args = args == null ? "" : args;
        this.client = client;
        this.executor = executor;
        offset = event.getMessage().getContentRaw().split("\\s+")[0].equalsIgnoreCase("<@" + event.getJDA().getSelfUser().getId() + ">") ? 1 : 0;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public Helpers getHelpers() {
        return client.getMelijn().getHelpers();
    }

    public CommandClient getClient() {
        return client;
    }

    public User getAuthor() {
        return event.getAuthor();
    }

    public Member getMember() {
        return event.getMember();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public void reply(int i) {
        reply(String.valueOf(i));
    }

    public void reply(double d) {
        reply(String.valueOf(d));
    }

    public void reply(long l) {
        reply(String.valueOf(l));
    }

    public void reply(String text) {
        if (text == null || text.isEmpty()) return;
        event.getChannel().sendMessage(text).queue();
    }

    public void reply(String text, Consumer<Message> message) {
        if (text == null || text.isEmpty()) return;
        event.getChannel().sendMessage(text).queue(message);
    }

    public void reply(MessageEmbed embed) {
        if (event.getGuild() == null || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EMBED_LINKS))
            event.getChannel().sendMessage(embed).queue();
        else reply("I don't have permission to send embeds here.");
    }

    public void reply(String text, BufferedImage image) {
        try {
            if (event.getGuild() == null || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES)) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", byteArrayOutputStream);
                if (byteArrayOutputStream.size() > 8_000_000) {
                    reply("The image is bigger then 8MB and cannot be sent");
                    return;
                }
                event.getChannel().sendMessage(text).addFile(byteArrayOutputStream.toByteArray(), "finished.png").queue();
            } else {
                reply("I don't have permission to send images here.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reply(BufferedImage image) {
        try {
            if (event.getGuild() == null || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES)) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", byteArrayOutputStream);
                if (byteArrayOutputStream.size() > 8_000_000) {
                    reply("The image is bigger then 8MB and cannot be sent");
                    return;
                }
                event.getChannel().sendFile(byteArrayOutputStream.toByteArray(), "finished.png").queue();
            } else {
                reply("I don't have permission to send images here.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFullAuthorName() {
        return getAuthor().getName() + "#" + getAuthor().getDiscriminator();
    }

    public long getAuthorId() {
        return getAuthor().getIdLong();
    }

    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }

    public String getAvatarUrl() {
        return getAuthor().getEffectiveAvatarUrl();
    }

    public String getBotName() {
        return getJDA().getSelfUser().getName();
    }

    public String getExecutor() {
        return executor;
    }

    public int getOffset() {
        return offset;
    }

    public long getGuildId() {
        return getGuild().getIdLong();
    }

    public void sendUsage(Command command, CommandEvent event) {
        event.getClient().getMelijn().getMessageHelper().sendUsage(command, event);
    }

    public boolean hasPerm(Member member, String commandName, int i) {
        return client.getMelijn().getHelpers().hasPerm(member, commandName, i);
    }

    public MessageHelper getMessageHelper() {
        return client.getMelijn().getMessageHelper();
    }

    public WebUtils getWebUtils() {
        return client.getMelijn().getWebUtils();
    }

    public Private getPrivate() {
        return client.getMelijn().getPrivate();
    }

    public MySQL getMySQL() {
        return client.getMelijn().getMySQL();
    }

    public Variables getVariables() {
        return client.getMelijn().getVariables();
    }

    public ImageUtils getImageUtils() {
        return client.getMelijn().getImageUtils();
    }

    public void async(Runnable runnable) {
        client.getMelijn().getTaskManager().async(runnable);
    }

    public void async(Runnable runnable, int initialMillis) {
        client.getMelijn().getTaskManager().async(runnable, initialMillis);
    }
}
