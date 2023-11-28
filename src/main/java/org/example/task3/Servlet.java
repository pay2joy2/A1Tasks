package org.example.task3;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/")
public class Servlet extends HttpServlet {

    private PostingService postingService = new PostingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader bufferedReader = req.getReader();
        String body = bufferedReader.lines().collect(Collectors.joining());

        JSONObject json = new JSONObject(body);

        String period = json.getString("period");
        Boolean ActiveStatus = json.getBoolean("ActiveStatus");

        PrintWriter out = resp.getWriter();
        List<Posting> postings = null;
        try {
            postings = postingService.GetPostingsForPeriod(period,ActiveStatus);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        for(int i = 0; i < postings.size(); i++){
            out.println(postings.get(i).toString());
        }


    }
}