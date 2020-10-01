package nl.daanh.hiromi.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromi.commands.annotations.CommandCategory;
import nl.daanh.hiromi.commands.annotations.CommandHelp;
import nl.daanh.hiromi.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.commands.annotations.SelfPermission;
import nl.daanh.hiromi.commands.context.CommandContext;
import nl.daanh.hiromi.commands.context.CommandInterface;

@CommandInvoke("ping")
@CommandInvoke("latency")
@CommandCategory(CommandCategory.CATEGORY.OTHER)
@CommandHelp("Prints the latency of the bot connection")
@SelfPermission(Permission.MESSAGE_WRITE)
public class PingCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getJDA();
        TextChannel channel = ctx.getChannel();
        long startTime = System.currentTimeMillis();
        channel.sendMessage("Pong!").queue(message -> {
            long finishTime = System.currentTimeMillis();
            jda.getRestPing().queue((ping) -> message.editMessage("Gateway ping is " + jda.getGatewayPing() + "ms, Rest API Ping is " + ping + "ms, message took " + (finishTime - startTime) + "ms to send.").queue());
        });
    }
}
