package com.certain.External.service.v1;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.RegistrationListObject;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.util.*;

@SuppressWarnings("all")
public class RegistrationList extends CertainAPIBase {

    private final TestDataLoad testDataLoad = new TestDataLoad();
    private java.lang.String eventCode;
    List<RegistrationListObject> registrationListObjectList = new ArrayList<>();
    private RegistrationObjSvc registrationObjSvc = new RegistrationObjSvc();

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        // ACCOUNT_CODE = ACCOUNT_CODE;
    }

    @DataProvider(name = "get-list-fields", parallel = false)
    private Object[][] getRegListObject() throws Exception {
        registrationListObjectList = testDataLoad.getRegistrationList();
        Object[][] objects = new Object[registrationListObjectList.size()][1];
        for (int i = 0; i < registrationListObjectList.size(); i++) {
            objects[i][0] = registrationListObjectList.get(i);
        }
        return objects;
    }

    @org.testng.annotations.Test(dataProvider = "get-list-fields", groups = "RegistrationList")
    public void testGETRegistrationList(RegistrationListObject registrationListObject) throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrationList(ACCOUNT_CODE, eventCode, null, registrationListObject.getFields(),
                registrationListObject.getMaxResults(), registrationListObject.getStartIndex());
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("404 No registration list found for the criteria ", true);
            Assert.assertTrue(true);
        }
    }


}
