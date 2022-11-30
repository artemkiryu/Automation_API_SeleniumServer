package com.certain.standard.api;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.pojo.TestCaseData;
import internal.qaauto.certain.platform.pojo.locations.LocationsObject;
import internal.qaauto.certain.platform.services.LocationManagement;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("all")
public class Locations extends CertainAPIBase {

    private TestDataLoad testDataLoad = new TestDataLoad();
    private List<LocationsObject> locationObjects = new ArrayList<>();
    private LocationManagement locationManagement = new LocationManagement();
    private LocationsObject locationsObject = new LocationsObject();
    private String locationCode, locationName;
    private String accountCode;
    private String eventCode;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        try {
            locationObjects = testDataLoad.getUCLocationObjData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true, groups = {"postLocations", "UC", "Locations"})
    public void testPostLocation() throws Exception {
        locationsObject = locationObjects.get(0);
        locationName = locationsObject.getLocationName();
        locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"putLocations", "UC", "Locations"}, dependsOnGroups = {"postLocations"})
    public void testPutLocation() throws Exception {
        locationsObject = locationObjects.get(1);
        locationsObject.setLocationCode(locationCode);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location updated successfully");
        TestCase testCase = locationManagement.putLocation(accountCode, eventCode, locationCode, locationsObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-locations", parallel = false)
    public Object[][] getLocations() throws Exception {
        Object obj = locationManagement.getLocationFilters(locationObjects.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-locations", groups = {"getLocations", "UC", "Locations"}, dependsOnGroups = {"postLocations"})
    public void getLocationsWithSearchFilter(String filter, String value) throws Exception {
        TestCase testCase = locationManagement.getLocations(accountCode, eventCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No locations found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-locations-neg-dp1", parallel = false)
    public Object[][] getNegSessionFilters() throws Exception {
        return this.testDataLoad.getNegativeFiltersData("Locations");
    }

    @Test(dataProvider = "get-locations-neg-dp1", enabled = true, groups = {"getLocations", "UC", "Locations"}, dependsOnGroups = {"postLocations"})
    public void getLocationsWithNegativeSearchFilter(String filter, String value, String statusCode, String expectedMessage) throws Exception {
        TestCase testCase = locationManagement.getLocations(accountCode, eventCode, filter, value, 0, 0);
        if (testCase.getStatusCode() == Integer.valueOf(statusCode)) {
            if (testCase.getPayload().contains(expectedMessage)) {
                Assert.assertTrue(true);
            } else if (testCase.getStatusCode() == 404) {
                Reporter.log("HTTP 404 No locations found matching criteria", true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase.getMessage());
        }
    }

    @DataProvider(name = "get-filters-dp1")
    public Object[][] getLocationFilters() throws Exception {
        Object obj = locationManagement.getLocationFilters(locationsObject);
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 0);
    }

    @Test(dataProvider = "get-filters-dp1", groups = {"getLocations", "UC", "Locations"}, dependsOnGroups = {"postLocations"})
    public void getLocationsWithMultipleSearchFilter(HashMap<String, Object> f) throws Exception {
        TestCase testCase = locationManagement.getLocations(accountCode, eventCode, f, null, 1, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No locations found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 3, groups = {"deleteLocations", "UC", "Locations"}, dependsOnGroups = {"postLocations"})
    public void testDELETELocation() throws Exception {
        TestCase testCase = locationManagement.deleteLocation(accountCode, eventCode, locationCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //Negative tests
    @DataProvider(name = "post-locations-dp1", parallel = false)
    public Object[][] postLocationNegative() throws Exception {
        List<TestCaseData> testCaseDataList = testDataLoad.getLocationData();
        int startRow = 2;
        Object[][] objects = new Object[testCaseDataList.size() - startRow][4];
        int index = 0;
        for (int i = startRow; i < testCaseDataList.size(); i++) {
            objects[index][0] = testCaseDataList.get(i).getObject();
            objects[index][1] = testCaseDataList.get(i).getDescription();
            objects[index][2] = testCaseDataList.get(i).getStatusCode();
            objects[index][3] = testCaseDataList.get(i).getExpectedMessage();
            index++;
        }
        return objects;
    }

    @Test(dataProvider = "post-locations-dp1", enabled = true, groups = {"postLocationNeg", "UC", "Locations"})
    public void testPOSTLocationNegative(LocationsObject locationsObject, String description, int statusCode, String expectedMessage) throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(statusCode, expectedMessage);
        Reporter.log("Test-Scenario: " + description, true);
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}


