package com.example.jupiter.servlet;

import com.example.jupiter.database.MySQLClient;
import com.example.jupiter.database.MySQLException;
import com.example.jupiter.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(request.getReader(), User.class);

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        MySQLClient client = null;
        boolean success = false;
        try {
            client = new MySQLClient();
            user.setPassword(Util.encryptPassword(user.getUsrId(), user.getPassword()));
            success = client.register(user);
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            client.close();
        }

        if (!success) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print("Invalid request");
    }
}
