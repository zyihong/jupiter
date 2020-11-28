package com.example.jupiter.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import com.example.jupiter.entity.Game;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet(name = "GameServlet", urlPatterns = {"/game"})
public class GameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        JSONObject jsonRequest = new JSONObject(IOUtils.toString(request.getReader()));
        String name = jsonRequest.getString("name");
        String developer = jsonRequest.getString("developer");
        String releaseTime = jsonRequest.getString("release_time");
        String website = jsonRequest.getString("website");
        float price = jsonRequest.getFloat("price");

        System.out.println("Name is: " + name);
        System.out.println("Developer is: " + developer);
        System.out.println("Release time is: " + releaseTime);
        System.out.println("Website is: " + website);
        System.out.println("Price is: " + price);

        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "ok");
        response.getWriter().print(jsonResponse);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
//        String gameName = request.getParameter("gamename");
        ObjectMapper mapper = new ObjectMapper();
        Game object = new Game("World of Warcraft", "Blizzard Entertainment",
                        "Feb 11, 2005", "https://www.worldofwarcraft.com", 49.99);

        response.setContentType("application/json");
        response.getWriter().print(mapper.writeValueAsString(object));
    }
}
