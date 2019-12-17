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

import me.melijn.jda.blub.*;

import static me.melijn.jda.Melijn.PREFIX;

public class SetVerificationTypeCommand extends Command {

    public SetVerificationTypeCommand() {
        this.commandName = "setVerificationType";
        this.usage = PREFIX + commandName + " [code | reCaptcha]";
        this.description = "Sets the verification type";
        this.extra = "reCaptcha uses the melijn site";
        this.aliases = new String[]{"svtype"};
        this.category = Category.MANAGEMENT;
        this.needs = new Need[]{Need.GUILD};
        this.id = 109;
    }

    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            if (args.length == 0 || args[0].isEmpty()) {
                String value = event.getVariables().verificationTypes.get(event.getGuildId()).name().toLowerCase();
                event.reply("The verification mode is set to **" + value + "**");
                return;
            }
            if (args[0].equalsIgnoreCase("code")) {
                event.getVariables().verificationTypes.put(event.getGuildId(), VerificationType.CODE);
                event.getMySQL().setVerificationType(event.getGuildId(), VerificationType.CODE);
                event.reply("The verification type has been set to **" + VerificationType.CODE.toString().toLowerCase() + "**");
            } else if (args[0].equalsIgnoreCase("reCaptcha")) {
                event.getVariables().verificationTypes.put(event.getGuildId(), VerificationType.RECAPTCHA);
                event.getMySQL().setVerificationType(event.getGuildId(), VerificationType.RECAPTCHA);
                event.reply("The verification type has been set to **" + VerificationType.RECAPTCHA.toString().toLowerCase() + "**");
            } else {
                event.sendUsage(this, event);
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
