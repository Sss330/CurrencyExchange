package dao;

import config.DataConfig;
import model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {

    public List<Currency> getAllCurrencies() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM Currencies";

        try (Connection connection = DataConfig.getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                currencies.add(mapToCurrency(rs));
            }
        }
        return currencies;
    }

    public Currency getCurrencyByCode(String code) throws SQLException {

        String query = "SELECT * FROM Currencies WHERE Code = ?";

        try (Connection connection = DataConfig.getDataSource().getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapToCurrency(rs);
                }
            }
        }
        return null;
    }

    public Currency addNewCurrency(String code, String fullName, String sign) throws SQLException {
        String query = "INSERT INTO Currencies (Code, fullName, Sign) VALUES (?, ?, ?)";
        Currency currency = null;

        try (Connection connection = DataConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, fullName);
            preparedStatement.setString(3, sign);
            if (preparedStatement.executeUpdate() > 0) {
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        currency = Currency.builder()
                                .id(rs.getLong(1))
                                .code(code)
                                .fullName(fullName)
                                .sign(sign)
                                .build();
                    }
                }
            }
        }
        return currency;
    }

    public boolean currencyExists(String code) throws SQLException {
        String query = "SELECT 1 FROM Currencies WHERE Code = ?";

        try (Connection connection = DataConfig.getDataSource().getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        }
    }


    private static Currency mapToCurrency(ResultSet rs) throws SQLException {
        return Currency.builder()
                .id(rs.getLong("ID"))
                .code(rs.getString("Code"))
                .fullName(rs.getString("FullName"))
                .sign(rs.getString("Sign"))
                .build();
    }
}
