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

package me.melijn.jda.commands.fun;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.utils.ImageUtils;
import net.dv8tion.jda.core.Permission;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static me.melijn.jda.Melijn.PREFIX;

public class SayCommand extends Command {

    public SayCommand() {
        this.commandName = "say";
        this.description = "Makes the bot say stuff";
        this.usage = PREFIX + commandName + " [%fontSize%, default is 60] <text>";
        this.aliases = new String[]{"zeg"};
        this.permissions = new Permission[]{Permission.MESSAGE_ATTACH_FILES};
        this.category = Category.FUN;
        this.id = 29;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            if (event.getArgs().isEmpty()) {
                event.sendUsage(this, event);
                return;
            }
            BufferedImage image;
            try {
                String resourcename = event.getExecutor().equalsIgnoreCase("zeg") ? "melijn_zegt.jpg" : "melijn_says.jpg";
                image = ImageIO.read(new File(resourcename));
                String[] args = event.getArgs().split("\\s+");
                Graphics graphics = image.getGraphics();
                Font font = graphics.getFont().deriveFont(60f);
                String text = event.getArgs();

                if (args[0].matches("%\\d+(?:.\\d+)?%")) {
                    try {
                        float fontsize = Float.parseFloat(args[0].substring(1, args[0].length() - 1));
                        font = graphics.getFont().deriveFont(fontsize);
                    } catch (NumberFormatException ignored) {
                        event.reply("The number inside the %% signs must be under 20 figures");
                    }
                    text = text.replaceFirst(args[0] + "\\s+?", "");
                }


                graphics.setFont(font);
                graphics.setColor(Color.DARK_GRAY);

                int startx = 1133;
                int endx = 1800;
                int starty = 82;
                int endy = 1000;

                ImageUtils imageUtils = new ImageUtils();
                image = imageUtils.putText(image, text, startx, endx, starty, endy, graphics);

                event.reply(image);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
