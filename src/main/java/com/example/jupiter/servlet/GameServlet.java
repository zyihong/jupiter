package com.example.jupiter.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.jupiter.twitch.TwitchClient;
import com.example.jupiter.twitch.TwitchException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import com.example.jupiter.entity.Game;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet(name = "GameServlet", urlPatterns = {"/game"})
public class GameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        response.getWriter().print("Invalid request");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        String gameName = request.getParameter("game_name");
        TwitchClient client = new TwitchClient();

        response.setContentType("application/json;charset=UTF-8");

        try {
            ObjectMapper mapper = new ObjectMapper();

            if (gameName != null) {
                // Search for target game.
                String result = mapper.writeValueAsString(client.searchGame(gameName));
                response.getWriter().print(result);
            }
            else {
                // Search for top games.
                String result = mapper.writeValueAsString(client.getTopGames(0));
                response.getWriter().print(result);
            }
        } catch (TwitchException e) {
            throw new ServletException(e);
        }
    }
}
