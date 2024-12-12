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
public class CurrenciesServlet extends HttpServlet  {

    private final CurrencyDao currencyDao = new CurrencyDao();
    private final Gson gson = new Gson();


    private final String MESSAGE_BAD_REQUEST = gson.toJson("отсутствует нужное поле формы"); // 400
    private final String MESSAGE_CONFLICT = gson.toJson("валюта с таким кодом уже существует"); // 409
    private final String MESSAGE_SERVER_ERROR = gson.toJson("ошибка сервера"); //500


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            List<Currency> currencies = currencyDao.getAllCurrencies();
            String jsonResponse = gson.toJson(currencies);
            resp.getWriter().write(jsonResponse);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
            resp.getWriter().write(MESSAGE_SERVER_ERROR);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        String currencyCode = req.getParameter("code");
        String currencyName = req.getParameter("name");
        String currencySign = req.getParameter("sign");

        if (currencyCode == null || currencyName == null || currencySign == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            resp.getWriter().write(MESSAGE_BAD_REQUEST);
            return;
        }

        if (!currencyCode.matches("^[A-Za-z]+$") || !currencyName.matches("^[A-Za-z ]+$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("Код и имя валюты должны быть на латинице"));
            return;
        }


        if (currencyCode.length() != 3){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            resp.getWriter().write("код вальты не может быть больше или меньше 3 букв");
            return;
        }


        try {
            if (currencyDao.currencyExists(currencyCode)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                resp.getWriter().write(MESSAGE_CONFLICT);
                return;
            }

            Currency newCurrency = currencyDao.addNewCurrency(currencyCode, currencyName, currencySign);
            if (newCurrency != null) {
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201
                resp.getWriter().write(gson.toJson(newCurrency));
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            resp.getWriter().write(MESSAGE_SERVER_ERROR);
        }
    }
}







