package nl.daanh.hiromi;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import nl.daanh.hiromi.listeners.GuildMessageListener;
import nl.daanh.hiromi.listeners.MessageReactionListener;
import nl.daanh.hiromi.listeners.MusicPauseListener;

import javax.security.auth.login.LoginException;

public class Main {
    private static final Dotenv dotenv = Dotenv.load();

    public static void main(String[] args) throws LoginException {

        DefaultShardManagerBuilder jdaBuilder = DefaultShardManagerBuilder.createDefault(dotenv.get("DISCORD_TOKEN", null));
        configureMemoryUsage(jdaBuilder);

        jdaBuilder.addEventListeners(new GuildMessageListener());
        jdaBuilder.addEventListeners(new MusicPauseListener());
        jdaBuilder.addEventListeners(new MessageReactionListener());

        jdaBuilder.build();
    }

    public static void configureMemoryUsage(DefaultShardManagerBuilder builder) {
        // Disable cache for member activities (streaming/games/spotify)
        builder.disableCache(CacheFlag.ACTIVITY);

        // Only cache members who are either in a voice channel or owner of the guild
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE);

        // Disable member chunking on startup
        builder.setChunkingFilter(ChunkingFilter.NONE);

        // Disable presence updates and typing events
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS);

        // Consider guilds with more than 10 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled.
        builder.setLargeThreshold(10);
    }
}
