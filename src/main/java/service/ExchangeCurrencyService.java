package service;

import com.google.gson.Gson;
import dao.ExchangeCurrencyDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.ExchangeCurrencyModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeCurrencyService extends HttpServlet {

    private final ExchangeCurrencyDao dao = new ExchangeCurrencyDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountParam = req.getParameter("amount");

        if (from == null || to == null || amountParam == null) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "все параметры (from, to, amount) обязательны.");
            return;
        }

        if (!from.matches("^[A-Za-z]{3}$") || !to.matches("^[A-Za-z]{3}$")) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "коды валют должны состоять из трёх латинских букв.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountParam);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "сумма должна быть больше нуля.");
                return;
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "сумма должна быть числом.");
            return;
        }

        try {
            Optional<BigDecimal> rateOpt = getExchangeRate(from.toUpperCase(), to.toUpperCase());

            if (rateOpt.isEmpty()) {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        String.format("курс обмена для валютной пары %s -> %s не найден.", from, to));
                return;
            }

            BigDecimal rate = rateOpt.get();
            BigDecimal convertedAmount = amount.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);


            ExchangeCurrencyModel exchangeCurrencyModel = ExchangeCurrencyModel.builder()
                    .baseCurrency(Currency.builder().code(from).build())
                    .targetCurrency(Currency.builder().code(to).build())
                    .rate(rate)
                    .amount(amount)
                    .convertedAmount(convertedAmount)
                    .build();

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(exchangeCurrencyModel));

        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ошибка сервера.");
        }
    }

    private Optional<BigDecimal> getExchangeRate(String from, String to) throws Exception {
        Optional<BigDecimal> rateOpt = dao.getDirectExchangeRate(from, to);
        if (rateOpt.isEmpty()) rateOpt = dao.getReverseExchangeRate(from, to);
        if (rateOpt.isEmpty()) rateOpt = dao.getRateViaUSD(from, to);
        return rateOpt;
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(message));
    }
}
