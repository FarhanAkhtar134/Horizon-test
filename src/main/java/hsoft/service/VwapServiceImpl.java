package hsoft.service;

import hsoft.model.MarketData;
import hsoft.model.PricingData;
import hsoft.util.Utility;
import lombok.extern.log4j.Log4j;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class VwapServiceImpl implements VwapService {
    private Hashtable fairValueTable;
    private Hashtable transactionOccuredTable;
    private Hashtable vwapTable;

    public VwapServiceImpl() {
        this.fairValueTable = new Hashtable<String, PricingData>();
        this.transactionOccuredTable = new Hashtable<String, List<MarketData>>();
        this.vwapTable = new Hashtable<String, Double>();
    }

    @Override
    public void fairValueChanged(String productId, double fairValue) {
        log.info(String.format("Receiving fairValueChanged for productId: %s, fairValue: %s", productId, fairValue));
        try {
            // using builder design pattern
            PricingData pricingData = PricingData
                    .builder()
                    .productId(productId)
                    .fairValue(fairValue)
                    .build();
            // overriding the pricing data at the productId key.
            fairValueTable.put(pricingData.getProductId(), pricingData);
            log.info(String.format("Fair Value for %s is %s", pricingData.getProductId(), pricingData.getFairValue()));
            // looping the table to check if the pricing data key already exists.
            fairValueTable.forEach((key, value) -> {
                if (
                        !((String) key).equalsIgnoreCase(
                                pricingData.getProductId()
                        )
                ) {
                    log.info(String.format("Fair Value for %s is unchanged", key));
                }
            });
        } catch (Exception e) {
            log.error(String.format("Error updating hast table for fairValue with message: %s", e.getMessage()));
        }

    }

    @Override
    public void updateTransactionOccuredTable(String productId, long quantity, double price) {
        log.info(String.format("Receiving transactionOccured for productId: %s, quantity: %s, price: %s", productId, quantity, price));
        try {
            MarketData marketData = MarketData
                    .builder()
                    .productId(productId)
                    .quantity(quantity)
                    .price(price)
                    .timestamp(LocalDateTime.now()) //assign timestamp to incoming transactions for sorting
                    .build();
            List<MarketData> marketDataList = (List<MarketData>) Optional.ofNullable(
                    transactionOccuredTable.get(marketData.getProductId())
            ).orElseGet((() -> new ArrayList<MarketData>()));
            marketDataList.add(marketData);
            transactionOccuredTable.put(
                    marketData.getProductId(),
                    marketDataList
            );
            log.info(String.format("Adding transaction occured to hash table for productId: %s, quantity: %s, price: %s", marketData.getProductId(), marketData.getQuantity(), marketData.getPrice()));
        } catch (Exception e) {
            log.error(String.format("Error updating hast table for TransactionOccured with message: %s", e.getMessage()));
        }
    }

    @Override
    public void updateVwapTable(String productId, double vwap) {
        try {
            log.info(String.format("Receiving vwap for productId: %s, vwap: %s", productId, vwap));
            vwapTable.put(
                    productId,
                    vwap
            );
            log.info(String.format("Adding vwap to hash table for productId: %s, vwap: %s", productId, vwap));
        } catch (Exception e) {
            log.error(String.format("Error Adding vwap to hash table for productId: %s, vwap: %s, with error message: %s", productId, vwap, e.getMessage()));
        }
    }

    @Override
    public double calculateVWAP(String productId, long days) {
        double sumOfQuantityPrice;
        double sumOfQuantity;
        double vwap = 0;
        try {

            this.getTransactionOccuredTable().forEach((key, value) -> {
                if (
                        !((String) key).equalsIgnoreCase(
                                productId
                        )
                ) {
                    log.info(String.format("VWAP for %s is unchanged", key));
                }
            });

            List<MarketData> marketDataList = (
                    (List<MarketData>) Optional.ofNullable(
                            this.getTransactionOccuredTable().get(productId)
                    ).orElse(new ArrayList<MarketData>())
            )
                    .stream()
                    .sorted(Comparator.comparing(MarketData::getTimestamp).reversed()) // reduction operation
                    .limit(days)
                    .collect(Collectors.toList());

            if (marketDataList.size() == 0) {
                log.warn(String.format("VWAP for %s = %s for last %s transactions", productId, vwap, marketDataList.size()));
                return 0; // if there are no transactions
            }
            sumOfQuantityPrice = marketDataList
                    .stream()
                    .mapToDouble(marketData -> marketData.getQuantity() * marketData.getPrice())
                    .sum();
            sumOfQuantity = marketDataList
                    .stream()
                    .mapToDouble(marketData -> marketData.getQuantity())
                    .sum();
            vwap = Utility.round(sumOfQuantityPrice / sumOfQuantity, 4); // round to 4 decimal places
            log.info(String.format("VWAP for %s = %s for last %s transactions", productId, vwap, marketDataList.size()));
        } catch (Exception e) {
            log.error(String.format("Error calculating Volume Weighted Average Price for productId: %s with error message: %s", productId, e.getMessage()));
        }
        return vwap;
    }

    @Override
    public Hashtable getFairValueTable() {
        return fairValueTable;
    }

    @Override
    public Hashtable getTransactionOccuredTable() {
        return transactionOccuredTable;
    }

    @Override
    public Hashtable getVwapTable() {
        return vwapTable;
    }

    // didnt create settable method to ensure no overriding of the hashtable. Only have gettable method to retrieve the table.

    @Override
    public void compareVwapFairValue(String productId) {
        log.info(String.format("start comparing vwap and fairValue for %s", productId));
        double vwap = (double) Optional.ofNullable(this.getVwapTable().get(productId)).orElse(0.0);
        double fairValue = Optional.ofNullable((PricingData) this.getFairValueTable().get(productId))
                .map(PricingData::getFairValue).orElse(0.0);
        if (vwap > fairValue) {
            log.info(String.format("VWAP(%s) > FairValue(%s)", vwap, fairValue));
            if ("TEST_PRODUCT".equalsIgnoreCase(productId)) {
                log.warn(String.format("VWAP(%s) > FairValue(%s)", vwap, fairValue));
            }
        }
    }
}
