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

package me.melijn.jda.commands.developer;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static me.melijn.jda.Melijn.PREFIX;

public class JSEvalCommand extends Command {

    public JSEvalCommand() {
        this.commandName = "jsEval";
        this.description = "evaluates javascript code";
        this.usage = PREFIX + commandName + " [crappy javascript code]";
        this.aliases = new String[]{"javascriptEval"};
        this.category = Category.DEVELOPER;
        this.id = 114;
    }

    @Override
    protected void execute(CommandEvent event) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
        event.getHelpers().eval(event, engine, "js");
    }
}
