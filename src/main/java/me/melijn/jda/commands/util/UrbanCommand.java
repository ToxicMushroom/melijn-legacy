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
import me.melijn.jda.blub.Need;
import me.melijn.jda.utils.Embedder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONObject;

import static me.melijn.jda.Melijn.PREFIX;

public class UrbanCommand extends Command {

    public UrbanCommand() {
        this.commandName = "urban";
        this.usage = PREFIX + commandName + " <word>";
        this.description = "Searches a word on urbandictionary.com";
        this.aliases = new String[]{"dictionary", "meaning"};
        this.category = Category.UTILS;
        this.permissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.needs = new Need[]{Need.NSFW};
        this.id = 2;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            String[] args = event.getArgs().split("\\s+");
            if (event.getArgs().isEmpty()) {
                event.sendUsage(this, event);
                return;
            }
            String result = event.getWebUtils().run("https://api.urbandictionary.com/v0/define?term=" + event.getArgs());
            if (result != null) {
                JSONObject jresult = new JSONObject(result);
                if (jresult.getJSONArray("list").toList().size() > 0) {
                    JSONObject firstMeaning = jresult.getJSONArray("list").getJSONObject(0);
                    String definition = removeBrackets(firstMeaning.getString("definition"));
                    String example = removeBrackets(firstMeaning.getString("example"));

                    event.reply(new Embedder(event.getVariables(), event.getGuild())
                            .setTitle(firstMeaning.getString("word"))
                            .setDescription("**Meaning**\n " + definition.substring(0, definition.length() > 1000 ? 1000 : definition.length()) + "\n\n**Example**\n " + example.substring(0, example.length() > 1000 ? 1000 : example.length()))
                            .setFooter(event.getHelpers().getFooterStamp(), null)
                            .build());
                } else {
                    event.reply("Word not found, check for spelling mistakes");
                }
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }

    private String removeBrackets(String input) {
        return input.replaceAll("\\[", "").replaceAll("]", "");
    }
}
