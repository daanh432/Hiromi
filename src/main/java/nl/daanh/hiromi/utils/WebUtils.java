package nl.daanh.hiromi.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.concurrent.FutureTask;

public class WebUtils {
    private static final OkHttpClient client = new OkHttpClient();
    private static String userAgent;
    private static String apiToken;

    private static Request.Builder apiRequest() {
        return new Request.Builder()
                .header("User-Agent", userAgent)
                .header("Authorization", apiToken);
    }

    public static FutureTask<Object> getFutureTask() {
        return new FutureTask<>(() -> {
        }, new Object());
    }

    public static void apiGetJsonFromUrl(String url, Callback callback) throws IOException {
        Request request = apiRequest()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void apiPostToUrl(String url, RequestBody body, Callback callback) throws IOException {
        Request request = apiRequest()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void setUserAgent(String userAgent) {
        WebUtils.userAgent = userAgent;
    }

    public static void setApiToken(String apiToken) {
        WebUtils.apiToken = "Bearer " + apiToken;
    }
}
