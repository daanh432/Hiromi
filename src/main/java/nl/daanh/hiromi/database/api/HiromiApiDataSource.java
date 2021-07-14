package nl.daanh.hiromi.database.api;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.database.disk.HiromiDiskIOException;
import nl.daanh.hiromi.helpers.WebUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

public class HiromiApiDataSource implements IDatabaseManager {
    private static final String endpoint = "https://hiromi.daanh.nl";
    private static final int EXPIRE_IN = 30;
    private static final HashMap<Long, Pair<Instant, JSONObject>> guildCache = new HashMap<>();
    private static final HashMap<Long, Pair<Instant, JSONObject>> userCache = new HashMap<>();
    private static final HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> guildMemberCache = new HashMap<>();

    private void load(String url, HashMap<Long, Pair<Instant, JSONObject>> cache, long cacheKey) {
        try {
            WebUtils.apiGetJsonFromUrl(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // ignore
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            cache.put(cacheKey, Pair.of(Instant.now(), json.getJSONObject("data")));
                        } catch (JSONException exception) {
                            throw new IOException(exception.getMessage(), exception);
                        }
                    } else {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });
        } catch (IOException exception) {
            throw new HiromiApiIOException(exception.getMessage(), exception);
        }
    }

    private void load(String url, HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> cache, long cacheKey1, long cacheKey2) {
        try {
            WebUtils.apiGetJsonFromUrl(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // ignore
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            cache.put(Pair.of(cacheKey1, cacheKey2), Pair.of(Instant.now(), json.getJSONObject("data")));
                        } catch (JSONException exception) {
                            throw new IOException(exception.getMessage(), exception);
                        }
                    } else {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });
        } catch (IOException exception) {
            throw new HiromiApiIOException(exception.getMessage(), exception);
        }
    }

    private void writeKey(String url, HashMap<Long, Pair<Instant, JSONObject>> cache, long cacheKey, String key, String value) {
        try {
            RequestBody body = new FormBody.Builder()
                    .add("value", value)
                    .build();

            WebUtils.apiPostToUrl(url + key, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // ignore
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        final Pair<Instant, JSONObject> cached = cache.get(cacheKey);
                        if (cached != null)
                            cached.getRight().getJSONObject("settings").put(key, value);
                    } else {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });
        } catch (IOException e) {
            throw new HiromiDiskIOException(e);
        }
    }

    private void writeKey(String url, HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> cache, long cacheKey1, long cacheKey2, String key, String value) {
        try {
            RequestBody body = new FormBody.Builder()
                    .add("value", value)
                    .build();

            WebUtils.apiPostToUrl(url + key, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // ignore
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        final Pair<Instant, JSONObject> cached = cache.get(Pair.of(cacheKey1, cacheKey2));
                        cached.getRight().getJSONObject("settings").put(key, value);
                    } else {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });
        } catch (IOException e) {
            throw new HiromiDiskIOException(e);
        }
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

    @Override
    @Nullable
    public String getKey(Guild guild, String key) {
        load(guild);
        return getKey(guildCache, guild.getIdLong(), key);
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
    @Nullable
    public String getKey(User user, String key) {
        load(user);
        return getKey(userCache, user.getIdLong(), key);
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
