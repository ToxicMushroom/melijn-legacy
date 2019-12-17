package me.melijn.jda.commands.fun;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import static me.melijn.jda.Melijn.PREFIX;

public class SlapCommand extends Command {



    public SlapCommand() {
        this.commandName = "slap";
        this.description = "Shows a person being slapped [anime]";
        this.usage = PREFIX + commandName + " [user | role]";
        this.category = Category.FUN;
        this.id = 40;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || event.hasPerm(event.getMember(), commandName, 0)) {
            String[] args = event.getArgs().split("\\s+");
            if (args.length == 0 || args[0].isEmpty()) {
                event.getWebUtils().getImage("slap",
                        image -> event.getMessageHelper().sendFunText("**" + event.getAuthor().getName() + "** wants to slap someone", image.getUrl(), event)
                );
            } else if (args.length == 1) {
                User target = event.getHelpers().getUserByArgsN(event, args[0]);
                Role role = event.getHelpers().getRoleByArgs(event, args[0]);
                if (target == null && role == null) {
                    event.reply("Unknown user or role");
                } else if (target != null) {
                    event.getWebUtils().getImage("slap",
                            image -> event.getMessageHelper().sendFunText("**" + event.getAuthor().getName() + "** slapped **" + target.getName() + "**", image.getUrl(), event)
                    );
                } else {
                    event.getWebUtils().getImage("slap",
                            image -> event.getMessageHelper().sendFunText("**" + event.getAuthor().getName() + "** slapped **" + role.getAsMention() + "**", image.getUrl(), event)
                    );
                }
            } else {
                event.sendUsage(this, event);
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}