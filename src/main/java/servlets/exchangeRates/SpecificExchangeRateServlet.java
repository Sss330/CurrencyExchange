package servlets.exchangeRates;

import com.google.gson.Gson;
import dao.ExchangeRateDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRateResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRate/*")
public class SpecificExchangeRateServlet extends HttpServlet {
    Gson gson = new Gson();
    ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    private final String MESSAGE_BAD_REQUEST = gson.toJson("коды валют пары отсутствуют в адресе");// 400
    private final String MESSAGE_SERVER_ERROR = gson.toJson("ошибка сервера");//500
    private final String MESSAGE_NOT_FOUND = gson.toJson("Валюта не найдена");//404


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() < 7) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(MESSAGE_BAD_REQUEST);
            return;
        }

        String base = pathInfo.substring(1, 4).toUpperCase();
        String target = pathInfo.substring(4, 7).toUpperCase();


        if (!base.matches("^[A-Z]{3}$") || !target.matches("^[A-Z]{3}$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("коды валют должны содержать 3 буквы латиницы"));
            return;
        }

        try {
            ExchangeRateResponse exchangeRate = exchangeRateDao.getExchangeRateByCodes(base, target);

            if (exchangeRate == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(MESSAGE_NOT_FOUND);
                return;
            }

            String jsonResponse = gson.toJson(exchangeRate);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(jsonResponse);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(MESSAGE_SERVER_ERROR);
        }
    }
}
