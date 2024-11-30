package model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRateResponse {
    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private Double rate;
}
