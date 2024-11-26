package model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRate {
    private Long id;
    private String BaseCurrencyId;
    private String TargetCurrencyId;
    private String Rate;
}

