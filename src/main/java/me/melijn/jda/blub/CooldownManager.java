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

import me.melijn.jda.db.Variables;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {

    private final Variables variables;
    private final Map<Long, Map<Long, Map<Integer, Long>>> cooldowns = new HashMap<>();// Guild -> User -> command -> time used

    public CooldownManager(Variables variables) {
        this.variables = variables;
    }

    private void checkOldEntries() {
        long currentTime = System.currentTimeMillis();
        new HashMap<>(cooldowns).forEach((guildId, users) -> //Loop safely through the map so I can modify it
                new HashMap<>(users).forEach((userId, commands) ->
                        new HashMap<>(commands).forEach((commandId, time) -> {
            if (time > currentTime) return; //if the a command his delay isn't finished yet return; (return goes to next iteration)
            Map<Integer, Long> freshCommands = cooldowns.get(guildId).get(userId); //Create object to easily edit active command delays for a user
            freshCommands.remove(commandId); //Remove the command
            if (freshCommands.isEmpty()) { //If the user has no active command delays anymore then remove it from the map to save space
                Map<Long, Map<Integer, Long>> freshUsers = cooldowns.get(guildId);
                freshUsers.remove(userId);
                if (freshUsers.isEmpty()) { //If none of the users in the guild have active command delays then remove the guild
                    cooldowns.remove(guildId);
                } else { //Else update the users
                    cooldowns.put(guildId, freshUsers);
                }
            } else { //Else update the user's command delays
                Map<Long, Map<Integer, Long>> freshUsers = cooldowns.get(guildId);
                freshUsers.put(userId, freshCommands);
                cooldowns.put(guildId, freshUsers);
            }
        })));
    }

    public void updateCooldown(long guildId, long userId, int commandId) {
        checkOldEntries();
        if (!variables.cooldowns.get(guildId).containsKey(commandId)) return;
        Map<Long, Map<Integer, Long>> users = cooldowns.containsKey(guildId) ? cooldowns.get(guildId) : new HashMap<>();
        if (!users.containsKey(userId)) users.put(userId, new HashMap<>());
        Map<Integer, Long> commands = users.get(userId);
        commands.put(commandId, System.currentTimeMillis() + variables.cooldowns.get(guildId).get(commandId));
        users.put(userId, commands);
        cooldowns.put(guildId, users);
    }

    public boolean isActive(long guildId, long userId, int commandId) {
        long currentTime = System.currentTimeMillis();
        return (cooldowns.containsKey(guildId) &&
                cooldowns.get(guildId).containsKey(userId) &&
                cooldowns.get(guildId).get(userId).containsKey(commandId) &&
                cooldowns.get(guildId).get(userId).get(commandId) > currentTime);
    }

    public long getTimeLeft(long guildId, long userId, int commandId) {
        return (cooldowns.containsKey(guildId) &&
                cooldowns.get(guildId).containsKey(userId) &&
                cooldowns.get(guildId).get(userId).containsKey(commandId)) ?
                cooldowns.get(guildId).get(userId).get(commandId) - System.currentTimeMillis() :
                0;
    }
}
