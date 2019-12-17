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
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.ISimpleCompiler;

import java.lang.reflect.Method;

import static me.melijn.jda.Melijn.PREFIX;

public class JEvalCommand extends Command {

    private final String CLASS_NAME = "EvalTempClass";

    public JEvalCommand() {
        this.commandName = "jEval";
        this.description = "evaluates java code";
        this.usage = PREFIX + commandName + " [insert crappy code]";
        this.category = Category.DEVELOPER;
        this.id = 27;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.async(() -> {
            try {
                evaluate(event.getArgs(), event);
            } catch (Exception e) {
                event.getClient().getMelijn().getMessageHelper().sendSplitMessage(event.getTextChannel(), "```" + e.getMessage() + "```");
            }
        });

    }

    private void evaluate(final String source, CommandEvent event) throws Exception {
        final ISimpleCompiler compiler = CompilerFactoryFactory.getDefaultCompilerFactory().newSimpleCompiler();
        compiler.cook(createDummyClassSource(source));
        evaluateDummyClassMethod(event, compiler.getClassLoader());
    }

    private String createDummyClassSource(final String source) {
        return  "import me.melijn.jda.blub.CommandEvent;\n" +
                "import java.io.*;\n" +
                "import java.lang.*;\n" +
                "import java.util.*;\n" +
                "import java.util.concurrent.*;\n" +
                "import net.dv8tion.jda.core.*;\n" +
                "import net.dv8tion.jda.core.entities.*;\n" +
                "import net.dv8tion.jda.core.entities.impl.*;\n" +
                "import net.dv8tion.jda.core.managers.*;\n" +
                "import net.dv8tion.jda.core.managers.impl.*;\n" +
                "import net.dv8tion.jda.core.utils.*;\n" +
                "import java.util.regex.*;\n" +
                "import java.awt.*;\n" +
                "class " + CLASS_NAME + " {\n" +
                "   public static void eval(final CommandEvent event) {\n" +
                "       " + source + "\n" +
                "   }\n" +
                "}\n";
    }

    private void evaluateDummyClassMethod(final CommandEvent event, final ClassLoader classLoader) throws Exception {
        final Class<?> dummy = classLoader.loadClass(CLASS_NAME);
        final Method eval = dummy.getDeclaredMethod("eval", CommandEvent.class);
        eval.setAccessible(true);
        eval.invoke(null, event);
    }
}
