package nl.daanh.hiromi.database.api;

import net.dv8tion.jda.internal.utils.tuple.Pair;
import nl.daanh.hiromi.database.disk.HiromiDiskIOException;
import nl.daanh.hiromi.helpers.WebUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

public class HiromiApiAsyncDataSource extends HiromiApiDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(HiromiApiAsyncDataSource.class);

    @Override
    protected void load(String url, HashMap<Long, Pair<Instant, JSONObject>> cache, long cacheKey) {
        try {
            WebUtils.apiGetJsonFromUrl(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LOGGER.error(e.getMessage());
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
                    } else if (response.code() != 404) {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });
        } catch (IOException exception) {
            throw new HiromiApiException(exception.getMessage(), exception);
        }
    }

    @Override
    protected void load(String url, HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> cache, long cacheKey1, long cacheKey2) {
        try {
            WebUtils.apiGetJsonFromUrl(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LOGGER.error(e.getMessage());
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
                    } else if (response.code() != 404) {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });
        } catch (IOException exception) {
            throw new HiromiApiException(exception.getMessage(), exception);
        }
    }

    @Override
    protected void writeKey(String url, HashMap<Long, Pair<Instant, JSONObject>> cache, long cacheKey, String key, String value) {
        try {
            RequestBody body = new FormBody.Builder()
                    .add("value", value)
                    .build();

            WebUtils.apiPostToUrl(url + key, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LOGGER.error(e.getMessage());
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
            throw new HiromiApiException(e.getMessage(), e);
        }
    }

    @Override
    protected void writeKey(String url, HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> cache, long cacheKey1, long cacheKey2, String key, String value) {
        try {
            RequestBody body = new FormBody.Builder()
                    .add("value", value)
                    .build();

            WebUtils.apiPostToUrl(url + key, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LOGGER.error(e.getMessage());
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
}
