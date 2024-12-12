package dao;

import config.DataConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class ExchangeCurrencyDao {

    public Optional<Integer> getCurrencyIdByCode(String code) throws Exception {
        String sql = "SELECT ID FROM Currencies WHERE Code = ?";
        try (Connection connection = DataConfig.getDataSource().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getInt("ID"));
            }
        }
        return Optional.empty();
    }

    public Optional<BigDecimal> getDirectExchangeRate(String from, String to) throws Exception {
        Optional<Integer> fromIdOpt = getCurrencyIdByCode(from);
        Optional<Integer> toIdOpt = getCurrencyIdByCode(to);

        if (fromIdOpt.isEmpty() || toIdOpt.isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT rate FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
        try (Connection connection = DataConfig.getDataSource().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, fromIdOpt.get());
            stmt.setInt(2, toIdOpt.get());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getBigDecimal("rate"));
            }
        }
        return Optional.empty();
    }

    public Optional<BigDecimal> getReverseExchangeRate(String from, String to) throws Exception {
        Optional<Integer> fromIdOpt = getCurrencyIdByCode(from);
        Optional<Integer> toIdOpt = getCurrencyIdByCode(to);

        if (fromIdOpt.isEmpty() || toIdOpt.isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT rate FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
        try (Connection connection = DataConfig.getDataSource().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, toIdOpt.get());
            stmt.setInt(2, fromIdOpt.get());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal reverseRate = BigDecimal.ONE.divide(rs.getBigDecimal("rate"), 4, BigDecimal.ROUND_HALF_UP);
                return Optional.of(reverseRate);
            }
        }
        return Optional.empty();
    }

    public Optional<BigDecimal> getRateViaUSD(String from, String to) throws Exception {
        Optional<BigDecimal> rateFromUSD = getDirectExchangeRate("USD", from);
        Optional<BigDecimal> rateToUSD = getDirectExchangeRate("USD", to);

        if (rateFromUSD.isPresent() && rateToUSD.isPresent()) {
            return Optional.of(rateToUSD.get().divide(rateFromUSD.get(), 4, BigDecimal.ROUND_HALF_UP));
        }
        return Optional.empty();
    }
}
