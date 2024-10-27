package servlets.exchange;


import repository.DataSourceConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



@WebServlet("/GetCertainExchangeRate")
public class CertainExchangeRate extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = DataSourceConfig.getDataSource().getConnection();

            int baseCurrency = Integer.parseInt(req.getParameter("baseCurrency"));
            int targetCurrency = Integer.parseInt(req.getParameter("targetCurrency"));
            if (baseCurrency == targetCurrency) {

                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }


            String query = "select * from ExchangeRates where currency_code=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, req.getParameter("baseCurrency"));
            rs = pstmt.executeQuery();



        } catch (SQLException e) {
            try {
                throw new SQLException(e);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
