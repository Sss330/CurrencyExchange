package dao;

import model.CurrencyModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class CurrencyDao {


    public List<CurrencyModel> getAllCurrencies() throws SQLException {

        List<CurrencyModel> Currencies = new ArrayList<>();

        final String query = "select ID,Code,FullName,Sign from Currencies";
        try (Connection connection = DataConfig.getDataSource().getConnection()) {

            PreparedStatement statement = (PreparedStatement) connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                CurrencyModel currencyModel = new CurrencyModel(
                        //забилдеть
                        rs.getLong("ID"),
                        rs.getString("FullName"),
                        rs.getString("FullName"),
                        rs.getString("Sign")
                );
                Currencies.add(currencyModel);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Currencies;
    }

}
