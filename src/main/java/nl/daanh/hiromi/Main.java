package nl.daanh.hiromi;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import nl.daanh.hiromi.listeners.ButtonClickListener;
import nl.daanh.hiromi.listeners.GuildMessageListener;
import nl.daanh.hiromi.listeners.SlashCommandListener;
import nl.daanh.hiromi.models.configuration.DotEnvConfiguration;
import nl.daanh.hiromi.models.configuration.IConfiguration;

import javax.security.auth.login.LoginException;

public class Main {
    private static final IConfiguration configuration = new DotEnvConfiguration();

    public static void main(String[] args) throws LoginException {
        CommandManager commandManager = new CommandManager(configuration);

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(configuration.getDiscordToken());

        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Disable compression (not recommended)
        builder.setCompression(Compression.ZLIB);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.listening(configuration.getStatusText()));

        // Register event listeners
        builder.addEventListeners(new GuildMessageListener(configuration, commandManager));
        builder.addEventListeners(new SlashCommandListener(commandManager));
        builder.addEventListeners(new ButtonClickListener(commandManager));

        configureMemoryUsage(builder);
        builder.build();
    }

    private static void configureMemoryUsage(DefaultShardManagerBuilder builder) {
        // Disable cache for member activities (streaming/games/spotify)
        builder.disableCache(CacheFlag.ACTIVITY);

        // Only cache members who are either in a voice channel or owner of the guild
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER));

        // Disable member chunking on startup
        builder.setChunkingFilter(ChunkingFilter.NONE);

        // Disable presence updates and typing events
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES);

        // Consider guilds with more than 50 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled.
        builder.setLargeThreshold(50);
    }
}
