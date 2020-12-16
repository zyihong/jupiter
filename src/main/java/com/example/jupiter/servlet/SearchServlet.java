package com.example.jupiter.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.example.jupiter.twitch.TwitchClient;
import com.example.jupiter.twitch.TwitchException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print("Invalid request");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String gameId = request.getParameter("game_id");

        if (gameId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        TwitchClient client = new TwitchClient();

        try {
            Util.writeItemMap(response, client.searchItems(gameId));
        } catch (TwitchException e) {
            throw new ServletException(e);
        }
    }
}
