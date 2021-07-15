package nl.daanh.hiromi.database.api;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import nl.daanh.hiromi.database.IDatabaseManager;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.HashMap;

public abstract class HiromiApiDataSource implements IDatabaseManager {
    private static final String endpoint = "https://hiromi.daanh.nl";
    private static final int EXPIRE_IN = 300;
    private static final HashMap<Long, Pair<Instant, JSONObject>> guildCache = new HashMap<>();
    private static final HashMap<Long, Pair<Instant, JSONObject>> userCache = new HashMap<>();
    private static final HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> guildMemberCache = new HashMap<>();

    protected abstract void load(String url, HashMap<Long, Pair<Instant, JSONObject>> cache, long cacheKey);

    protected abstract void load(String url, HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> cache, long cacheKey1, long cacheKey2);

    protected abstract void writeKey(String url, HashMap<Long, Pair<Instant, JSONObject>> cache, long cacheKey, String key, String value);

    protected abstract void writeKey(String url, HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> cache, long cacheKey1, long cacheKey2, String key, String value);

    private void load(Guild guild) {
        final Pair<Instant, JSONObject> cached = guildCache.get(guild.getIdLong());
        if (cached != null && cached.getLeft().isAfter(Instant.now().minusSeconds(EXPIRE_IN)))
            return;

        load(endpoint + "/api/bot/guilds/" + guild.getId(), guildCache, guild.getIdLong());
    }

    private void load(Member member) {
        final Pair<Instant, JSONObject> cached = guildMemberCache.get(Pair.of(member.getGuild().getIdLong(), member.getIdLong()));
        if (cached != null && cached.getLeft().isAfter(Instant.now().minusSeconds(EXPIRE_IN)))
            return;

        load(endpoint + "/api/bot/guilds/" + member.getGuild().getId() + "/members/" + member.getId(), guildMemberCache, member.getGuild().getIdLong(), member.getIdLong());
    }

    private void load(User user) {
        final Pair<Instant, JSONObject> cached = userCache.get(user.getIdLong());
        if (cached != null && cached.getLeft().isAfter(Instant.now().minusSeconds(EXPIRE_IN)))
            return;

        load(endpoint + "/api/bot/members/" + user.getId(), userCache, user.getIdLong());
    }

    @Nullable
    private String getKey(HashMap<Long, Pair<Instant, JSONObject>> cache, long cacheKey, String key) {
        final Pair<Instant, JSONObject> cached = cache.get(cacheKey);
        if (cached != null) {
            final JSONObject json = cached.getRight();
            if (json.getJSONObject("settings").has(key)) {
                return json.getJSONObject("settings").getString(key);
            }
        }

        return this.getDefaultSetting(key);
    }

    @Override
    @Nullable
    public String getKey(Guild guild, String key) {
        load(guild);
        return getKey(guildCache, guild.getIdLong(), key);
    }

    @Override
    @Nullable
    public String getKey(User user, String key) {
        load(user);
        return getKey(userCache, user.getIdLong(), key);
    }

    @Override
    @Nullable
    public String getKey(Member member, String key) {
        load(member);
        final Pair<Instant, JSONObject> cached = guildMemberCache.get(Pair.of(member.getGuild().getIdLong(), member.getIdLong()));
        if (cached != null) {
            final JSONObject json = cached.getRight();

            if (json.getJSONObject("settings").has(key)) {
                return json.getJSONObject("settings").getString(key);
            }
        }

        return this.getDefaultSetting(key);
    }

    @Override
    public void writeKey(Guild guild, String key, String value) {
        writeKey(endpoint + "/api/bot/guilds/" + guild.getId() + "/", guildCache, guild.getIdLong(), key, value);
    }

    @Override
    public void writeKey(Member member, String key, String value) {
        writeKey(endpoint + "/api/bot/guilds/" + member.getGuild().getId() + "/members/" + member.getId() + "/", guildMemberCache, member.getGuild().getIdLong(), member.getIdLong(), key, value);
    }

    @Override
    public void writeKey(User user, String key, String value) {
        writeKey(endpoint + "/api/bot/members/" + user.getId() + "/", userCache, user.getIdLong(), key, value);
    }
}
