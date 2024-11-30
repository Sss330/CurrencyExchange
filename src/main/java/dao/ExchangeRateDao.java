package dao;

import config.DataConfig;
import model.Currency;
import model.ExchangeRate;
import model.ExchangeRateResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    public List<ExchangeRateResponse> getAllExchangeRates() {
        List<ExchangeRateResponse> exchangeRates = new ArrayList<>();

        final String queryToGetAllExchangeRates = "SELECT \n" +
                "    er.ID AS ExchangeRateId,\n" +
                "    bc.ID AS BaseCurrencyId,\n" +
                "    bc.Code AS BaseCurrencyCode,\n" +
                "    bc.FullName AS BaseCurrencyName,\n" +
                "    bc.Sign AS BaseCurrencySign,\n" +
                "    tc.ID AS TargetCurrencyId,\n" +
                "    tc.Code AS TargetCurrencyCode,\n" +
                "    tc.FullName AS TargetCurrencyName,\n" +
                "    tc.Sign AS TargetCurrencySign,\n" +
                "    er.Rate\n" +
                "FROM ExchangeRates er\n" +
                "JOIN Currencies bc ON er.BaseCurrencyId = bc.ID\n" +
                "JOIN Currencies tc ON er.TargetCurrencyId = tc.ID;";

        try (Connection connection = DataConfig.getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(queryToGetAllExchangeRates)) {

            while (rs.next()) {
                exchangeRates.add(mapToExchangeRateResponse(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exchangeRates;
    }


    public static ExchangeRateResponse mapToExchangeRateResponse(ResultSet rs) throws SQLException {
        Currency baseCurrency = Currency.builder()
                .id(rs.getLong("BaseCurrencyId"))
                .code(rs.getString("BaseCurrencyCode"))
                .fullName(rs.getString("BaseCurrencyName"))
                .sign(rs.getString("BaseCurrencySign"))
                .build();

        Currency targetCurrency = Currency.builder()
                .id(rs.getLong("TargetCurrencyId"))
                .code(rs.getString("TargetCurrencyCode"))
                .fullName(rs.getString("TargetCurrencyName"))
                .sign(rs.getString("TargetCurrencySign"))
                .build();

        Double rate = BigDecimal.valueOf(rs.getDouble("Rate"))
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();

        return ExchangeRateResponse.builder()
                .id(rs.getLong("ExchangeRateId"))
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(rate)
                .build();
    }


    public List<ExchangeRate> getSpecificExchangeRate(String currency) throws SQLException {
        //заджойнить таблицы
        final String queryToGetSpecificExchangeRate = "select";

        List<ExchangeRate> specificExchangeRate = new ArrayList<>();
        try (Connection connection = DataConfig.getDataSource().getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryToGetSpecificExchangeRate);
        }

        return specificExchangeRate;
    }
}
