package com.certain.External.service.v1;

import com.certain.external.dto.registration.RegistrationObj;
import com.certain.external.dto.rotation.RegistrationRotationObj;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class RegistrationRotation extends CertainAPIBase {

    private List<RegistrationRotationObj> rotationObjList;
    private TestDataLoad testDataLoad = new TestDataLoad();
    private RegistrationObjSvc registrationObjSvc = new RegistrationObjSvc();
    private List<RegistrationObj> registrationList = new ArrayList<>();
    private String rotationName = ROTATION_NAME;
    private String accountCode;
    private String eventCode;
    private String registrationCode;

    @org.testng.annotations.BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        try {
            rotationObjList = testDataLoad.getRegistrationRotationObjData();
            registrationList = testDataLoad.getRegistrationObjData(accountCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.testng.annotations.Test(enabled = true, groups = {"postReg4Rotation", "RegistrationRotation"})
    public void testPOSTRegistrationRequired() throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.setAttendeeTypeCode("Attendee");
        registrationObj.setRegistrationStatusLabel("New");
        registrationObj.getProfile().setFirstName("Reg " + randomNumber(5));
        registrationObj.getProfile().setLastName("ForRotation " + randomNumber(2));
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.postRegistration(accountCode, eventCode, registrationObj, expectedCondition, true);
        if (testCase.isPassed()) {
            registrationCode = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + registrationCode, true);

            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"postRegistrationRotation", "RegistrationRotation"}, dependsOnGroups = "postReg4Rotation")
    public void testPOSTRegistrationRotation() throws Exception {
        RegistrationRotationObj registrationRotationObj = rotationObjList.get(0);
        registrationRotationObj.setName(rotationName);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        TestCase testCase = registrationObjSvc.postRegistrationRotation(accountCode, eventCode, registrationCode, registrationRotationObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getRegistrationRotation", "RegistrationRotation"}, priority = 2, dependsOnGroups = "postRegistrationRotation")
    public void testGETRegistrationRotationByRegCode() throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrationRotation(accountCode, eventCode, registrationCode, rotationName);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getRegistrationRotation", "RegistrationRotation"}, priority = 2, dependsOnGroups = "postRegistrationRotation")
    public void testGETRegistrationRotationByEventCode() throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrationRotation(accountCode, eventCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"putRegistrationRotation", "RegistrationRotation"}, priority = 3, dependsOnGroups = "postRegistrationRotation")
    public void testPOSTUpdateRegistrationRotation() throws Exception {
        RegistrationRotationObj registrationRotationObj = rotationObjList.get(1);
        registrationRotationObj.setName(rotationName);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        TestCase testCase = registrationObjSvc.postRegistrationRotation(accountCode, eventCode, registrationCode, registrationRotationObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"deleteRegistrationRotation", "RegistrationRotation"}, priority = 4, dependsOnGroups = "postRegistrationRotation")
    public void testDELETERegistrationRotation() throws Exception {
        RegistrationRotationObj registrationRotationObj = rotationObjList.get(0);
        registrationRotationObj.setName(rotationName);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        TestCase testCase = registrationObjSvc.postRegistrationRotation(accountCode, eventCode, registrationCode, registrationRotationObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"deleteReg4Rotation", "RegistrationRotation"}, priority = 5, dependsOnGroups = {"deleteRegistrationRotation"})
    public void testDELETERegistration() throws Exception {
        TestCase testCase = registrationObjSvc.deleteRegistration(accountCode, eventCode, registrationCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}
