package hsoft.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MarketData {
    private String productId;
    private long quantity;
    private double price;
    private LocalDateTime timestamp; // to check which transaction occured at which time.
}
