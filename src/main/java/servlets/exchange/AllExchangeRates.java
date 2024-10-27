package servlets.exchange;

import org.json.JSONArray;
import org.json.JSONObject;
import repository.DataSourceConfig;

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

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = DataSourceConfig.getDataSource().getConnection();
            stmt = con.createStatement();
            String query = "SELECT er.id, er.rate, bc.id AS baseCurrencyId, bc.fullName AS baseCurrencyName, bc.code AS baseCurrencyCode, bc.sign AS baseCurrencySign, " +
                    "tc.id AS targetCurrencyId, tc.fullName AS targetCurrencyName, tc.code AS targetCurrencyCode, tc.sign AS targetCurrencySign " +
                    "FROM ExchangeRates er " +
                    "JOIN Currencies bc ON er.BaseCurrencyId = bc.id " +
                    "JOIN Currencies tc ON er.TargetCurrencyId = tc.id";
            rs = stmt.executeQuery(query);

            if (rs.next()){
                //ответить статусом
            }

            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();

                JSONObject baseCurrency = new JSONObject();
                baseCurrency.put("id", rs.getInt("baseCurrencyId"));
                baseCurrency.put("name", rs.getString("baseCurrencyName"));
                baseCurrency.put("code", rs.getString("baseCurrencyCode"));
                baseCurrency.put("sign", rs.getString("baseCurrencySign"));

                JSONObject targetCurrency = new JSONObject();
                targetCurrency.put("id", rs.getInt("targetCurrencyId"));
                targetCurrency.put("name", rs.getString("targetCurrencyName"));
                targetCurrency.put("code", rs.getString("targetCurrencyCode"));
                targetCurrency.put("sign", rs.getString("targetCurrencySign"));

                jsonObject.put("baseCurrency", baseCurrency);
                jsonObject.put("targetCurrency", targetCurrency);
                jsonObject.put("rate", rs.getDouble("rate"));

                jsonArray.put(jsonObject);
            }

            out.println(jsonArray.toString());
        } catch (SQLException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(errorResponse.toString());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}