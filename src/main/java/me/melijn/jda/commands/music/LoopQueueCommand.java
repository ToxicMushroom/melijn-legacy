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

package me.melijn.jda.commands.music;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;

import static me.melijn.jda.Melijn.PREFIX;

public class LoopQueueCommand extends Command {



    public LoopQueueCommand() {
        this.commandName = "loopQueue";
        this.description = "Changes the looping state of entire the queue";
        this.usage = PREFIX + commandName + " [false/off/yes | true/on/off]";
        this.extra = "When a track finishes playing or gets skipped it will move to the bottom of the queue";
        this.aliases = new String[]{"repeatq", "loopq"};
        this.needs = new Need[]{Need.GUILD, Need.SAME_VOICECHANNEL};
        this.category = Category.MUSIC;
        this.id = 31;
    }

    @Override
    protected void execute(CommandEvent event) {
        LoopCommand.executorLoops(this, event, event.getVariables().loopedQueues);
    }
}
