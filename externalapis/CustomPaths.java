package com.certain.External.service.v1;

import internal.qaauto.certain.platform.dataprovider.XLSTestCaseData;
import internal.qaauto.certain.platform.pojo.AuthenticationScheme;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skumar on 10/21/2015.
 */
public class CustomPaths extends CertainAPIBase {

    private final AuthenticationScheme auth = new AuthenticationScheme();
    private final String endPoint = "https://stagingd.certain.com";
    private List<HashMap<String, String>> testDataList = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        auth.setUsername(USERNAME);
        auth.setPassword(PASSWORD);
        try {
            testDataList = new XLSTestCaseData().getTestCaseFromXLSheet("CustomURL");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DataProvider(name = "custom_path")
    public Object[][] customPaths() {
        Object[][] customData = new Object[testDataList.size()][1];
        int i = 0;
        for (Map<String, String> testData : testDataList) {
            String accountCode = "env";
            String path = testData.get("urlPath").replace("{ACCOUNT_CODE}", accountCode);
            testData.put("urlPath", path);
            customData[i][0] = testData;
            i++;
        }
        return customData;
    }

    @Test(priority = 1, dataProvider = "custom_path", groups = {"Custom-JSON", "Custom"}, singleThreaded = true)
    public void testGET_CustomURLRequestsJSON(Map<String, String> customUrlData) throws Exception {
        String urlPath = this.endPoint + customUrlData.get("urlPath");
        String path = customUrlData.get("jsonArrayPath");
        Reporter.log("Getting  " + path + " List ");
        String responseType = "application/jsonHelper";
        Response response = super.restAssuredClient.GET(urlPath, this.auth, responseType);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == Integer.parseInt(customUrlData.get("statusCode"))) {
            Reporter.log("Get was successful and result set of " + path + " is [" + jsonHelper.getJsonValueByKey(__jsonResponseMessage, "size") + "]");
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, response.getStatusLine() + " Get Failed ");
    }

    @Test(priority = 2, dataProvider = "custom_path", groups = {"Custom-XML", "Custom"}, singleThreaded = true)
    public void testGET_CustomURLRequestsXML(Map<String, String> customUrlData) throws Exception {
        String urlPath = this.endPoint + customUrlData.get("urlPath");
        String path = customUrlData.get("jsonArrayPath");
        Reporter.log("Getting  " + path + " List ");
        Response response = super.restAssuredClient.GET(urlPath, this.auth, "application/xml");
        Reporter.log("Status: " + response.getStatusLine(), true);
        if (response.getStatusCode() == Integer.parseInt(customUrlData.get("statusCode"))) {
            Reporter.log("Get was successful and the results set of " + path + " is [" + response.getBody().xmlPath().setRoot("collection").getInt("size") + "]");
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, response.getStatusLine() + " Failed ");
    }

}
