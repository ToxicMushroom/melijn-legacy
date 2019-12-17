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

package me.melijn.jda.commands.util;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.utils.TableBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;

import java.util.List;

import static me.melijn.jda.Melijn.PREFIX;

public class ShardsCommand extends Command {

    public ShardsCommand() {
        this.commandName = "shards";
        this.usage = PREFIX + commandName;
        this.description = "Shows you all the shards with some information about them";
        this.category = Category.UTILS;
        this.id = 9;
    }

    /* CREDITS TO DUNCTE123 FOR DESIGN */

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            ShardManager shardManager = event.getJDA().asBot().getShardManager();
            TableBuilder tableBuilder = new TableBuilder(true).setColumns(List.of("Shard ID", "Ping", "Users", "Guilds", "VCs"));

            int avgping = 0;
            int vcs = 0;
            for (int id = 0; id < shardManager.getShardsTotal(); id++) {
                JDA jda = event.getJDA().asBot().getShardManager().getShardById(id);
                String shardInfo = event.getJDA().getShardInfo().getShardId() == id ? " (current)" : "";
                avgping += jda.getPing();

                long jvcs = jda.getVoiceChannels().stream().filter(
                        (vc) -> vc.getMembers().contains(vc.getGuild().getSelfMember())
                ).count();
                vcs += jvcs;
                tableBuilder.addRow(List.of(id + shardInfo, String.valueOf(jda.getPing()), String.valueOf(jda.getUserCache().size()), String.valueOf(jda.getGuildCache().size()), String.valueOf(jvcs)));
            }
            avgping = avgping/shardManager.getShardsTotal();
            tableBuilder.setFooterRow(List.of("Sum/Avg", String.valueOf(avgping), String.valueOf(shardManager.getUserCache().size()), String.valueOf(shardManager.getGuildCache().size()), String.valueOf(vcs)));

            for (String part : tableBuilder.build()) {
                event.reply(part);
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
