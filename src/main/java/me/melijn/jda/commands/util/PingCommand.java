package me.melijn.jda.commands.util;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;

import java.time.temporal.ChronoUnit;

import static me.melijn.jda.Melijn.PREFIX;

public class PingCommand extends Command {

    public PingCommand() {
        this.commandName = "ping";
        this.description = "Shows the bot's ping";
        this.usage = PREFIX + commandName;
        this.category = Category.UTILS;
        this.id = 57;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            event.getChannel().sendMessage("Pinging... ").queue((m) ->
                    m.editMessage("\uD83C\uDFD3 Ping: " +
                            event.getMessage().getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS) + "ms | " + "Websocket: " + event.getJDA().getPing() + "ms").queue());
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
