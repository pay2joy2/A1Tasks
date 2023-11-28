package org.example.task3;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.task3.connfactory.ConnFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Task3 {
    private static final ConnFactory cf = ConnFactory.getInstance();

    public static void main(String[] args) throws SQLException, IOException {

//        int ServerPort = 8080;
//        HttpServer server = HttpServer.create(new InetSocketAddress(ServerPort), 0);
//        server.createContext("/", (exchange -> {
//            String respText = "Hello!";
//            exchange.sendResponseHeaders(200, respText.getBytes().length);
//            OutputStream output = exchange.getResponseBody();
//            output.write(respText.getBytes());
//            output.flush();
//            exchange.close();
//        }));
//        HttpServer server = HttpServer.create(new InetSocketAddress(ServerPort), 0);
//        server.createContext("/", new MyHandler());
//        server.setExecutor(null); // creates a default executor
//        server.start();

//        LoadLogins();

//        try {
//            alterPosting();
//        } catch (CsvValidationException e) {
//            throw new RuntimeException(e);
//        }

    }

//    static class MyHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange t) throws IOException {
//            String response = "This is the response";
//            if(t.getRequestMethod().equals("GET")){
//                t.sendResponseHeaders(200, response.getBytes().length);
//                InputStream inputStream = t.getRequestBody();
//                OutputStream os = t.getResponseBody();
//                os.write(response.getBytes());
//                os.flush();
//                t.close();
//                os.close();
//            } else if(t.getRequestMethod().equals("POST")){
//                response = "THIS IS POST RESPONSE";
//                t.sendResponseHeaders(200, response.getBytes().length);
//                OutputStream os = t.getResponseBody();
//                os.write(response.getBytes());
//                os.flush();
//                t.close();
//                os.close();
//            }
//        }
//    }

    private static void LoadLogins() throws IOException, SQLException {
        Connection conn = cf.getConnection();
        String root = "'" + System.getProperty("user.dir")  + "\\logins.csv'";

        System.out.println(root);
        String sql =
                "COPY logins " +
                " FROM " + root +
                " DELIMITERS ','  "+
                " CSV HEADER; ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        conn.close();
    }
    private static void alterPosting() throws IOException, CsvValidationException {

        Map<String, Boolean> loginStatus = new HashMap<>();

        CSVReader LoginsReader = new CSVReader(new FileReader("logins.csv"));
        String [] nextLine;
        LoginsReader.readNext();
        while ((nextLine = LoginsReader.readNext()) != null) {
            loginStatus.put(nextLine[1].replaceAll("\\s+", ""), Boolean.valueOf(nextLine[2].replaceAll("\\s+", "")));
        }
        ArrayList<String[]> Matrix = new ArrayList<>();
        CSVReader PostingsReader = new CSVReader(new FileReader("postings.csv"));
        while ((nextLine = PostingsReader.readNext()) != null) {
            String[] line = Arrays.toString(nextLine).split(";");
            if(line.length > 1) {
                for(int i = 0; i < line.length; i++) {
                    line[i] = line[i].replaceAll("\t","");
                }
                line[0] = line[0].replace("[","");
                line[9] = line[9].replace("]","");
                line[7] = line[7].replaceAll(" ", "");
                Matrix.add(line);
            }
        }

        PostingsReader.close();

        for(int i = 0; i < Matrix.size(); i++) {
            String[] line = Matrix.get(i);
            if(i == 0) {
                line = addColumnToLine(line, "authorized posting");
            } else {
                if (loginStatus.containsKey(line[9]) && loginStatus.get(line[9])){
                    line = addColumnToLine(line, "true");
                } else {
                    line = addColumnToLine(line, "false");
                }
            }
            Matrix.set(i,line);
        }
//
//        for(int i = 0; i < Matrix.size(); i++) {
//            for(String s: Matrix.get(i)){
//                System.out.print(s);
//                System.out.print(" ");
//            }
//            System.out.println();
//        }

        for(int i = 1; i < Matrix.size(); i++) {
            String[] array = Matrix.get(i);
            String sqlDate2 = null;
            String sqlDate3 = null;
            try {
                sqlDate2 = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd.MM.yyyy").parse(array[2]));
                sqlDate3 = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd.MM.yyyy").parse(array[3]));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO postings VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            Connection conn = cf.getConnection();
            try {
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, array[0]);
                ps.setInt(2, Integer.parseInt(array[1]));
                ps.setDate(3, Date.valueOf(sqlDate2));
                ps.setDate(4, Date.valueOf(sqlDate3));
                ps.setString(5, array[4]);
                ps.setInt(6, Integer.parseInt(array[5]));
                ps.setString(7, array[6]);
                ps.setDouble(8,Double.parseDouble(array[7].replaceAll(",", ".")));
                ps.setString(9,array[8]);
                ps.setString(10,array[9]);
                ps.setBoolean(11, Boolean.parseBoolean(array[10]));
                ps.executeUpdate();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static String[] addColumnToLine(String[] line, String data) {
        String[] newLine = new String[line.length + 1];
        System.arraycopy(line, 0, newLine, 0, line.length);
        newLine[line.length] = data;
        return newLine;
    }
}
