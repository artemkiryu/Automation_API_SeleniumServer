package com.certain.External.service.v1;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class Appointments extends CertainAPIBase {

    private final TestDataLoad testDataLoad = new TestDataLoad();
    private final RegistrationObjSvc registrationObjSvc = new RegistrationObjSvc();
    private final Map<String, String> filterData = new HashMap<>();
    private String registrationCode;
    String eventCode, accountCode;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        accountCode = ACCOUNT_CODE;
        eventCode = USER_EVENT_CODE;
    }
    @DataProvider(name = "get-appointments")
    public Object[][] getAppointments() throws Exception {
        return testDataLoad.getKeyValuePairFromObject(filterData);
    }

    @Test(description = "get appointments by event", groups = {"get-appointments", "Appointments"})
    public void testGETAllAppointmentsByEventCode() throws Exception {
        TestCase testCase = registrationObjSvc.getAppointments(accountCode, eventCode, null, null, 0, 0);
        if (testCase.isPassed()) {
            List<HashMap<String, Object>> appointmentList = jsonHelper.getList(testCase.getPayload(), "appointments");
            if (appointmentList.get(0).get("reason") == null || "".equals(appointmentList.get(0).get("reason"))) {
                filterData.put("reason", null);
            } else {
                filterData.put("reason", appointmentList.get(0).get("reason").toString());
            }
            HashMap<String, Object> registration = (HashMap) appointmentList.get(0).get("registration");
            registrationCode = registration.get("regCode").toString();
            filterData.put("firstName", registration.get("name").toString().split(" ")[0]);
            filterData.put("appointmentType", appointmentList.get(0).get("appointmentType").toString());
            filterData.put("appointmentSource", appointmentList.get(0).get("appointmentSource").toString());
            filterData.put("startDate", appointmentList.get(0).get("startDate").toString());
            filterData.put("organization", registration.get("organization").toString());
            filterData.put("attendeeType", registration.get("attendeeType").toString());
            filterData.put("registrationCode", registration.get("regCode").toString());
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true, "filtre data matched " + filterData);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(description = "get appointments by reg code", groups = {"get-appointments2", "Appointments"}, dependsOnGroups = "get-appointments")
    public void testGETRegistrationAppointments() throws Exception {
        TestCase testCase = registrationObjSvc.getAppointments(accountCode, eventCode, registrationCode, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(description = "get appointments by event", groups = {"get-appointments2", "Appointments"})
    public void testGETAppointmentsByEventCodeWithMaxResultsAndStartIndex() throws Exception {
        TestCase testCase = registrationObjSvc.getAppointments(accountCode, eventCode, null, null, 1, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(dataProvider = "get-appointments", description = "get appointments by event", groups = {"get-appointments-filter", "Appointments"}, priority = 2, dependsOnGroups = "get-appointments")
    public void testGETAppointmentsByEventWithSearchFilters(String k, String v) throws Exception {
        TestCase testCase = registrationObjSvc.getAppointments(accountCode, eventCode, null, k, v, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(dataProvider = "get-appointments", enabled = false, description = "get appointments by event", groups = {"get-appointments-filter", "Appointments"}, priority = 2, dependsOnGroups = "get-appointments")
    public void testGETRegistrationAppointmentsWithSearchFilters(String k, String v) throws Exception {
        TestCase testCase = registrationObjSvc.getAppointments(accountCode, eventCode, registrationCode, k, v, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }
}

