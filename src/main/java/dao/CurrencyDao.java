package dao;

import config.DataConfig;
import model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CurrencyDao {

    public List<Currency> getAllCurrencies() throws SQLException {

        List<Currency> currencies = new ArrayList<>();

        try (Connection connection = DataConfig.getDataSource().getConnection()) {

            Statement statement = connection.createStatement();
            final String queryToGetCurrencies = "select * from Currencies";
            ResultSet rs = statement.executeQuery(queryToGetCurrencies);

            while (rs.next())
                currencies.add(mapToCurrency(rs));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return currencies;
    }

    public List<Currency> getSpecificCurrency(String Code) throws SQLException {
        List<Currency> specificCurrency = new ArrayList<>();

        final String queryToGetSpecificCurrency = "select * from Currencies where Code = ?";

        try (Connection connection = DataConfig.getDataSource().getConnection()) {
            PreparedStatement prepareStatement = connection.prepareStatement(queryToGetSpecificCurrency);
            prepareStatement.setString(1, Code);
            ResultSet rs = prepareStatement.executeQuery();

            while (rs.next())
                specificCurrency.add(mapToCurrency(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return specificCurrency;
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
