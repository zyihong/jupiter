package com.example.jupiter.twitch;

import com.example.jupiter.entity.Game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Arrays;

public class TwitchClient {
    private static final String TOKEN = "Bearer sckjbwqanj11wl5e8mef84ijbx36lc";
    private static final String CLIENT_ID = "qqs5w89ts8cvoigpm47fe8gcm9qnp4";
    private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s";
    private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s";
    private static final int DEFAULT_GAME_LIMIT = 20;

    private String buildUrl(String urlBase, String gameName, int limit) {
        if (gameName.equals("")) return String.format(urlBase, limit);

        try {
            gameName = URLEncoder.encode(gameName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return String.format(urlBase, gameName);
    }

    private String queryTwitch(String url) throws TwitchException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Create a custom response handler
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                if (entity == null)
                    throw new TwitchException("Failed to get data from Twitch");

                JSONObject obj = new JSONObject(EntityUtils.toString(entity));
                return obj.getJSONArray("data").toString();
            } else {
                throw new TwitchException("Failed to request Twitch: " + status);
            }
        };

        // send request
        try {
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", TOKEN);
            request.setHeader("Client-Id", CLIENT_ID);

            return httpclient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to request Twitch");
        } finally {
            try{
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Game> getGameList(String data) throws TwitchException {
        // convert json string to object array
        ObjectMapper mapper = new ObjectMapper();

        try {
            Game[] games = mapper.readValue(data, Game[].class);
            return Arrays.asList(games);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to convert JSON string to Game Object");
        }
    }

    public List<Game> getTopGames(int limit) throws TwitchException {
        if (limit <= 0) limit = DEFAULT_GAME_LIMIT;

        String url = buildUrl(TOP_GAME_URL, "", limit);
        return getGameList(queryTwitch(url));
    }

    public Game searchGame(String gameName) throws TwitchException {
        String url = buildUrl(GAME_SEARCH_URL_TEMPLATE, gameName, 0);
        List<Game> gameList = getGameList(queryTwitch(url));

        if (gameList.size() == 0) return null;

        return gameList.get(0);
    }
}
