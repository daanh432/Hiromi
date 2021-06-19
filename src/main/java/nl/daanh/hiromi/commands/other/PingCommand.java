package nl.daanh.hiromi.commands.other;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.daanh.hiromi.models.commandcontext.ICommandContext;
import nl.daanh.hiromi.models.commandcontext.ISlashCommandContext;
import nl.daanh.hiromi.models.commands.ICommand;
import nl.daanh.hiromi.models.commands.ISlashCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;

@CommandInvoke("ping")
@CommandCategory(CommandCategory.CATEGORY.OTHER)
@SelfPermission(Permission.MESSAGE_WRITE)
public class PingCommand implements ISlashCommand, ICommand {
    @Override
    public void handle(ISlashCommandContext ctx) {
        SlashCommandEvent event = ctx.getEvent();

        long time = System.currentTimeMillis();
        ctx.reply("Pong!").setEphemeral(true)
                .flatMap(v -> event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)
                ).queue();
    }

    @Override
    public void handle(ICommandContext ctx) {
        JDA jda = ctx.getJDA();
        long startTime = System.currentTimeMillis();
        ctx.reply("Pong!").queue(message -> {
            long finishTime = System.currentTimeMillis();
            jda.getRestPing().queue((ping) -> message.editMessage("Gateway ping is " + jda.getGatewayPing() + "ms, Rest API Ping is " + ping + "ms, message took " + (finishTime - startTime) + "ms to send.").queue());
        });
    }

    @Override
    public CommandData getCommandDefinition() {
        return new CommandData("ping", "Tells the time it took for the command to be processed round trip.");
    }
}
