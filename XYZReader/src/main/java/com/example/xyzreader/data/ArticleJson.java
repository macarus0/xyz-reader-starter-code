package com.example.xyzreader.data;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArticleJson {


    static public Article[] fetchArticles() {
        String recipeJsonString;
        try {
            String BAKING_URL = "https://go.udacity.com/xyz-reader-json";
            recipeJsonString = fetchArticleJson(BAKING_URL);
        } catch (IOException e) {
            Log.e("fetchArticles", "Error fetching URL "+ e.getMessage());
            return null;
        }
        Gson gson =new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Article[] recipeLists = gson.fromJson(recipeJsonString, Article[].class);
        Log.i("fetchArticles", String.format("Retrieved %s artcles", recipeLists.length));
        return recipeLists;
    }



    private static String fetchArticleJson(String urlString) throws IOException {
        String response;
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        response = streamToString(httpURLConnection.getInputStream());
        httpURLConnection.disconnect();
        return response;
    }

    private static String streamToString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder(inputStream.available());
        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line).append('\n');
        }
        return stringBuilder.toString();
    }
}
