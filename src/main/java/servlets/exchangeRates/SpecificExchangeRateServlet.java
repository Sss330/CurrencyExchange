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


}
