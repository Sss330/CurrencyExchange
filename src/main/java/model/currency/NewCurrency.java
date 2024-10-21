package model.currency;

import org.json.JSONArray;
import utils.example.config.DataSourceConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//todo реализовать метод

@WebServlet("/addNewCurrency")
public class NewCurrency extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        JSONArray jsonArray = new JSONArray();

        PrintWriter out = resp.getWriter();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            //подключение к бд
            con = DataSourceConfig.getDataSource().getConnection();
            String currencyName = req.getParameter("currency");

            String query = "insert into currencies (currencyName) values (?)  ";
            pstmt = con.prepareStatement(query);

            pstmt.setString(1, currencyName);
            pstmt.executeUpdate();


            while (rs.next()) {


            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
