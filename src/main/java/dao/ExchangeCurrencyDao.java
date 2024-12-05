package dao;

import model.ExchangeRateResponse;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class ExchangeCurrencyDao {


    public Optional<BigDecimal> getDirectExchangeRate(String from, String to, Connection connection) throws Exception {

        String sql = "SELECT rate FROM ExchangeRates WHERE from_currency = ? AND to_currency = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, from);
            stmt.setString(2, to);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getBigDecimal("rate"));
            }
        }
        return Optional.empty();
    }

    public Optional<BigDecimal> getReverseExchangeRate(String from, String to, Connection connection) throws Exception {
        String sql = "SELECT rate FROM ExchangeRates WHERE from_currency = ? AND to_currency = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, to);
            stmt.setString(2, from);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal reverseRate = BigDecimal.ONE.divide(rs.getBigDecimal("rate"), 4, BigDecimal.ROUND_HALF_UP);
                return Optional.of(reverseRate);
            }
        }
        return Optional.empty();
    }

    public Optional<BigDecimal> getRateViaUSD(String from, String to, Connection connection) throws Exception {

        Optional<BigDecimal> rateFromUSD = getDirectExchangeRate("USD", from, connection);
        Optional<BigDecimal> rateToUSD = getDirectExchangeRate("USD", to, connection);

        if (rateFromUSD.isPresent() && rateToUSD.isPresent()) {

            return Optional.of(rateToUSD.get().divide(rateFromUSD.get(), 4, BigDecimal.ROUND_HALF_UP));
        }
        return Optional.empty();
    }
}
