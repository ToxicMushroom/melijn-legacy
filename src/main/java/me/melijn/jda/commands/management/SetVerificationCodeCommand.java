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

import static me.melijn.jda.Melijn.PREFIX;

public class SetVerificationCodeCommand extends Command {


    public SetVerificationCodeCommand() {
        this.commandName = "setVerificationCode";
        this.usage = PREFIX + commandName + " [code | null]";
        this.description = "Sets the VerificationCode that members will have to send in the VerificationChannel in order to get verified";
        this.category = Category.MANAGEMENT;
        this.needs = new Need[]{Need.GUILD};
        this.id = 7;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            Guild guild = event.getGuild();
            if (guild.getTextChannelById(event.getVariables().verificationChannelsCache.get(guild.getIdLong())) == null) {
                event.reply("" +
                        "You first have to setup a Verification TextChannel\n" +
                        "You'll probably want to follow this guide: https://melijn.com/guides/guide-7"
                );
                return;
            }
            String[] args = event.getArgs().split("\\s+");
            if (args.length > 0 && !args[0].isEmpty()) {
                if (args[0].equalsIgnoreCase("null")) {
                    event.async(() -> {
                        event.getMySQL().removeVerificationCode(guild.getIdLong());
                        event.getVariables().verificationCodeCache.invalidate(guild.getIdLong());
                    });
                    event.reply("The VerificationCode has been set to nothing by **" + event.getFullAuthorName() + "**");
                } else {
                    event.async(() -> {
                        event.getMySQL().setVerificationCode(guild.getIdLong(), args[0]);
                        event.getVariables().verificationCodeCache.put(guild.getIdLong(), args[0]);
                    });
                    event.reply("The VerificationCode has been set to " + args[0] + " by **" + event.getFullAuthorName() + "**");
                }
            } else {
                String value = (event.getVariables().verificationCodeCache.get(guild.getIdLong()) == null ?
                        "unset" :
                        event.getVariables().verificationCodeCache.get(guild.getIdLong()));
                event.reply("The VerificationCode is **" + value + "**");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
