package com.example.textmessage;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChatGPTAPI {
    private static final String CHAT_GPT_BASE_URL = "https://api.openai.com/v1/chat/completions";

    private static final String API_KEY = BuildConfig.OPENAI_CHAT_GPT_API_KEY;

    private AsyncHttpClient client;

    public ChatGPTAPI() {
        client = new AsyncHttpClient();
    }

    public void getGPT35Response(String query, AsyncHttpClient.HTTPJSONResponse response) {
        HashMap<String, String> headers = new HashMap<>();

        headers.put("Authorization", String.format("Bearer %s", API_KEY));
        headers.put("Content-Type", "application/json");

        JSONObject body = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject primaryMessage = new JSONObject();

        try {
            primaryMessage.put("role", "user");
            primaryMessage.put("content", query);

            messages.put(primaryMessage);

            body.put("model", "gpt-3.5-turbo");
            body.put("messages", messages);
        } catch (JSONException e) {
            Log.d("GPT35 Error", "Failed to Construct Body");
        }

        client.post(CHAT_GPT_BASE_URL, headers, body, response);
    }
}
