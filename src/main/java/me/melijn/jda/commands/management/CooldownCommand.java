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
import me.melijn.jda.db.Variables;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static me.melijn.jda.Melijn.PREFIX;

public class CooldownCommand extends Command {

    public CooldownCommand() {
        this.commandName = "cooldown";
        this.usage = PREFIX + commandName + " <set | remove | list>";
        this.description = "Main command to configure cooldowns for each command";
        this.extra = "Users with the bypass.cooldown permission will not be affected by cooldowns";
        this.category = Category.MANAGEMENT;
        this.needs = new Need[]{Need.GUILD};
        this.id = 108;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            Variables vars = event.getVariables();
            if (event.getArgs().isEmpty()) {
                event.getMessageHelper().sendUsage(this, event);
                return;
            }
            switch (args[0].toLowerCase()) {
                case "set":
                    if (args.length != 3) {
                        event.reply(event.getVariables().prefixes.get(event.getGuildId()) + commandName + " set <command | category | all> <1-30000 (milliseconds)>");
                        return;
                    }
                    List<Command> matches = event.getClient().getCommands().stream()
                            .filter(cmd ->
                                    cmd.getCommandName().equalsIgnoreCase(args[1]) ||
                                    Arrays.asList(cmd.getAliases()).contains(args[1]) ||
                                    cmd.getCategory().toString().equalsIgnoreCase(args[1]) ||
                                    args[1].equalsIgnoreCase("all"))
                            .collect(Collectors.toList());
                    if (matches.size() == 0) {
                        event.reply("Unknown commandName or alias\nTip: check https://melijn.com/commands for commandNames");
                        return;
                    }
                    if (!args[2].matches("\\d{1,5}") || Integer.parseInt(args[2]) > 30_000 || Integer.parseInt(args[2]) < 1) {
                        event.reply("Your input was not a number or was not in range of **1-30000** (milliseconds)");
                        return;
                    }
                    event.async(() -> {
                        event.getMySQL().setCooldown(event.getGuildId(), matches, Integer.parseInt(args[2]));
                        Map<Integer, Integer> map = vars.cooldowns.get(event.getGuildId());
                        matches.forEach(command -> map.put(command.getId(), Integer.parseInt(args[2])));
                        vars.cooldowns.put(event.getGuildId(), map);
                    });
                    event.reply("The cooldown of **" + args[1] + "** has been set to **" + Integer.parseInt(args[2]) + "ms**");
                    break;
                case "remove":
                    if (args.length != 2) {
                        event.reply(event.getVariables().prefixes.get(event.getGuildId()) + commandName + " remove <command | category | all>");
                        return;
                    }
                    matches = event.getClient().getCommands().stream()
                            .filter(cmd ->
                                    cmd.getCommandName().equalsIgnoreCase(args[1]) ||
                                    Arrays.asList(cmd.getAliases()).contains(args[1]) ||
                                    cmd.getCategory().toString().equalsIgnoreCase(args[1]) ||
                                    args[1].equalsIgnoreCase("all"))
                            .collect(Collectors.toList());
                    if (matches.size() == 0) {
                        event.reply("Unknown commandName or alias\nTip: check https://melijn.com/commands for commandNames");
                        return;
                    }
                    event.async(() -> {
                        event.getMySQL().removeCooldown(event.getGuildId(), matches);
                        Map<Integer, Integer> map = vars.cooldowns.get(event.getGuildId());
                        matches.forEach(cmd -> map.remove(cmd.getId()));
                        vars.cooldowns.put(event.getGuildId(), map);
                    });
                    event.reply("The cooldown of **" + args[1] + "** has been removed");
                    break;
                case "list":
                    Map<Integer, Integer> map = vars.cooldowns.get(event.getGuildId());
                    StringBuilder sb = new StringBuilder();
                    AtomicInteger i = new AtomicInteger(1);
                    map.forEach((a, b) -> {
                        List<Command> match = event.getClient().getCommands().stream().filter(cmd -> cmd.getId() == a).collect(Collectors.toList());
                        if (match.size() > 0)
                            sb.append(i.getAndIncrement()).append(". ").append(match.get(0).getCommandName()).append(" ").append(b).append("ms\n");
                    });
                    if (sb.length() == 0) event.reply("Looks empty to me");
                    else event.getMessageHelper().sendSplitCodeBlock(event.getTextChannel(), sb.toString(), "Markdown");
                    break;
                default:
                    event.sendUsage(this, event);
                    break;
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
