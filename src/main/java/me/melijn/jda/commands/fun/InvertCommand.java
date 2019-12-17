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

public class InvertCommand extends Command {

    public InvertCommand() {
        this.commandName = "invert";
        this.usage = PREFIX + commandName + " [image]";
        this.description = "Inverts an image";
        this.aliases = new String[]{"negative"};
        this.category = Category.FUN;
        this.id = 4;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            ImageUtils imageUtils = new ImageUtils();
            BufferedImage img = imageUtils.getBufferedImage(event);
            if (img == null) return;
            /* >> Right shift in bits
                    p >> n     n is hier het aantal bits dat je de p verschuift naar rechts
                    & 0xff;    door & neem je alleen de laatste bits gespecifieerd door de hex erachter, in dit geval 0xff of 8 bits;
                    source: https://android.jlelse.eu/java-when-to-use-n-8-0xff-and-when-to-use-byte-n-8-2efd82ae7dd7
                    8bits = max 255
                     */
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int pixel = img.getRGB(x, y);

                    int a = (pixel >> 24) & 0xff;
                    int r = (pixel >> 16) & 0xff;
                    int g = (pixel >> 8) & 0xff;
                    int b = pixel & 0xff;

                    r = 255 - r;
                    g = 255 - g;
                    b = 255 - b;
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
