
package servlets.exchangeRates;

import com.google.gson.Gson;
import dao.ExchangeRateDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRateResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    Gson gson = new Gson();
    ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        try {
            List<ExchangeRateResponse> allExchangeRates = exchangeRateDao.getAllExchangeRates();
            String jsonResponse = gson.toJson(allExchangeRates);
            resp.getWriter().write(jsonResponse);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");


        long baseCurrencyId = Long.parseLong(req.getParameter("base"));
        long targetCurrencyId = Long.parseLong(req.getParameter("target"));
        BigDecimal rate = new BigDecimal (req.getParameter("rate"));

        try {


            List<ExchangeRateResponse> allExchangeRates = (List<ExchangeRateResponse>) exchangeRateDao.addNewExchangeRate(baseCurrencyId,targetCurrencyId,rate);
            String jsonResponse = gson.toJson(allExchangeRates);
            resp.getWriter().write(jsonResponse);

        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

