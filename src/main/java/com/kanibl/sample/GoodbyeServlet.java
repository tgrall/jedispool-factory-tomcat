package com.kanibl.sample;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet(
        name="Goodbye Servlet",
        urlPatterns = "/goodbye"
)
public class GoodbyeServlet extends HttpServlet {

    private JedisPool jedisPool = null;

    @Override public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            Context initCtx =  new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            jedisPool = (JedisPool) envCtx.lookup("jedis/JedisPoolFactory");
        } catch (NamingException e) {
            e.printStackTrace();
        }


    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String fooValue = null;
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("uuid", UUID.randomUUID().toString());
            fooValue = jedis.get("uuid");
        }

        resp.getWriter().println("Value from Redis "+ fooValue);
        resp.getWriter().println("Jedis Pool Instance "+ jedisPool);

    }
}
