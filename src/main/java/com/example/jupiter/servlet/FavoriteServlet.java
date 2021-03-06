package com.example.jupiter.servlet;

import com.example.jupiter.database.MySQLClient;
import com.example.jupiter.database.MySQLException;
import com.example.jupiter.entity.Favorite;
import com.example.jupiter.entity.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "FavoriteServlet", urlPatterns = {"/favorite"})
public class FavoriteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        Favorite body = Util.readRequestBody(Favorite.class, request);
        if (body == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        MySQLClient client = null;
        try {
            client = new MySQLClient();
            client.addFavorite(userId, body.getFavoriteItem());
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String userId = (String) session.getAttribute("user_id");
        Map<String, List<Item>> itemMap;
        MySQLClient client = null;
        try {
            client = new MySQLClient();
            itemMap = client.getFavoriteItems(userId);
            Util.writeItemMap(response, itemMap);
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        Favorite body = Util.readRequestBody(Favorite.class, request);
        if (body == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        MySQLClient client = null;
        try {
            client = new MySQLClient();
            client.removeFavorite(userId, body.getFavoriteItem().getId());
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
