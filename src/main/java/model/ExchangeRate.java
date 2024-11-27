package model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRate {
    private Long id;
    private Long baseCurrencyId;
    private Long targetCurrencyId;
    private Long rate;
}


