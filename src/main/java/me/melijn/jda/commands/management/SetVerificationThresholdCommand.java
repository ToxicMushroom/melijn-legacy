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

public class SetVerificationThresholdCommand extends Command {


    public SetVerificationThresholdCommand() {
        this.commandName = "setVerificationThreshold";
        this.usage = PREFIX + commandName + " <0 - 20>";
        this.description = "Sets the VerificationThreshold aka the amount of times the unverified member can try the code before being kicked";
        this.aliases = new String[]{"svthreshold"};
        this.needs = new Need[]{Need.GUILD};
        this.category = Category.MANAGEMENT;
        this.extra = "0 disables the threshold any higher number is the amount of times the user can answer incorrect before getting kicked";
        this.id = 5;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            Guild guild = event.getGuild();
            if (args.length > 0 && args[0].matches("^([0-1]?[0-9]|20)$")) {
                int i = Integer.parseInt(args[0]);
                if (i == 0) {
                    event.async(() -> {
                        event.getMySQL().removeVerificationThreshold(guild.getIdLong());
                        event.getVariables().verificationThresholdCache.invalidate(guild.getIdLong());
                    });
                    event.reply("The VerificationThreshold has been disabled by **" + event.getFullAuthorName() + "**");
                } else {
                    event.async(() -> {
                        event.getMySQL().setVerificationThreshold(guild.getIdLong(), i);
                        event.getVariables().verificationThresholdCache.put(guild.getIdLong(), i);
                    });
                    event.reply("The VerificationThreshold has been set to **" + i + "** by **" + event.getFullAuthorName() + "**");
                }
            } else {
                String value = event.getVariables().verificationThresholdCache.get(guild.getIdLong()) == 0 ?
                        "disabled" :
                        String.valueOf(event.getVariables().verificationThresholdCache.get(guild.getIdLong()));
                event.reply("The VerificationThreshold is **" + value + "**");
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
