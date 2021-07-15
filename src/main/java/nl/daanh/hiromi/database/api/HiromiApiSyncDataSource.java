package nl.daanh.hiromi.database.api;

import net.dv8tion.jda.internal.utils.tuple.Pair;
import nl.daanh.hiromi.helpers.WebUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class HiromiApiSyncDataSource extends HiromiApiDataSource {
    @Override
    protected void load(String url, HashMap<Long, Pair<Instant, JSONObject>> cache, long cacheKey) {
        FutureTask<Object> futureTask = WebUtils.getFutureTask();

        try {
            WebUtils.apiGetJsonFromUrl(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    futureTask.run();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            cache.put(cacheKey, Pair.of(Instant.now(), json.getJSONObject("data")));
                            futureTask.run();
                        } catch (JSONException exception) {
                            throw new IOException(exception.getMessage(), exception);
                        }
                    } else {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });

            futureTask.get();
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new HiromiApiException(e.getMessage(), e);
        }
    }

    @Override
    protected void load(String url, HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> cache, long cacheKey1, long cacheKey2) {
        FutureTask<Object> futureTask = WebUtils.getFutureTask();

        try {
            WebUtils.apiGetJsonFromUrl(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    futureTask.run();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            cache.put(Pair.of(cacheKey1, cacheKey2), Pair.of(Instant.now(), json.getJSONObject("data")));
                            futureTask.run();
                        } catch (JSONException exception) {
                            throw new IOException(exception.getMessage(), exception);
                        }
                    } else {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });

            futureTask.get();
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new HiromiApiException(e.getMessage(), e);
        }
    }

    @Override
    protected void writeKey(String url, HashMap<Long, Pair<Instant, JSONObject>> cache, long cacheKey, String key, String value) {
        FutureTask<Object> futureTask = WebUtils.getFutureTask();

        try {
            RequestBody body = new FormBody.Builder()
                    .add("value", value)
                    .build();

            WebUtils.apiPostToUrl(url + key, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    futureTask.run();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        final Pair<Instant, JSONObject> cached = cache.get(cacheKey);
                        if (cached != null)
                            cached.getRight().getJSONObject("settings").put(key, value);
                        futureTask.run();
                    } else {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });

            futureTask.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new HiromiApiException(e.getMessage(), e);
        }
    }

    @Override
    protected void writeKey(String url, HashMap<Pair<Long, Long>, Pair<Instant, JSONObject>> cache, long cacheKey1, long cacheKey2, String key, String value) {
        FutureTask<Object> futureTask = WebUtils.getFutureTask();

        try {
            RequestBody body = new FormBody.Builder()
                    .add("value", value)
                    .build();

            WebUtils.apiPostToUrl(url + key, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    futureTask.run();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        final Pair<Instant, JSONObject> cached = cache.get(Pair.of(cacheKey1, cacheKey2));
                        cached.getRight().getJSONObject("settings").put(key, value);
                        futureTask.run();
                    } else {
                        throw new IOException("Response did not contain valid data");
                    }
                }
            });
            futureTask.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new HiromiApiException(e);
        }
    }
}
