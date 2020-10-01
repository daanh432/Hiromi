package nl.daanh.hiromi.commands.music;

import net.dv8tion.jda.api.Permission;
import nl.daanh.hiromi.commands.annotations.CommandCategory;
import nl.daanh.hiromi.commands.annotations.CommandHelp;
import nl.daanh.hiromi.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.commands.annotations.SelfPermission;
import nl.daanh.hiromi.commands.context.CommandContext;
import nl.daanh.hiromi.commands.context.CommandInterface;

@CommandInvoke("queue")
@CommandInvoke("queued")
@CommandHelp("Shows you a list of the currently queued songs.")
@CommandCategory(CommandCategory.CATEGORY.MUSIC)
@SelfPermission(Permission.MESSAGE_WRITE)
@SelfPermission(Permission.MESSAGE_ADD_REACTION)
@SelfPermission(Permission.MESSAGE_MANAGE)
@SelfPermission(Permission.VOICE_CONNECT)
@SelfPermission(Permission.VOICE_SPEAK)
public class QueueCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        // TODO Implement queue command
    }
}
