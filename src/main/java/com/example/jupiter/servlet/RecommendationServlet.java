package com.example.jupiter.servlet;

import com.example.jupiter.entity.Item;
import com.example.jupiter.recommendation.ItemRecommender;
import com.example.jupiter.recommendation.RecommendationException;
import com.example.jupiter.twitch.TwitchClient;
import com.example.jupiter.twitch.TwitchException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "RecommendationServlet", urlPatterns = {"/recommendation"})
public class RecommendationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print("Invalid request");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        ItemRecommender recommender = new ItemRecommender();
        Map<String, List<Item>> itemMap;

        try {
            if (session == null) itemMap = recommender.recommendItemsByDefault();
            else {
                String userId = (String) request.getSession().getAttribute("user_id");
                itemMap = recommender.recommendItemsByUser(userId);
            }
        } catch (RecommendationException e) {
            throw new ServletException(e);
        }

        Util.writeItemMap(response, itemMap);
    }
}
