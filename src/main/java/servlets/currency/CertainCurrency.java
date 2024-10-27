package servlets.currency;

import org.json.JSONArray;
import org.json.JSONObject;
import repository.DataSourceConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

//todo доделать коды ошибок в блоке catch

@WebServlet("/GetSpecificCurrency")
public class CertainCurrency extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONArray jsonArray = new JSONArray();
        String selectedCode = req.getParameter("selectedCode");
        Connection con = null;

        try {

            con = DataSourceConfig.getDataSource().getConnection();

            String query = "SELECT * FROM currencies WHERE code = ?";
            if(true){
            //проверить гет параметры
            }
            PreparedStatement pstmt = con.prepareStatement(query);


            pstmt.setString(1, selectedCode);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("fullName");
                String code = rs.getString("code");
                String sign = rs.getString("sign");


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("fullName", fullName);
                jsonObject.put("code", code);
                jsonObject.put("sign", sign);

                jsonArray.put(jsonObject);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        out.println(jsonArray.toString());
    }
}
