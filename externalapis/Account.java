package com.certain.External.service.v1;


import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.AccountObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

@SuppressWarnings("all")
public class Account extends CertainAPIBase {

    private final TestDataLoad testDataLoad = new TestDataLoad();
    private final AccountObjSvc accountObjSvc = new AccountObjSvc();
    private final com.certain.external.dto.account.AccountObj accountObj = new com.certain.external.dto.account.AccountObj();
    private final String[] includeList = {"sub_account"};
    private String accountCode;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        if (USERNAME.equals("system")) {
            accountCode = "register123";
        } else {
            accountCode = ACCOUNT_CODE;
        }
        try {
            accountObj.setAccountCode(accountCode);
            accountObj.setIsActive(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DataProvider(name = "get-accounts")
    public Object[][] getAccountsDP() throws Exception {
        Object obj = testDataLoad.getAccountFilters(accountObj);
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    //Reference: SCM-24765

    @org.testng.annotations.Test(groups = "Account")
    public void testGETSpecificAccountDetails() throws Exception {
        TestCase testCase = accountObjSvc.getAccount(accountCode, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Account")
    public void testGETSpecificAccountUsingMaxResults() throws Exception {
        TestCase testCase = accountObjSvc.getAccount(accountCode, null, null, 1, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //under devlopment
    @DataProvider(name = "get-account-includeList")
    public Object[][] getEventIncludeList() throws Exception {
        Object[][] includeLists = new Object[includeList.length][1];
        int i = 0;
        for (String item : includeList) {
            includeLists[i][0] = item;
            i++;
        }
        return includeLists;
    }

    @org.testng.annotations.Test(dataProvider = "get-account-includeList", groups = "Account")
    public void testGETSubAccountList(String includeList) throws Exception {
        TestCase testCase = accountObjSvc.getAccount(accountCode, new String[]{includeList}, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-account-includeList", groups = "Account")
    public void testGETSubAccountListUsingMaxResults(String includeList) throws Exception {
        TestCase testCase = accountObjSvc.getAccount(accountCode, new String[]{includeList}, 1, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = "Account")
    public void testGETAccountsUsingMaxResults() throws Exception {
        TestCase testCase = accountObjSvc.getAccount(null, null, null, 1, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Account")
    public void testGETAccountsUsingStartIndex() throws Exception {
        TestCase testCase = accountObjSvc.getAccount(accountCode, null, null, 0, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = "Account")
    public void testGETAccountsUsingMaxResultsAndStartIndex() throws Exception {
        TestCase testCase = accountObjSvc.getAccount(accountCode, null, null, 1, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

   /* @org.testng.annotations.Test(dataProvider = "get-accounts", groups = "Account")
    public void testGETAccountsUsingSearchFilter(String filter, String value) throws Exception{
        TestCase testCase = accountObjSvc.getAccount(ACCOUNT_CODE,filter,value,null,0,0);
        if(testCase.isPassed()){
            Reporter.log(testCase.getMessage(),true);
            Assert.assertTrue(true);
        }else Assert.assertTrue(false,testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = "Account")
    public void testGETAccountsOrderBy() throws Exception{
        TestCase testCase = accountObjSvc.getAccount(null,null,"accountdateModified_asc",0,0);
        if(testCase.isPassed()){
            Reporter.log(testCase.getMessage(),true);
            Assert.assertTrue(true);
        }else Assert.assertTrue(false,testCase.getMessage());
    }

    @DataProvider(name="get-filters-dp1")
    public Object[][] getAccountAPIFilters() throws Exception{
        Object obj = testDataLoad.getAccountFilters(accountObj);
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj,0);
    }

    @org.testng.annotations.Test(dataProvider = "get-filters-dp1")
    public void testGETAccountUsingMultipleSearchFilters(HashMap<String,Object> f) throws Exception{
        TestCase testCase = accountObjSvc.getAccount(null, f, null,0,0);
        if(testCase.isPassed()) {
            Reporter.log(testCase.getMessage(),true);
            Assert.assertTrue(true);
        }else Assert.assertTrue(false,testCase.getMessage());
    }*/

}
