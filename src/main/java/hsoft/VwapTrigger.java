package hsoft;

import com.hsoft.codingtest.DataProvider;
import com.hsoft.codingtest.DataProviderFactory;
import com.hsoft.codingtest.MarketDataListener;
import com.hsoft.codingtest.PricingDataListener;
import hsoft.service.VwapService;
import hsoft.service.VwapServiceImpl;
import org.apache.log4j.LogManager;

public class VwapTrigger {

    // code on abstraction for less coupling
    private static final VwapService vwapService = new VwapServiceImpl(); //can use dependency injection here -> @Autowired in Spring Boot Project

    public static void main(String[] args) {
        DataProvider provider = DataProviderFactory.getDataProvider();
        provider.addMarketDataListener(new MarketDataListener() {
            public void transactionOccured(String productId, long quantity, double price) {
                // TODO Start to code here when a transaction occurred
                vwapService.updateTransactionOccuredTable(productId, quantity, price); //save transaction occured history in a hash table
                double vwap = vwapService.calculateVWAP(productId, 5);
                vwapService.updateVwapTable(productId, vwap);
                vwapService.compareVwapFairValue(productId);  // one to one function single responsibility
            }
        });
        provider.addPricingDataListener(new PricingDataListener() {
            public void fairValueChanged(String productId, double fairValue) {
                // TODO Start to code here when a fair value changed
                vwapService.fairValueChanged(productId, fairValue);
                vwapService.compareVwapFairValue(productId);
            }
        });

        provider.listen();
        // When this method returns, the test is finished and you can check your results in the console
        LogManager.shutdown();
    }
}