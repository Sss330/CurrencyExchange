
package servlets.exchangeRates;

import com.google.gson.Gson;
import dao.ExchangeRateDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRateResponse;

import java.math.BigDecimal;



public class PatchExchangeRate {

    /*private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    Gson gson = new Gson();

    protected void service(HttpServletRequest req, HttpServletResponse resp)   {
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo();
        
        String currencyPair = pathInfo.substring(1);

        String baseCurrencyCode = currencyPair.substring(0, 3);
        String targetCurrencyCode = currencyPair.substring(3, 6);

        String rateParam = req.getParameter("rate");

        try {
            BigDecimal patchedRate = new BigDecimal(rateParam);
            ExchangeRateResponse updatedExchangeRate = exchangeRateDao.patchExchangeRate(baseCurrencyCode, targetCurrencyCode, patchedRate);

            String jsonResponse = gson.toJson(updatedExchangeRate);
            resp.getWriter().write(jsonResponse);

        } catch (Exception e) {
          throw new RuntimeException(e);
        }
    }
}
*/
}
