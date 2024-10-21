package model.exchange;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet("/ExchangeRates")
public class AllExchangeRates extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        JSONArray jsonArray = new JSONArray();

        PrintWriter out = resp.getWriter();

        try {

            Class.forName("org.sqlite.JDBC");

            String url = "jdbc:sqlite:C:\\Users\\podvo\\sqlite\\ExchangeCurrencies.db";
            Connection con = DriverManager.getConnection(url);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT rate FROM ExchangeRates");


            while (rs.next()) {
                double rate = rs.getDouble("rate");
                jsonArray.put(rate);
            }
            out.println(jsonArray.toString());

            rs.close();
            stmt.close();
            con.close();

        } catch (SQLException | ClassNotFoundException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", e.getMessage());
            out.println(errorResponse.toString());
            return;
        }

    }
}
