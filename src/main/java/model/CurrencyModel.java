package model;

import jdk.jfr.DataAmount;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyModel {
    private Long id;
    private String Code;
    private String FullName;
    private String Sign;
}
