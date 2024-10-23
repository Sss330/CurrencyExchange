package model.currency;

import org.json.JSONObject;
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

@WebServlet("/addNewCurrency")
public class NewCurrency extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DataSourceConfig.getDataSource().getConnection();

            String Name = req.getParameter("name");
            String Code = req.getParameter("code");
            String Sing = req.getParameter("sign");

            if (Name == null || Code == null || Sing == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing required fields.\"}");
                return;
            }

            String checkQuery = "SELECT COUNT(*) FROM currencies WHERE code = ?";
            pstmt = con.prepareStatement(checkQuery);
            pstmt.setString(1, Code);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                out.println("{\"error\": \"Currency with this code already exists.\"}");
                return;
            }

            String insertQuery = "INSERT INTO currencies (fullName, code, sing) VALUES (?, ?, ?)";
            pstmt = con.prepareStatement(insertQuery);

            pstmt.setString(1, Name);
            pstmt.setString(2, Code);
            pstmt.setString(3, Sing);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("name", Name);
                jsonResponse.put("code", Code);
                jsonResponse.put("sign", Sing);

                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.println(jsonResponse.toString());
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"error\": \"Failed to add the currency.\"}");
            }
        } catch (SQLException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(errorResponse.toString());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}