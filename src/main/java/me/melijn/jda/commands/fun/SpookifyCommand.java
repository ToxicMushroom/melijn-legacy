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

import java.awt.image.BufferedImage;

import static me.melijn.jda.Melijn.PREFIX;

public class SpookifyCommand extends Command {

    private ImageUtils imageUtils = new ImageUtils();

    public SpookifyCommand() {
        this.commandName = "spookify";
        this.description = "Spookifies an image";
        this.category = Category.FUN;
        this.usage = PREFIX + commandName + " [image] [brightness threshold 1-254 (default 128)]";
        this.id = 97;
    }
    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            BufferedImage img = imageUtils.getBufferedImage(event);
            String[] args = event.getArgs().split("\\s+");
            if (img == null) return;
            int threshold = 128;
            if (args[args.length - 1].matches("[0-9]{1,3}") && Integer.parseInt(args[args.length - 1]) < 255)
                threshold = Integer.parseInt(args[args.length - 1]);
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int pixel = img.getRGB(x, y);

                    int a = (pixel >> 24) & 0xff;
                    int r = (pixel >> 16) & 0xff;
                    int g = (pixel >> 8) & 0xff;
                    int b = pixel & 0xff;

                    int[] newcolor = imageUtils.getSpookyForPixel(r, g, b, threshold);
                    r = newcolor[0];
                    g = newcolor[1];
                    b = newcolor[2];
                    pixel = (a << 24) | (r << 16) | (g << 8) | b;

                    img.setRGB(x, y, pixel);
                }
            }
            event.reply(img);
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
