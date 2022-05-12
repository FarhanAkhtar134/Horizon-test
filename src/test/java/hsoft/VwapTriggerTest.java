package hsoft;

import hsoft.model.MarketData;
import hsoft.service.VwapService;
import hsoft.service.VwapServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Hashtable;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

// One may want to add some automatic test(s) here
public class VwapTriggerTest {

    private VwapService vwapService;

    @BeforeEach
    void init() {
        vwapService = new VwapServiceImpl();
    }

    @Test
    void shouldXxxx() {

    }

    @Test
    void test_fairValueChanged_success() {
        vwapService.fairValueChanged("P1", 11.0);
        vwapService.fairValueChanged("P1", 10.9);
        vwapService.fairValueChanged("P2", 12.5);
        vwapService.fairValueChanged("P3", 13.5);
        vwapService.fairValueChanged("P1", 14.5);

        Hashtable fairValueTable = vwapService.getFairValueTable();

        assertThat(fairValueTable.size(), is(3));
        assertThat(fairValueTable.containsKey("P1"), is(true));
        assertThat(fairValueTable.containsKey("P2"), is(true));
        assertThat(fairValueTable.containsKey("P3"), is(true));
    }


    @Test
    void test_updateTransactionOccuredTable_success() {
        vwapService.updateTransactionOccuredTable("P1", 1000, 10);
        vwapService.updateTransactionOccuredTable("P1", 1001, 10.1);
        vwapService.updateTransactionOccuredTable("P2", 2000, 12.0);
        vwapService.updateTransactionOccuredTable("P2", 2001, 12.1);
        vwapService.updateTransactionOccuredTable("P1", 1002, 10.2);
        vwapService.updateTransactionOccuredTable("P3", 300, 5.2);

        Hashtable transactionOccuredTable = vwapService.getTransactionOccuredTable();

        assertThat(transactionOccuredTable.size(), is(3));
        assertThat(transactionOccuredTable.containsKey("P1"), is(true));
        assertThat((List<MarketData>) transactionOccuredTable.get("P1"), hasSize(3));
        assertThat(transactionOccuredTable.containsKey("P2"), is(true));
        assertThat((List<MarketData>) transactionOccuredTable.get("P2"), hasSize(2));
        assertThat(transactionOccuredTable.containsKey("P3"), is(true));
        assertThat((List<MarketData>) transactionOccuredTable.get("P3"), hasSize(1));
    }

    @Test
    void test_calculateVWAP_0entries_success() {
        try {
            vwapService.updateTransactionOccuredTable("P1", 1000, 10);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 1001, 10.1);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P2", 2000, 12.0);
            vwapService.updateTransactionOccuredTable("P2", 2001, 12.1);
            vwapService.updateTransactionOccuredTable("P1", 1002, 10.2);
            vwapService.updateTransactionOccuredTable("P3", 300, 5.2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double vwap = vwapService.calculateVWAP("P5", 5);
        assertThat(vwap, is(0.0));
    }

    @Test
    void test_calculateVWAP_3entries_success() {
        try {
            vwapService.updateTransactionOccuredTable("P1", 1000, 10);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 1001, 10.1);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P2", 2000, 12.0);
            vwapService.updateTransactionOccuredTable("P2", 2001, 12.1);
            vwapService.updateTransactionOccuredTable("P1", 1002, 10.2);
            vwapService.updateTransactionOccuredTable("P3", 300, 5.2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double vwap = vwapService.calculateVWAP("P1", 5);
        assertThat(vwap, is(10.1001));
    }

    @Test
    void test_calculateVWAP_8entries_success() {
        try {
            vwapService.updateTransactionOccuredTable("P1", 1000, 10);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 1001, 10.1);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P2", 2000, 12.0);
            vwapService.updateTransactionOccuredTable("P2", 2001, 12.1);
            vwapService.updateTransactionOccuredTable("P1", 1002, 10.2);
            vwapService.updateTransactionOccuredTable("P3", 300, 5.2);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 345, 15.8);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 433, 55.89);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 875, 84.383);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 246, 74.7483);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 673, 36.563);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double vwap = vwapService.calculateVWAP("P1", 5);
        assertThat(vwap, is(56.9524));
    }

    @Test
    public void test_calculateVWAP_success() {

        try {
            vwapService.updateTransactionOccuredTable("P1", 1000, 10);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 1001, 10.1);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P2", 2000, 12.0);
            vwapService.updateTransactionOccuredTable("P2", 2001, 12.1);
            vwapService.updateTransactionOccuredTable("P3", 345, 834.4);
            vwapService.updateTransactionOccuredTable("P1", 1002, 10.2);
            vwapService.updateTransactionOccuredTable("P3", 300, 5.2);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 345, 15.8);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 433, 55.89);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 875, 84.383);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 246, 74.7483);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("P1", 673, 36.563);

            double vwap1 = vwapService.calculateVWAP("P1", 5);
            double vwap2 = vwapService.calculateVWAP("P2", 5);
            double vwap3 = vwapService.calculateVWAP("P3", 5);
            vwapService.updateVwapTable("P1", vwap1);
            vwapService.updateVwapTable("P2", vwap2);
            vwapService.updateVwapTable("P3", vwap3);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Hashtable vwapTable = vwapService.getVwapTable();

        assertThat(vwapTable.size(), is(3));
        assertThat(vwapTable.containsKey("P1"), is(true));
        assertThat(vwapTable.get("P1"), is(56.9524));
        assertThat(vwapTable.containsKey("P2"), is(true));
        assertThat(vwapTable.get("P2"), is(12.05));
        assertThat(vwapTable.containsKey("P3"), is(true));
        assertThat(vwapTable.get("P3"), is(448.7256));

    }

    @Test
    public void test_compareVwapFairValue_success() {
        try {
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 1000, 10);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 1001, 10.1);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 2000, 12.0);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 2001, 12.1);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 345, 834.4);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 1002, 10.2);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 300, 5.2);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 345, 15.8);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 433, 55.89);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 875, 84.383);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 246, 74.7483);
            Thread.sleep(1000);
            vwapService.updateTransactionOccuredTable("TEST_PRODUCT", 673, 36.563);

            vwapService.fairValueChanged("TEST_PRODUCT", 11.0);

            double vwap = vwapService.calculateVWAP("TEST_PRODUCT", 5);

            vwapService.updateVwapTable("TEST_PRODUCT", vwap);

            vwapService.compareVwapFairValue("TEST_PRODUCT");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}