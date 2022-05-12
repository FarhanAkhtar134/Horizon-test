package hsoft.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricingData {
    private String productId;
    private double fairValue;
}
