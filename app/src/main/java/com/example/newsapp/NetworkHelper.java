package com.example.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public final class NetworkHelper {
    private static final String TAG = NetworkHelper.class.getSimpleName();

    private NetworkHelper() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        if (!TextUtils.isEmpty(stringUrl)) {
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                Log.e(TAG, "Error Creating URL: ", e);
            }
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader =
                    new BufferedReader(inputReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        if (url != null) {
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(15000 /*15 seconds*/);
                urlConnection.setReadTimeout(10000 /*10 seconds*/);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(TAG, "Error getting data from server: " + urlConnection.getResponseCode(), null);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error Making Http Request: ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return jsonResponse;
    }

    public static ArrayList<Article> extractDataFromJson(String urlString) {
        ArrayList<Article> articles = new ArrayList<>();
        String jsonResponse = "";

        if (!TextUtils.isEmpty(urlString)) {
            try {
                jsonResponse = makeHttpRequest(createUrl(urlString));
            } catch (IOException e) {
                Log.e(TAG, "Error Fetching Data From Url: ", e);
            }
        }

        if (!TextUtils.isEmpty(jsonResponse)) {
            try {
                JSONObject root = new JSONObject(jsonResponse);
                JSONObject response = root.getJSONObject("response");
                JSONArray results = response.getJSONArray("results");
                for (int i = 0; i < 10; i++) {
                    JSONObject articleObject = results.getJSONObject(i);
                    String sectionName = articleObject.getString("sectionName");
                    String publicationDate = articleObject.getString("webPublicationDate");
                    String title = articleObject.getString("webTitle");
                    String webUrl = articleObject.getString("webUrl");
                    String authorName = "";

                    JSONArray tags = articleObject.getJSONArray("tags");
                    if (!tags.isNull(0)) {
                        JSONObject tagsObject = tags.getJSONObject(0);
                        if (tagsObject != null) {
                            authorName = tagsObject.getString("webTitle");
                        }
                    }
                    articles.add(new Article(sectionName, formatDate(publicationDate), title, webUrl, authorName));
                }
            } catch (JSONException | ParseException e) {
                Log.e(TAG, "Error Extracting Data From Json String (Parsing): ", e);
            }
        } else {
            return null;
        }
        return articles;
    }

    private static String formatDate(String timeString) throws ParseException {
        SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
        Date date = inputDate.parse(timeString);
        return dateFormat.format(date);
    }
}
