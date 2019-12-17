package me.melijn.jda.commands.management;

import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import static me.melijn.jda.Melijn.PREFIX;

public class MuteCommand extends Command {

    public MuteCommand() {
        this.commandName = "mute";
        this.description = "Mutes a member on your server and sends a message with information about the mute to that member";
        this.usage = PREFIX + commandName + " <member> [reason]";
        this.extra = "The mute role should be higher then the default role and shouldn't have talking permission";
        this.aliases = new String[]{"permmute"};
        this.category = Category.MANAGEMENT;
        this.needs = new Need[]{Need.GUILD, Need.ROLE};
        this.permissions = new Permission[]{
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MANAGE_ROLES,
                Permission.MESSAGE_HISTORY
        };
        this.id = 48;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.hasPerm(event.getMember(), commandName, 1)) {

            String[] args = event.getArgs().split("\\s+");
            Guild guild = event.getGuild();
            if (args.length > 0 && !args[0].isEmpty()) {
                User target = event.getHelpers().getUserByArgsN(event, args[0]);
                if (target == null || guild.getMember(target) == null) {
                    event.reply("Unknown member");
                    return;
                }
                Role muteRole = guild.getRoleById(event.getVariables().muteRoleCache.get(guild.getIdLong()));
                if (muteRole == null) {
                    event.reply("**No mute role set!**\nCreating Role..");
                    TempMuteCommand.createMuteRole(event, guild, newMuteRole -> {
                        event.reply("Role created. You can change the settings of the role to your desires in the role managment tab.\nThis role wil be added to the muted users so it should have no talk permissions!");
                        doMute(event, newMuteRole, target, args);
                    });
                } else {
                    doMute(event, muteRole, target, args);
                }
            } else {
                event.sendUsage(this, event);
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }

    private void doMute(CommandEvent event, Role muteRole, User target, String[] args) {
        if (event.getHelpers().canNotInteract(event, muteRole)) return;
        Guild guild = muteRole.getGuild();
        guild.getController().addSingleRoleToMember(guild.getMember(target), muteRole).queue(s -> {
            String reason = event.getArgs().replaceFirst(args[0] + "\\s+|" + args[0], "");
            if (reason.length() <= 1000 & event.getMySQL().setPermMute(event.getAuthor(), target, guild, reason)) {
                event.getMessage().addReaction("\u2705").queue();
            } else {
                event.getMessage().addReaction("\u274C").queue();
            }
        });
    }
}
