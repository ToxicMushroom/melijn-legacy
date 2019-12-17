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

import net.dv8tion.jda.core.Permission;

import static me.melijn.jda.blub.Category.DEFAULT;

public abstract class Command {

    protected int id = 0;
    protected String commandName = "null";
    protected String description = "no description set";
    protected String usage = "no usage set";
    protected String extra = "";
    protected String[] aliases = new String[0];
    protected Category category = DEFAULT;
    protected Permission[] permissions = new Permission[0];
    protected Need[] needs = new Need[0];

    public String getExtra() {
        return extra;
    }

    public int getId() {
        return id;
    }

    public Permission[] getPermissions() {
        return permissions;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public String[] getAliases() {
        return aliases;
    }

    public Category getCategory() {
        return category;
    }

    protected abstract void execute(CommandEvent event);

    public final void run(CommandEvent event) {
        execute(event);
        //if (event.getClient().getListener() != null) event.getClient().getListener().onCompletedCommand(event, this);
    }


    public boolean isCommandFor(String input) {
        if (commandName.equalsIgnoreCase(input)) return true;
        for (String alias : aliases)
            if (alias.equalsIgnoreCase(input)) return true;
        return false;
    }
}
