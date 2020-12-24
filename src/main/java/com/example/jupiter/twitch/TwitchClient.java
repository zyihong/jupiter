package com.example.jupiter.twitch;

import com.example.jupiter.entity.Game;
import com.example.jupiter.entity.Item;

import com.example.jupiter.entity.ItemType;
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
import java.util.*;

public class TwitchClient {
    private static final String TOKEN = "Bearer sckjbwqanj11wl5e8mef84ijbx36lc";
    private static final String CLIENT_ID = "qqs5w89ts8cvoigpm47fe8gcm9qnp4";
    private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s";
    private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s";
    private static final String STREAM_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/streams?game_id=%s&first=%s";
    private static final String VIDEO_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/videos?game_id=%s&first=%s";
    private static final String CLIP_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/clips?game_id=%s&first=%s";
    private static final String TWITCH_BASE_URL = "https://www.twitch.tv/";
    private static final int DEFAULT_SEARCH_LIMIT = 20;
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

    private String buildSearchUrl(String urlBase, String gameId, int limit) {
        try {
            gameId = URLEncoder.encode(gameId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return String.format(urlBase, gameId, limit);
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

    private List<Item> getItemList(String data) throws TwitchException {
        // Convert json string to object array.
        ObjectMapper mapper = new ObjectMapper();

        try {
            Item[] items = mapper.readValue(data, Item[].class);
            return Arrays.asList(items);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to convert JSON string to Item Object");
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

    private List<Item> searchStreams(String gameId, int limit) throws TwitchException {
        String url = buildSearchUrl(STREAM_SEARCH_URL_TEMPLATE, gameId, limit);
        List<Item> streamList = getItemList(queryTwitch(url));

        for (Item item : streamList) {
            // ItemType skipped when deserialize (Custom field).
            item.setType(ItemType.STREAM);

            // Stream type does not contains url, so we build it.
            item.setUrl(TWITCH_BASE_URL + item.getBroadcasterName());
        }

        return streamList;
    }

    private List<Item> searchVideos(String gameId, int limit) throws TwitchException {
        String url = buildSearchUrl(VIDEO_SEARCH_URL_TEMPLATE, gameId, limit);
        List<Item> videoList = getItemList(queryTwitch(url));

        for (Item item : videoList) {
            // ItemType skipped when deserialize (Custom field).
            item.setType(ItemType.VIDEO);

            // Video type does not contains game id, so we set it.
            item.setGameId(gameId);
        }

        return videoList;
    }

    private List<Item> searchClips(String gameId, int limit) throws TwitchException {
        String url = buildSearchUrl(CLIP_SEARCH_URL_TEMPLATE, gameId, limit);
        List<Item> clipList = getItemList(queryTwitch(url));

        for (Item item : clipList) {
            // ItemType skipped when deserialize (Custom field).
            item.setType(ItemType.CLIP);
        }

        return clipList;
    }

    public List<Item> searchByTypes(String gameId, ItemType type, int limit)
            throws TwitchException {
        List<Item> itemList = Collections.emptyList();

        switch (type) {
            case STREAM:
                itemList = searchStreams(gameId, limit);
                break;
            case VIDEO:
                itemList = searchVideos(gameId, limit);
                break;
            case CLIP:
                itemList = searchClips(gameId, limit);
                break;
        }

        // set game id?

        return itemList;
    }

    public Map<String, List<Item>> searchItems(String gameId) throws TwitchException {
        Map<String, List<Item>> itemMap = new HashMap<>();

        itemMap.put(ItemType.STREAM.toString(), searchStreams(gameId, DEFAULT_SEARCH_LIMIT));
        itemMap.put(ItemType.VIDEO.toString(), searchVideos(gameId, DEFAULT_SEARCH_LIMIT));
        itemMap.put(ItemType.CLIP.toString(), searchClips(gameId, DEFAULT_SEARCH_LIMIT));

        return itemMap;
    }
}
