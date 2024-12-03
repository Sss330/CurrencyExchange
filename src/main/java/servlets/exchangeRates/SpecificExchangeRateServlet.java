package servlets.exchangeRates;

import com.google.gson.Gson;
import dao.ExchangeRateDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRateResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRate")
public class SpecificExchangeRateServlet extends HttpServlet {
    Gson gson = new Gson();
    ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        Long base = Long.valueOf(req.getParameter("baseCurrency"));
        Long target = Long.valueOf(req.getParameter("targetCurrency"));


        try {
            List<ExchangeRateResponse> allExchangeRates = exchangeRateDao.getExchangeRateById(base,target);
            String jsonResponse = gson.toJson(allExchangeRates);
            resp.getWriter().write(jsonResponse);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
