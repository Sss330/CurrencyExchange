package dao;

import config.DataConfig;
import model.Currency;
import model.ExchangeRateResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    public List<ExchangeRateResponse> getAllExchangeRates() {
        List<ExchangeRateResponse> exchangeRates = new ArrayList<>();

        final String queryToGetAllExchangeRates = "select \n" +
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }

    public List<ExchangeRateResponse> getExchangeRateById(Long baseCurrency, Long targetCurrency)  {
        List<ExchangeRateResponse> specificExchangeRates = new ArrayList<>();
        final String queryToGetSpecificExchangeRate = """
            SELECT 
                er.ID AS ExchangeRateId,
                bc.ID AS BaseCurrencyId,
                bc.Code AS BaseCurrencyCode,
                bc.FullName AS BaseCurrencyName,
                bc.Sign AS BaseCurrencySign,
                tc.ID AS TargetCurrencyId,
                tc.Code AS TargetCurrencyCode,
                tc.FullName AS TargetCurrencyName,
                tc.Sign AS TargetCurrencySign,
                er.Rate
            FROM ExchangeRates er
            JOIN Currencies bc ON er.BaseCurrencyId = bc.ID
            JOIN Currencies tc ON er.TargetCurrencyId = tc.ID
            WHERE bc.ID = ? AND tc.ID = ?;
            """;

        try (Connection connection = DataConfig.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(queryToGetSpecificExchangeRate)) {


            statement.setLong(1, baseCurrency);
            statement.setLong(2, targetCurrency);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    specificExchangeRates.add(mapToExchangeRateResponse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return specificExchangeRates;
    }

    public ExchangeRateResponse addNewExchangeRate(long baseCurrencyId, long targetCurrencyId, BigDecimal rate) throws SQLException {

        final String queryToAddRate = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";

        try (Connection connection = DataConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryToAddRate, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, baseCurrencyId);
            preparedStatement.setLong(2, targetCurrencyId);
            preparedStatement.setBigDecimal(3, rate);


            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException();


            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);

                    return ExchangeRateResponse.builder()
                            .id(newId)
                            .baseCurrency(Currency.builder().id(baseCurrencyId).build())
                            .targetCurrency(Currency.builder().id(targetCurrencyId).build())
                            .rate((rate))
                            .build();
                } else {
                    throw new SQLException();
                }
            }
        }
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

        BigDecimal rate = BigDecimal.valueOf((rs.getBigDecimal("Rate")
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue()));

        return ExchangeRateResponse.builder()
                .id(rs.getLong("ExchangeRateId"))
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate((rate))
                .build();
    }



}