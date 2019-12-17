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

import me.melijn.jda.Melijn;

import java.util.HashSet;
import java.util.Set;

public class CommandClientBuilder {

    private final long ownerId;
    private final Set<Command> commands = new HashSet<>();
    private final Melijn melijn;

    public CommandClientBuilder(Melijn melijn, long ownerId) {
        this.melijn = melijn;
        this.ownerId = ownerId;
    }

    public CommandClient build() {
        return new CommandClientImpl(melijn, ownerId, commands);
    }

    public CommandClientBuilder addCommand(Command command) {
        commands.add(command);
        melijn.getMySQL().addCommand(command);
        return this;
    }
}