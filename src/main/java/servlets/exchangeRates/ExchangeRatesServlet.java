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

    private final String MESSAGE_BAD_REQUEST = gson.toJson("отсутствует нужное поле формы");// 400
    private final String MESSAGE_CONFLICT = gson.toJson("такой обменик уже существует");// 409
    private final String MESSAGE_SERVER_ERROR = gson.toJson("ошибка сервера");//500
    private final String MESSAGE_NOT_FOUND = gson.toJson("Валюта не найдена");//404

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            List<ExchangeRateResponse> allExchangeRates = exchangeRateDao.getAllExchangeRates();
            String jsonResponse = gson.toJson(allExchangeRates);
            resp.getWriter().write(jsonResponse);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(MESSAGE_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        long baseCurrencyId = Long.parseLong(req.getParameter("base"));
        long targetCurrencyId = Long.parseLong(req.getParameter("target"));
        BigDecimal rate = new BigDecimal(req.getParameter("rate"));

        if (baseCurrencyId <= 0 || targetCurrencyId <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(MESSAGE_BAD_REQUEST);
            return;
        }

        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("курс не может быть меньше 0");
        }

        try {
            ExchangeRateResponse NewExchangeRate = exchangeRateDao.addNewExchangeRate(baseCurrencyId, targetCurrencyId, rate);

            if (NewExchangeRate == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(MESSAGE_NOT_FOUND);
                return;
            }

            if (NewExchangeRate != null) {
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                resp.getWriter().write(gson.toJson(NewExchangeRate));
            }

            if (exchangeRateDao.exchangeRateExists(baseCurrencyId, targetCurrencyId)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                resp.getWriter().write(MESSAGE_CONFLICT);
            }

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(MESSAGE_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}

