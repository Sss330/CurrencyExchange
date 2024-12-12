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
public class SpecificCurrencyServlet extends HttpServlet {
    private final Gson gson = new Gson();
    CurrencyDao currencyDao = new CurrencyDao();

    private final String MESSAGE_BAD_REQUEST = gson.toJson("код валюты отсутствует в адресе");// 400
    private final String MESSAGE_CONFLICT = gson.toJson("валюта с таким кодом уже существует");// 409
    private final String MESSAGE_SERVER_ERROR = gson.toJson("ошибка сервера");//500
    private final String MESSAGE_NOT_FOUND = gson.toJson("Валюта не найдена");//404

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String code = req.getParameter("code");

        if (code == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
            resp.getWriter().write(MESSAGE_BAD_REQUEST);
            return;
        }

        if (code.length() != 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            resp.getWriter().write("код вальты не может быть больше или меньше 3 букв");
            return;
        }
        if (!code.matches("^[A-Za-z]+$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("Код валюты должен быть на латинице"));
            return;
        }

        try {
            Currency specificCurrency = currencyDao.getCurrencyByCode(code);

            if (specificCurrency == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                resp.getWriter().write(MESSAGE_NOT_FOUND);
                return;
            }
            if (currencyDao.currencyExists(code)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                resp.getWriter().write(MESSAGE_CONFLICT);
                return;
            }

            String jsonResponse = gson.toJson(specificCurrency);
            resp.setStatus(HttpServletResponse.SC_OK); // 200
            resp.getWriter().write(jsonResponse);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            resp.getWriter().write(MESSAGE_SERVER_ERROR);
        }

    }
}
