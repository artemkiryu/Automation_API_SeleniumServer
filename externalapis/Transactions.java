package com.certain.External.service.v1;

import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.util.Date;

@SuppressWarnings("all")
public class Transactions extends CertainAPIBase {

    private final RegistrationObjSvc registrationObjectSvc = new RegistrationObjSvc();
    private final String regCode = "32278-1879686-5669";
    private String eventCode;
    private String accountCode;
    // --Commented out by Inspection (4/6/2016 1:56 PM):HashMap<String,Object> filters = new HashMap<>();

    /**
     * **********************************************************************************************************************************
     * IMPORTANT : Transactions must be present for the eventCode provided as run time parameter else these tests will fail
     * ***********************************************************************************************************************************
     */

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
    }
    @org.testng.annotations.Test(groups = "Transactions")
    public void testGETTransactionsForAccountCode() throws Exception {
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No transactions found for the accountCode " + accountCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Transactions")
    public void testGETTransactionsForAccountCodeUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, null, null, 2, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No transactions found for the accountCode " + accountCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Transactions")
    public void testGETTransactionsForAccountCodeUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, null, null, 2, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No transactions found for the accountCode " + accountCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Transactions")
    public void testGETTransactionsForEvent() throws Exception {
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, USER_EVENT_CODE, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No transactions found for the event code " + USER_EVENT_CODE, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Transactions")
    public void testGETTransactionsForEventUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, USER_EVENT_CODE, null, 3, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No transactions found for the event code " + USER_EVENT_CODE, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Transactions")
    public void testGETTransactionsForEventUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, USER_EVENT_CODE, null, 3, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No transactions found for the event code " + USER_EVENT_CODE, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Transactions")
    public void testGETTransactionsForRegistration() throws Exception {
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, USER_EVENT_CODE, regCode, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No transactions found for the registration code " + regCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Transactions")
    public void testGETTransactionsForRegistrationUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, USER_EVENT_CODE, regCode, 3, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No transactions found for the registration code " + regCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(groups = "Transactions")
    public void testGETTransactionsForRegistrationUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, USER_EVENT_CODE, regCode, 3, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No transactions found for the registration code " + regCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "transactions-filters")
    private Object[][] filtersDP1() throws Exception {
        Object[][] objects = new Object[12][2];
        objects[0][0] = "isCompleted";
        objects[0][1] = true;
        objects[1][0] = "isActive";
        objects[1][1] = true;
        objects[2][0] = "dateCreated_before";
        objects[2][1] = du.setForwardDate(new Date(), 30);
        objects[3][0] = "dateCreated_after";
        objects[3][1] = du.setBehindDate(new Date(), 180);
        objects[4][0] = "dateModified_before";
        objects[4][1] = du.setForwardDate(new Date(), 30);
        objects[5][0] = "dateModified_after";
        objects[5][1] = du.setBehindDate(new Date(), 180);
        objects[6][0] = "transactionType";
        objects[6][1] = "CA";
        objects[7][0] = "isCharge";
        objects[7][1] = false;
        objects[8][0] = "isPayment";
        objects[8][1] = true;
        objects[9][0] = "isReconciled";
        objects[9][1] = false;
        objects[10][0] = "isReceived";
        objects[10][1] = true;
        objects[11][0] = "registrationCode";
        objects[11][1] = USER_EVENT_CODE;
        return objects;
    }

    @org.testng.annotations.Test(dataProvider = "transactions-filters", groups = "Transactions")
    public void testGETTransactionsForAccountCodeWithSearchFilter(String filter, Object value) throws Exception {
        Reporter.log("retrieving transactions for accountCode " + accountCode + " with search filter [" + filter + "=" + value + "]", true);
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, null, null, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No transactions found for the matching criteria [" + filter + "=" + value + "]", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "transactions-filters", groups = "Transactions")
    public void testGETTransactionsForEventCodeWithSearchFilter(String filter, Object value) throws Exception {
        Reporter.log("retrieving transactions for eventCode " + eventCode + " with search filter [" + filter + "=" + value + "]", true);
        TestCase testCase = registrationObjectSvc.getTransactions(accountCode, eventCode, null, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No transactions found for the matching criteria [" + filter + "=" + value + "]", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}
