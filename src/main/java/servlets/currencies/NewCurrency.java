package servlets.currencies;

import com.google.gson.Gson;
import dao.CurrencyDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/newCurrency")
public class NewCurrency extends HttpServlet {
    CurrencyDao currencyDao = new CurrencyDao();
    Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String currencyCode = req.getParameter("code");
        String currencyName = req.getParameter("name");
        String currencySign = req.getParameter("sign");


        try {
           Currency newCurrency = currencyDao.addNewCurrency(currencyCode, currencyName, currencySign);

            String jsonResponse = gson.toJson(newCurrency);
            resp.getWriter().write(jsonResponse);
            resp.setStatus(201);
        }catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }
}
