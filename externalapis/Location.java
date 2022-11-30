package com.certain.External.service.v1;

import com.certain.external.dto.location.LocationObj;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.LocationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

/**
 * Created by skumar on 7/28/2015.
 */

@SuppressWarnings("all")
public class Location extends CertainAPIBase {

    java.util.List<com.certain.external.dto.location.LocationObj> locationObjList;
    String locationCode, locationCodeReq;
    private LocationObjSvc locationServices = new LocationObjSvc();
    private TestDataLoad testDataLoad = new TestDataLoad();
    private String accountCode;

    @org.testng.annotations.BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        loadData();
        accountCode = ACCOUNT_CODE;
        TestDataLoad testDataLoad = new TestDataLoad();
        locationObjList = testDataLoad.getLocationObjData();
    }

    @org.testng.annotations.Test(priority = 1, enabled = true, groups = {"post-location-obj-required", "Location"})
    public void testPOSTLocationRequiredFields() throws Exception {
        com.certain.external.dto.location.LocationObj locationObj = locationObjList.get(5);
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.createLocation(ACCOUNT_CODE, locationObj, expectedCondition, true);
        locationCodeReq = locationObj.getLocationCode();
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(priority = 1, enabled = true, groups = {"post-location-obj", "Location"})
    public void testPOSTLocation() throws Exception {
        for (int i = 0; i < 4; i++) {
            com.certain.external.dto.location.LocationObj locationObj = locationObjList.get(i);
            ExpectedCondition expectedCondition = new ExpectedCondition();
            expectedCondition.setStatusCode(200);
            TestCase testCase = locationServices.createLocation(ACCOUNT_CODE, locationObj, expectedCondition, false);
            locationCode = locationObj.getLocationCode();
            if (testCase.isPassed()) {
                Reporter.log(testCase.getMessage(), true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase.getMessage());
        }
    }


    @org.testng.annotations.Test(priority = 2, enabled = true, groups = {"get-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testGETLocationByCode() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.getLocation(ACCOUNT_CODE, locationCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(priority = 2, enabled = true, groups = {"get-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testGETAllLocations() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.getLocation(ACCOUNT_CODE, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(priority = 2, enabled = true, groups = {"get-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testGETAllLocationListOrderByDateCreated() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.getLocation(ACCOUNT_CODE, "dateCreated_asc", 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(priority = 2, enabled = true, groups = {"get-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testGETLocationListMaxResults() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.getLocation(ACCOUNT_CODE, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(priority = 2, enabled = true, groups = {"get-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testGETLocationListMaxResultsAndStartIndex() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        int size = locationServices.getLocationCollectionSize(ACCOUNT_CODE);
        TestCase testCase = locationServices.getLocation(ACCOUNT_CODE, size / 2, 2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-location-filters", parallel = false)
    public Object[][] getLocations() throws Exception {
        Object obj = testDataLoad.getLocationObjFilters(locationObjList.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @org.testng.annotations.Test(dataProvider = "get-location-filters", priority = 2, enabled = true, groups = {"get-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testGETLocationListWithFilter(String filter, String value) throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.getLocation(ACCOUNT_CODE, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No locations found for the matching criteria..", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @DataProvider(name = "get-locations-multi-filters")
    public Object[][] getLocationMultipleFilters() throws Exception {
        Object obj = testDataLoad.getLocationObjFilters(locationObjList.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @org.testng.annotations.Test(dataProvider = "get-locations-multi-filters", priority = 2, enabled = true, groups = {"get-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testGETLocationListWithMultipleSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.getLocation(ACCOUNT_CODE, multipleFilters, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No locations found for the matching criteria..", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(priority = 2, enabled = true, groups = {"get-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testGETLocationListOrderByWithMaxResults() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.getLocation(ACCOUNT_CODE, "dateCreated_asc", 4, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(priority = 3, enabled = true, groups = {"delete-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testPOSTUpdateLocation() throws Exception {
        LocationObj locationObj = locationObjList.get(5);
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.updateLocation(ACCOUNT_CODE, locationCode, locationObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(priority = 2, enabled = false, groups = {"delete-location-obj", "Location"}, dependsOnGroups = "post-location-obj")
    public void testDELETELocation() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationServices.deleteLocation(ACCOUNT_CODE, locationCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}

