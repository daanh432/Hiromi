package nl.daanh.hiromi.commands.other;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromi.models.commandcontext.IBaseCommandContext;
import nl.daanh.hiromi.models.commandcontext.ICommandContext;
import nl.daanh.hiromi.models.commandcontext.ISlashCommandContext;
import nl.daanh.hiromi.models.commands.ICommand;
import nl.daanh.hiromi.models.commands.ISlashCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;

import java.lang.management.ManagementFactory;


@CommandInvoke("status")
@CommandInvoke("about")
@CommandCategory(CommandCategory.CATEGORY.OTHER)
@SelfPermission(Permission.MESSAGE_WRITE)
public class StatusCommand implements ICommand, ISlashCommand {
    private String getMessage(IBaseCommandContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        ShardManager shardManager = ctx.getJDA().getShardManager();
        int uptimeInDays = (int) ManagementFactory.getRuntimeMXBean().getUptime() / 3600000;

        stringBuilder.append("Aww! You're interested in me how sweet. Well here you go:\n");
//        stringBuilder.append("I'm currently serving ").append(shardManager.getGuildCache().size()).append(" guilds\n"); // TODO Maybe in the future privileged gateway??
        stringBuilder.append("I have been running for ").append(uptimeInDays).append(" days!\n");
        stringBuilder.append("We are on shard ").append(ctx.getJDA().getShardInfo().getShardId()).append(" together with ").append(ctx.getJDA().getGuildCache().size()).append(" others!\n");
        stringBuilder.append("\n");
        stringBuilder.append("Thanks for using our bot. Greetings Hiromi team. :heart:\n");
        stringBuilder.append("Visit us on Github: ").append(ctx.getConfiguration().getGithubLink());

        return stringBuilder.toString();
    }

    @Override
    public void handle(ICommandContext ctx) {
        ctx.reply(this.getMessage(ctx)).queue();
    }

    @Override
    public void handle(ISlashCommandContext ctx) {
        ctx.reply(this.getMessage(ctx)).setEphemeral(true).queue();
    }

    @Override
    public CommandData getCommandDefinition() {
        return new CommandData("status", "Displays information about the status of the bot and the server");
    }
}
