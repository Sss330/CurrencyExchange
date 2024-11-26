package dao;

import config.DataConfig;
import model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class CurrencyDao {
    final String queryToGetCurrencies = "select ID,Code,FullName,Sign from Сurrencies";
    //final String QueryToGetSpecificCurrencies = "select ";

    public List<Currency> getAllCurrencies() throws SQLException {

        List<Currency> currencies = new ArrayList<>();


        try (Connection connection = DataConfig.getDataSource().getConnection()) {

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(queryToGetCurrencies);

            while (rs.next()) {
                Currency currencyModel = mapToCurrency(rs);

                currencies.add(currencyModel);
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return currencies;
    }

    public List<Currency> getSpecificCurrency() {
        List<Currency> Currency = new ArrayList<>();


        PreparedStatement statement = null;
        return Currency;
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
