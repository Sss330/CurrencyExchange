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

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new CurrencyDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            List<Currency> currencies = currencyDao.getAllCurrencies();

            String jsonResponse = gson.toJson(currencies);

            resp.getWriter().write(jsonResponse);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
