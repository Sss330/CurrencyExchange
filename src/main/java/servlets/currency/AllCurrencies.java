package servlets.currency;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import org.json.JSONArray;
import org.json.JSONObject;
import repository.DataSourceConfig;


@WebServlet("/Currencies")
public class AllCurrencies extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        JSONArray jsonArray = new JSONArray();

        try {
            //подключение к бд
            Connection con = null;
            con = DataSourceConfig.getDataSource().getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Currencies");

            while (rs.next()) {

                String code = rs.getString("code");
                String fullName = rs.getString("fullName");
                String sign = rs.getString("sign");
                int id = rs.getInt("id");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("fullName", fullName);
                jsonObject.put("code", code);
                jsonObject.put("sign", sign);

                jsonArray.put(jsonObject);
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", e.getMessage());
            out.println(errorResponse.toString());
            return;
        }

        out.println(jsonArray.toString());
    }
}