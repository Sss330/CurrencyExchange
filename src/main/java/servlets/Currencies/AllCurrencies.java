package servlets.Currencies;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.ContentType;

import java.sql.SQLException;

@WebServlet ("/getAllCurrencies")
public class AllCurrencies extends HttpServlet  {

    @Override
    protected void doGet  (HttpServletRequest request, HttpServletResponse response) {


    }


}
