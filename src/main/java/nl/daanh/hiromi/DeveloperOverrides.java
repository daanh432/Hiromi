package nl.daanh.hiromi;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class DeveloperOverrides {
    public static void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        final IHiromiConfig config = Hiromi.getConfig();
        final IDatabaseManager databaseManager = config.getDatabaseManager();
        if (event.getAuthor().getIdLong() != config.getOwner()) return;
        final String prefix = config.getGlobalPrefix();
        final Member member = event.getMember();

        final String[] splitMessage = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
        final List<String> args = Arrays.asList(splitMessage).subList(1, splitMessage.length);

        switch (splitMessage[0].toLowerCase()) {
            case "shutdown":
                if (args.size() > 0) {
                    int shardId = Integer.parseInt(args.get(0));
                    if (shardId < 0 || shardId >= config.getTotalShards()) {
                        event.getChannel().sendMessage(String.format("The shard %s is out of the range of %s total shards.", shardId, config.getTotalShards())).queue();
                        return;
                    }

                    event.getChannel().sendMessage(String.format("Shutting down %s.", shardId)).queue();
                    Hiromi.getEventManager().restart(shardId);
                } else {
                    event.getChannel().sendMessage("Shutting down.").queue();
                    Hiromi.getEventManager().shutdown();
                }
                break;
            case "givemoney":
                event.getChannel().sendMessage("Giving user money").queue();
                databaseManager.setCashAmount(member, databaseManager.getCashAmount(member) + 500);
                break;
        }
    }
}
