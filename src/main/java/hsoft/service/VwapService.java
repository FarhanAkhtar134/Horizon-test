package hsoft.service;

import lombok.Data;

import java.util.Hashtable;

public interface VwapService {
    public void fairValueChanged(String productId, double fairValue);
    public void updateTransactionOccuredTable(String productId, long quantity, double price);
    public void updateVwapTable(String productId, double vwap);
    public double calculateVWAP(String productId, long days);
    public Hashtable getFairValueTable();
    public Hashtable getTransactionOccuredTable();
    public Hashtable getVwapTable();
    public void compareVwapFairValue(String productId);
}
