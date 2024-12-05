package service;

import dao.ExchangeCurrencyDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

@WebServlet("/exchange")
public class Service extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));

        try (Connection connection = DriverManager.getConnection("jdbc:your_database_url", "username", "password")) {
            ExchangeCurrencyDao dao = new ExchangeCurrencyDao();
            Optional<BigDecimal> rateOpt = dao.getDirectExchangeRate(from, to, connection);

            if (!rateOpt.isPresent()) {
                rateOpt = dao.getReverseExchangeRate(from, to, connection);
            }

            if (!rateOpt.isPresent()) {
                rateOpt = dao.getRateViaUSD(from, to, connection);
            }

            if (rateOpt.isPresent()) {
                BigDecimal convertedAmount = amount.multiply(rateOpt.get());
                resp.setContentType("application/json");
                resp.getWriter().write("{\"rate\":" + rateOpt.get() + ", \"convertedAmount\":" + convertedAmount + "}");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Exchange rate not found");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
