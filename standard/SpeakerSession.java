package com.certain.standard.api;


import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.pojo.sessions.SessionObject;
import internal.qaauto.certain.platform.pojo.speakers.SpeakersObject;
import internal.qaauto.certain.platform.services.SessionManagement;
import internal.qaauto.certain.platform.services.SpeakerManagement;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skumar on 8/17/2015.
 */
@SuppressWarnings("all")
public class SpeakerSession extends CertainAPIBase {

    private final TestDataLoad testDataLoad = new TestDataLoad();
    private SpeakerManagement speakerManagement = new SpeakerManagement();
    private List<SpeakersObject> speakersObjects = new ArrayList<>();
    private String firstName, speakerPin, speakerEmail;
    private String speakerId;
    private SpeakersObject speakersObject = new SpeakersObject();
    private SessionManagement sessionManagement = new SessionManagement();
    private List<SessionObject> sessionObjects = new ArrayList<>();
    private String sessionCode, sessionName;
    private int sessionInstanceId;
    private String accountCode;
    private String eventCode;
    private String sessionTrack = TRACK_NAME;

    @Override
    public void loadData() {
        super.loadData();
        accountCode = ACCOUNT_CODE;
        eventCode = USER_EVENT_CODE;

    }
    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        try {
            speakersObjects = testDataLoad.getUCSpeakersObjData();
            sessionObjects = testDataLoad.getUCSessionsObjData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true, groups = "postSpeaker-ss")
    public void testPOSTSpeaker() throws Exception {
        String uniqueCode = randomString(12);
        speakersObject = speakersObjects.get(0);
        speakerEmail = uniqueCode + "@gmail.com";
        speakersObject.setEmail(speakerEmail);
        speakersObject.setPin(speakersObject.getPin() + randomNumber(3));
        speakersObject.setFirstName("Speaker " + uniqueCode);
        speakerPin = speakersObject.getPin();
        firstName = speakersObject.getFirstName();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Speaker " + speakerEmail + " added to event successfully");
        TestCase testCase = speakerManagement.postSpeaker(accountCode, eventCode, speakersObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            speakerId = String.valueOf(testCase.getSpeakerId());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postSession-ss", "UC", "SpeakerSession"})
    public void testPOSTSessionInstance() throws Exception {
        String uniqueCode = randomString(12);
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("Session " + uniqueCode);
        sessionObject.setNoOfInstances("1");
        sessionName = sessionObject.getName();
        sessionCode = sessionObject.getSessionCode();
        sessionObject.setEventTrack(sessionTrack);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceId = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postSpeakerSession", "UC", "SpeakerSession"}, dependsOnGroups = {"postSpeaker-ss", "postSession-ss"})
    public void testPOSTSpeakerSession() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Speaker " + speakerEmail + " has been assigned  to session " + sessionName + " successfully");
        TestCase testCase = speakerManagement.postSpeakerSession(accountCode, eventCode, sessionCode, speakerId, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postSpeakerSessionInv1", "UC", "SpeakerSession"}, priority = 1, dependsOnGroups = {"postSpeakerSession"})
    public void testPOSTAssignSpeakerWhoIsAlreadyAssignedToTheSession() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "Speaker " + speakerEmail + " is already assigned to given session");
        TestCase testCase = speakerManagement.postSpeakerSession(accountCode, eventCode, sessionCode, speakerId, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteSpeakerSession", "UC", "SpeakerSession"}, priority = 2, dependsOnGroups = {"postSpeakerSession"})
    public void testDELETESpeakerSessions() throws Exception {
        TestCase testCase = speakerManagement.deleteSpeakerSession(accountCode, eventCode, sessionCode, speakerId);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteSpeaker-ss", "UC", "SpeakerSession"}, priority = 3, dependsOnGroups = {"deleteSpeakerSession"})
    public void testDELETESpeaker() throws Exception {
        TestCase testCase = speakerManagement.deleteSpeaker(accountCode, eventCode, speakerId);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(true);
    }

    @Test(enabled = true, groups = {"postSpeakerSessionInv2", "UC", "SpeakerSession"}, priority = 4, dependsOnGroups = {"deleteSpeakerSession", "deleteSpeaker-ss"})
    public void testPOSTDeletedSpeakerToActiveSession() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "Invalid Speaker id");
        TestCase testCase = speakerManagement.postSpeakerSession(accountCode, eventCode, sessionCode, speakerId, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postSpeakerSessionInv3", "UC", "SpeakerSession"}, priority = 5, dependsOnGroups = {"postSession-ss"})
    public void testPOSTInvalidSpeakerToActiveSession() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "Invalid Speaker id.Please provide valid speaker id");
        TestCase testCase = speakerManagement.postSpeakerSession(accountCode, eventCode, sessionCode, "109747183", expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteSession-ss", "UC", "SpeakerSession"}, priority = 6, dependsOnGroups = "postSpeakerSessionInv3")
    public void testDELETESession() {
        TestCase testCase = sessionManagement.deleteSession(accountCode, eventCode, sessionCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}
