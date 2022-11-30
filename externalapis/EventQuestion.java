package com.certain.External.service.v1;

import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.EventObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

@SuppressWarnings("all")
public class EventQuestion extends CertainAPIBase {

    private final EventObjSvc eventObjSvc = new EventObjSvc();
    private String eventCode;
    private final String[] questionTypes = {"Text", "Textarea", "Radio", "Checkbox", "Select", "Select multiple", "Ticket",
            "Date", "Time", "Date-Time", "Integer", "Number", "File", "Image"};

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
    }

//    @DataProvider(name="get-events-includeList",parallel = false)
//    public Object[][] getEventsIncludeList() throws Exception{
//        Object[][] includeList = new Object[6][1];
//        includeList[0][0] = "answers";
//        includeList[1][0] = "groups";
//        includeList[2][0] = "rotations";
//        includeList[3][0] = "question_assignments";
//        includeList[4][0] = "forms";
//        includeList[5][0] = "websites";
//        return includeList;
//    }

//    @Test(dataProvider = "get-events-includeList", enabled = true, description = "get account details by event code", groups = {"get-event-includeList", "EventQuestion"},
//            priority = 2)
//    public void testGET_EventDetailsByEventCodeIncludeList(String includeList) throws Exception {
//        Reporter.log("Getting Event Details by eventCode = " + this.eventCode + " with includeList =" + includeList);
//        RequestSpecDataType request = new RequestSpecDataType();
//        request.setBaseUri(super.SERVER_HOST);
//        request.setBasePath(super.BASE_PATH);
//        request.setAuthenticationScheme(authenticationScheme);
//        request.setContentType(super.DEFAULT_CONTENT_TYPE);
//        request.setCollectionResource("Event");
//        request.setAccountCode(ACCOUNT_CODE);
//        request.setEventCode(this.eventCode);
//        request.addQueryParameters("includeList", includeList);
//        RequestSpecification requestSpec = new RequestSpecification(request);
//        requestSpec.buildV1();
//        Response response = super.restAssuredClient.GET(requestSpec);
//        java.lang.String __jsonResponseMessage = response.asString();
//
//        String __path = includeList;
//        if (response.getStatusCode() == 200) {
//
//            switch (includeList) {
//                case "question_assignments":
//                    __path = "questionAssignments.questionAssignment";
//                    break;
//                case "answers":
//                    __path = "eventQuestions.question";
//                    break;
//                case "groups":
//                    __path = "groups.group";
//                    break;
//                case "websites":
//                    __path = "websites.website";
//                    break;
//                case "forms":
//                    __path = "forms.form";
//                    break;
//                case "rotations":
//                    __path = "rotations.rotation";
//                    break;
//            }
//            ArrayList __arrayResults = jsonHelper.getJsonArray(__jsonResponseMessage,__path);
//            Reporter.log("Event has total " + __arrayResults.size() + " " + includeList, true);
//
//            if(__arrayResults.size()>0){
//                if(includeList.equals("answers")) {
//                    for (HashMap<String, Object> map : (List<HashMap<String, Object>>) __arrayResults) {
//                        if (map.containsKey("answers")) {
//                            Reporter.log("Answers retrieved for the event questions " + map.get("answers"));
//                            Assert.assertTrue(true);
//                        }else
//                            Assert.assertTrue(false,"Answers not found");
//                    }
//                }
//                Reporter.log("Event details retrieved successfully \n" + includeList + " " + __arrayResults.toString());
//                Assert.assertTrue(true);
//
//            } else {
//                Assert.assertTrue(false, "Event details retrieved but found " + includeList + " returned as 'null'....please verify the response ");
//            }
//        }else if(response.getStatusCode() == 404) {
//            Reporter.log("No groups found for the event code " + this.eventCode);
//            Assert.assertTrue(true);
//        }else {
//            Assert.assertTrue(false, response.getStatusLine() + " Failed to get Event Details by eventCode = " + this.eventCode + " with includeList " + includeList);
//        }
//    }

    @DataProvider(name = "get-questions-dp1")
    public Object[][] getEventQuestionsDP() {
        Object[][] questionsFilter = new Object[questionTypes.length][2];
        int index = 0;
        for (String questionType : questionTypes) {
            questionsFilter[index][0] = "questionType";
            questionsFilter[index][1] = questionType;
            index++;
        }
        return questionsFilter;
    }

    @org.testng.annotations.Test(groups = "EventQuestions")
    public void testGETEventQuestionsForAccount() throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, null, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //[SCM-25564]
    @org.testng.annotations.Test(groups = "EventQuestions")
    public void testGETEventQuestionsForAccountUsingMaxResults() throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, null, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //[SCM-25564]
    @org.testng.annotations.Test(groups = "EventQuestions")
    public void testGETEventQuestionsForAccountUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, null, null, null, 5, 2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-questions-dp1", groups = "EventQuestions")
    public void testGETEventQuestionsForAccountWithSearchFilter(String filter, String value) throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, null, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-questions-dp1", groups = "EventQuestions")
    public void testGETEventQuestionsForAccountWithSearchFilterWithMaxResults(String filter, String value) throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, null, filter, value, 9, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = "EventQuestions")
    public void testGETEventQuestionsForEvent() throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, eventCode, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //[SCM-25564]
    @org.testng.annotations.Test(groups = "EventQuestions")
    public void testGETEventQuestionsForEventUsingMaxResults() throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, eventCode, null, null, 4, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //[SCM-25564]
    @org.testng.annotations.Test(groups = "EventQuestions")
    public void testGETEventQuestionsForEventUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, eventCode, null, null, 4, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-questions-dp1", groups = "EventQuestions")
    public void testGETEventQuestionsForEventWithSearchFilter(String filter, String value) throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, null, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-questions-dp1", groups = "EventQuestions")
    public void testGETEventQuestionsForEventWithSearchFilterUsingMaxResults(String filter, String value) throws Exception {
        TestCase testCase = eventObjSvc.getEventQuestions(ACCOUNT_CODE, null, filter, value, 3, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


}
