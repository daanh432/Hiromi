package nl.daanh.hiromi;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import nl.daanh.hiromi.models.configuration.BaseHiromiConfig;
import nl.daanh.hiromi.models.configuration.HiromiConfigDotEnv;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import nl.daanh.hiromi.utils.EmbedUtils;
import nl.daanh.hiromi.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.TimeZone;

public class Hiromi {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hiromi.class);
    private static Hiromi instance;
    private final ShardManager shardManager;
    private final EventManager eventManager;

    public Hiromi() throws LoginException {
        IHiromiConfig config = new HiromiConfigDotEnv();
        this.configureDefaults(config);
        this.eventManager = new EventManager(config);

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(
//                        GatewayIntent.GUILD_INVITES,
//                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_EMOJIS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGES
                )
                .setToken(config.getToken())
                .setShardsTotal(config.getTotalShards())
                .setActivityProvider((shardId) -> Activity.listening(
                        "/help | Shard " + (shardId + 1)
                ))
                .setBulkDeleteSplittingEnabled(false)
                .setEventManagerProvider((id) -> this.eventManager)
                .setMemberCachePolicy(MemberCachePolicy.DEFAULT)
                .setChunkingFilter(ChunkingFilter.NONE)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
                .setGatewayEncoding(GatewayEncoding.ETF);

        shardManager = builder.build();
    }

    private void configureDefaults(IHiromiConfig config) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        EmbedUtils.setEmbedBuilder(() -> new EmbedBuilder()
                .setColor(config.getEmbedColor())
                .setFooter("Hiromi Bot", null)
                .setTimestamp(Instant.now()));

        WebUtils.setApiToken(config.getApiToken());
        String implementationVersion = this.getClass().getPackage().getImplementationVersion();
        WebUtils.setUserAgent(String.format("Hiromi/%s", implementationVersion != null ? implementationVersion : "DEVELOPMENT"));
    }

    public static void main(String[] args) {
        try {
            instance = new Hiromi();
        } catch (LoginException loginException) {
            LOGGER.error("Login failed", loginException);
            System.exit(1);
        } catch (Exception exception) {
            LOGGER.error("Uncaught exception.", exception);
        }
    }

    public static Hiromi getInstance() {
        return instance;
    }

    public static ShardManager getShardManager() {
        return getInstance().shardManager;
    }

    public static EventManager getEventManager() {
        return getInstance().eventManager;
    }

    public static IHiromiConfig getConfig() {
        return BaseHiromiConfig.getInstance();
    }
}
