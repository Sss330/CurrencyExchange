package servlets.currency;

import org.json.JSONObject;
import repository.DataSourceConfig;

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

@WebServlet("/AddNewCurrency")
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

            String name = req.getParameter("name");
            String code = req.getParameter("code");
            String sign = req.getParameter("sign");

            if (name == null || code == null || sign == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missign required fields.\"}");
                return;
            }

            String checkQuery = "SELECT COUNT(*) FROM currencies WHERE code = ?";
            pstmt = con.prepareStatement(checkQuery);
            pstmt.setString(1, code);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                out.println("{\"error\": \"Currency with this code already exists.\"}");
                return;
            }

            String insertQuery = "INSERT INTO currencies (fullName, code, sign) VALUES (?, ?, ?)";
            pstmt = con.prepareStatement(insertQuery);

            pstmt.setString(1, name);
            pstmt.setString(2, code);
            pstmt.setString(3, sign);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("name", name);
                jsonResponse.put("code", code);
                jsonResponse.put("sign", sign);

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