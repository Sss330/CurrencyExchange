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

@WebServlet("/currency")
public class SpecificCurrency extends HttpServlet {
    private final Gson gson = new Gson();
    CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        String code = req.getParameter("code");

        try {
            List<Currency> specificCurrency = currencyDao.getSpecificCurrency(code);

            String jsonResponse = gson.toJson(specificCurrency);
            resp.getWriter().write(jsonResponse);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }
}
