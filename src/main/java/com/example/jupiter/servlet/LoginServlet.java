package com.example.jupiter.servlet;

import com.example.jupiter.database.MySQLClient;
import com.example.jupiter.database.MySQLException;
import com.example.jupiter.entity.LoginRequestBody;
import com.example.jupiter.entity.LoginResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        LoginRequestBody body = mapper.readValue(request.getReader(), LoginRequestBody.class);

        if (body == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String usrName = null;
        MySQLClient client = null;
        try {
            client = new MySQLClient();
            String usrId = body.getUsrId();
//            System.out.println("Login: usr_id = " + usrId);
//            System.out.println("Login: password = " + body.getPassword());
            String password = Util.encryptPassword(usrId, body.getPassword());
            usrName = client.verifyUser(usrId, password);
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            client.close();
        }

        if (usrName != null) {
            // Login success.
            HttpSession session = request.getSession();
            session.setAttribute("user_id", body.getUsrId());
            session.setAttribute("user_name", usrName);
            session.setMaxInactiveInterval(600);

            LoginResponseBody loginResponseBody = new LoginResponseBody(body.getUsrId(), usrName);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(new ObjectMapper().writeValueAsString(loginResponseBody));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print("Invalid request");
    }
}
