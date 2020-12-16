package com.example.jupiter.servlet;

import com.example.jupiter.entity.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class Util {
    public static void writeItemMap(HttpServletResponse response,
                                    Map<String, List<Item>> itemMap) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(new ObjectMapper().writeValueAsString(itemMap));
    }
}
