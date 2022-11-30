package com.certain.External.service.v1;

import com.certain.external.dto.supplier.SupplierObj;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.SupplierObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class Supplier extends CertainAPIBase {

    final SupplierObjSvc supplierObjSvc = new SupplierObjSvc();
    final String[] searchFilters = {"dateModified_after", "dateModified_before", "dateCreated_after", "dateCreated_before", "supplierCode", "isActive"};
    final String supplierCode = SUPPLIER_CODE;
    String eventCode;
    String accountCode;
    private TestDataLoad testDataLoad = new TestDataLoad();
    private List<SupplierObj> supplierObjects = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;

        try {
            supplierObjects = testDataLoad.getSupplierObjData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-supplierObj", "Supplier"})
    public void testGETSuppliersForEvent() throws Exception {
        TestCase testCase = supplierObjSvc.getSuppliers(accountCode, eventCode, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No suppliers found for the matching criteria...", true);
            Assert.assertTrue(true);

        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(enabled = true, groups = {"get-supplierObj", "Supplier"})
    public void testGETSuppliersForEventUsingMaxResults() throws Exception {
        TestCase testCase = supplierObjSvc.getSuppliers(accountCode, eventCode, 1, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);

        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No suppliers found for the matching criteria...", true);
            Assert.assertTrue(true);

        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(enabled = true, groups = {"get-supplierObj", "Supplier"})
    public void testGETSuppliersForEventUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = supplierObjSvc.getSuppliers(accountCode, eventCode, 2, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No suppliers found for the matching criteria...", true);
            Assert.assertTrue(true);

        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.DataProvider(name = "get-filters-dp1")
    public Object[][] getSuppliers() throws Exception {
        Object obj = testDataLoad.getSupplierObjFilters(supplierObjects.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @org.testng.annotations.Test(dataProvider = "get-filters-dp1", enabled = true, groups = {"get-supplierObj", "Supplier"})
    public void testGETSuppliersForEventUsingSearchFilter(String filterBy, Object value) throws Exception {
        TestCase testCase = supplierObjSvc.getSuppliers(accountCode, eventCode, filterBy, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No suppliers found for the matching criteria...", true);
            Assert.assertTrue(true);

        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.DataProvider(name = "get-filters-dp2")
    public Object[][] getSuppliersMultipleFilters() throws Exception {
        Object obj = testDataLoad.getSupplierObjFilters(supplierObjects.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 0);
    }

    @org.testng.annotations.Test(dataProvider = "get-filters-dp2", enabled = true, groups = {"get-supplierObj", "Supplier"})
    public void testGETSuppliersForEventUsingMultipleSearchFilter(HashMap<String, Object> multipleFilters) throws Exception {
        TestCase testCase = supplierObjSvc.getSuppliers(accountCode, eventCode, multipleFilters, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No suppliers found for the matching search criteria...", true);
            Assert.assertTrue(true);

        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-supplierObj", "Supplier"})
    public void testGETSuppliersForEventOrderBy() throws Exception {
        TestCase testCase = supplierObjSvc.getSuppliers(accountCode, eventCode, "dateCreated_asc", 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-supplierObj", "Supplier"})
    public void testGETSuppliersForEventOrderByMultiple() throws Exception {
        TestCase testCase = supplierObjSvc.getSuppliers(accountCode, eventCode, "supplierCode_asc", 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-supplierObj", "Supplier"})
    public void testGETSuppliersForSupplierCode() throws Exception {
        TestCase testCase = supplierObjSvc.getSuppliers(accountCode, eventCode, supplierCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}
