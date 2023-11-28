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

    private final PostingService postingService = new PostingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader bufferedReader = req.getReader();
        String body = bufferedReader.lines().collect(Collectors.joining());

        JSONObject json = new JSONObject(body);

        String period = json.getString("period");
        Boolean ActiveStatus = null;
        if(json.has("ActiveStatus")){
            ActiveStatus = json.getBoolean("ActiveStatus");
        }

        PrintWriter out = resp.getWriter();
        List<Posting> postings = null;
        try {
            postings = postingService.GetPostingsForPeriod(period,ActiveStatus);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (Posting posting : postings) {
            out.println(posting.toString());
        }
    }
}
