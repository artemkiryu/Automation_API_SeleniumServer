package certainwebapptests;
import internal.qaauto.certain.platform.constants.AccountSettingsPageConstants;
import internal.qaauto.certain.platform.constants.CommonActionPageConstants;
import internal.qaauto.certain.platform.constants.ResetPasswordFirstLoginPageConstants;
import internal.qaauto.certain.platform.dataload.XLSTestCaseData;
import internal.qaauto.certain.platform.dataprovider.EmailParser;
import internal.qaauto.certain.platform.dataprovider.TestData;
import internal.qaauto.certain.platform.dataprovider.TestNGTestCase;
import internal.qaauto.certain.platform.pojo.*;
import internal.qaauto.certain.platform.tom.*;
import internal.qaauto.certain.platform.utils.ExcelDataPool;
import internal.qaauto.framework.Assert;
import internal.qaauto.framework.ConfigManager;
import internal.qaauto.framework.Verify;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ApiTestDataSprint extends TestNGTestCase {


    XLSTestCaseData testCaseData;
    TestNGTestCase testNGTestCase;
    TestData testData;

    DashboardPage dashboardPage;
    AccountInformation accountInformation;
    UserInformation userInformation;
    LoginPage loginPage;

    String userName;
    String password;

    String accountCode, accountName;
    String eventCode;
    String eventCode1, eventCode2;
    String tags, industries, functions, track;


    /**
     * Set Up method for opening the UI page and setting up the selenium webdriver and Test Data
     * Also set's up the test cases have to run locally or remotely.
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {

        accountCode = ConfigProperties.getAccountCode();
        eventCode = ConfigProperties.getEventCode();
        tags = ConfigProperties.getTags();
        industries = ConfigProperties.getIndustries();
        functions = ConfigProperties.getJobFunctions();
        track = ConfigProperties.getTrackName();
        accountName = ConfigProperties.getAccountName();
        loginPage = new LoginPage();
        loginPage.load();

        testNGTestCase = new TestNGTestCase();
        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "LoginTestData");
        testData = new TestData(testCaseData.getTestCases());

        userName = ConfigProperties.getUserName();
        password = ConfigProperties.getPassword();

        EmailParser emailParser = new EmailParser(ConfigManager.getEmailHostName(), ConfigManager.getEmailUserName(), ConfigManager.getEmailPassword());
        emailParser.connect();
        emailParser.cleanUpUsedMails("Certain Temporary Password notification");
        emailParser.disconnect();

        webDriver.manage().window().maximize();

        reporter.info("**************************************************************");
        reporter.info("Starting Execution of Certain Web App Regression Tests");
        reporter.info("Connecting to : " + loginPage.getPageURL());
        reporter.info("**************************************************************");
    }

    /**
     * After each Test Method this method should be fired for tear Down.
     * This test method shall bring the test case into safe state so that next test case can begin with the safe state.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        reporter.info("Inside Tear Down.");
        CommonActionsPage commonActionsPage;

        if (webDriver.getWindowHandles().size() > 1) {
            webDriver.closePopup();
            reporter.info("Closed Popup");
        }

        commonActionsPage = new CommonActionsPage();
        commonActionsPage.scrollUp();
        try {
            if (commonActionsPage.CANCEL_BUTTON_MODEL.isDisplayed())
                webDriver.clickByJavaScript(commonActionsPage.CANCEL_BUTTON_MODEL);

        } catch (Exception e) {
            reporter.info("Button not found");
        }
        try {
            if (!(commonActionsPage.isElementDisplayedWithoutWait(AccountSettingsPageConstants.SUB_ACCOUNTS_LINK))) {
                webDriver.switchTo().defaultContent();
                commonActionsPage.scrollUp();
                if ((commonActionsPage.isElementDisplayedWithoutWait(CommonActionPageConstants.USER_ACCOUNT_DROPDOWN)))
                    commonActionsPage.clickAccountSettingFromDropDown();
                  //  webDriver.get("https://" + ConfigManager.getHostFromList("website") + "admin_home/");
                else {
                    webDriver.get("https://" + ConfigManager.getHostFromList("website") + "/admin_home/");
                }
            }
        } catch (Exception e) {
            reporter.error("Not able to reach Account Settings Page due to some technical error, Subsequent test cases will not be run.");
        }
        reporter.info("Exiting Tear Down.");
    }

    /**
     * Test method to check login has been done correctly.
     * Assertion to the check the logged in User.
     * <p/>
     * group :  "regression"
     */
    @Test(groups = {"regression", "UAT-SET-1"}, enabled = true, priority = 1, description = "Login into Web App")
    public void testLoginIntoWebApp() throws Exception {

        //login to the application
        dashboardPage = loginPage.login(userName, password);
        // dashboardPage = loginPage.login("H1519903283665@gmail.com", "info1234");
        //assertion of header page after login.
        //Assert.assertElementDisplayed(DashboardPageConstants.DASHBOARD_HEADER);
        Assert.assertTrue(webDriver.getTitle().contains("Dashboard | Certain"), "Page title found", "Page title not found");

        //click on don't show button.
        dashboardPage.clickNotificationPopup();
        dashboardPage.clickDontShowButtonIfPresent();
    }

    /**
     * Test method to create a sub account and user.
     * Assertion to the check sub account has been created successfully.////////
     * Assertion to check the email has been send for the new user created
     * Login with the new user, change Password, and create an event with new user
     * <p/>
     * group : "regression"
     * Jira ID : SQA-5150 Automation SQA Project Prerequisites: create Account, User, Event
     */
    @Test(groups = {"regression", "UAT-SET-1"}, enabled = true, priority = 2, description = "SQA - 5150 Login into Web App and Create a Sub Account, User Account and Enable All Products", dependsOnMethods = "testLoginIntoWebApp")
    public void testCreateSubAccount() throws Exception {
        //click on sub accounts menu item.
        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();


        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CreateEventsData");
        testData = new TestData(testCaseData.getTestCases());
        Event event = testData.getEventData().get(0);
        Event event1 = testData.getEventData().get(1);

        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "EventAttributes");
        testData = new TestData(testCaseData.getTestCases());
        EventAttributes eventAttributes = testData.getEventAttributes().get(0);


        if (accountCode == null || "".equals(accountCode.trim())) {

            SubAccountsPage subAccountsPage = accountSettingsPage.clickSubAccounts();

            //getting data from excel file.
            testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CreateSubAccountData");
            testData = new TestData(testCaseData.getTestCases());

            AccountSettings accountSettings = testData.getAccountSettingsData().get(0);
            accountInformation = testData.getAccountInformation().get(0);

            //creating new account.
            subAccountsPage.clickAddNew();
            subAccountsPage.fillAccountSettings(accountSettings);
            subAccountsPage.fillAccountInformation(accountInformation);
            subAccountsPage.enterCustomerID("12345");
            subAccountsPage.clickSave();

            String message = webDriver.element().getMessageTitle();
            reporter.info("Message Displayed = " + message);
            // Assert.assertTrue(message.contains("Success"), "Message Displayed was Success", "Message Displayed was not Success");

            ExcelDataPool excelDataPool = new ExcelDataPool();
            //updating new account name in excel file.

            Map<Integer, Map<String, String>> updateDataInExcel = new HashMap<>();
            Map<String, String> columnsValue = new HashMap<>();
            /*columnsValue.put("ACCOUNT_NAME", accountInformation.getAccountName());
            updateDataInExcel.put(1, columnsValue);

            testNGTestCase.updateTestCaseDataIntoExcelFile(this.getClass().getName(), "CreateSubAccountData", updateDataInExcel);
*/
            columnsValue.put("ACCOUNT_NAME", accountInformation.getAccountName());
            updateDataInExcel.put(1, columnsValue);
            excelDataPool.writeIntoWorkBook("./src/test/resources/datasheets/",
                    "ApiTestDataSprint.xlsx", "AccountData", updateDataInExcel);

            //enable all products on account level.
            accountSettingsPage = subAccountsPage.selectSubAccount(accountInformation);

            webDriver.switchTo().defaultContent();
            ProductsPage productsPage = accountSettingsPage.clickProductsFromDropDown();
            productsPage.enableAllProducts();

            message = webDriver.element().getMessageTitle();
            reporter.info("Message Displayed = " + message);
            Assert.assertTrue((message.contains("Success") || message.contains("Warning")), "Message Displayed was Success", "Message Displayed was not Success");

            productsPage.saveAnonymizationPolicy();
            //Assert.assertElementDisplayed(ProductsPageConstatnts.MESSAGE_BOX);

            UserInformationPage userInformationPage = productsPage.clickUserInformationDropDown();

            XLSTestCaseData userInformationTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "UserInformation");
            TestData userInformationTestData = new TestData(userInformationTestCaseData.getTestCases());
            userInformation = userInformationTestData.getUserInformation().get(0);

            //Adding new user to the account.
            userInformationPage.addNewUser(userInformation);
            userInformationPage.clickSave();

            message = webDriver.element().getMessageText();
            reporter.info("Message Displayed = " + message);

            //asserting whether email containing temporary password is sent to the email or not.
            if (!message.contains("temporary password")) {
                reporter.info("Password is not received hence trying again.");
                userInformationPage.enterLoginId(userInformation.getLoginId());
                userInformationPage.clickSearchButton();
                userInformationPage.clickAccountIDRow();
                userInformationPage.checkGeneratePassword();
                userInformationPage.clickSave();

                message = webDriver.element().getMessageText();
                reporter.info("Message Displayed = " + message);
                Assert.assertTrue(message.contains("temporary password"), "Message Displayed was Success", "Message Displayed was not Success");
            } else
                Assert.assertTrue(message.contains("temporary password"), "Message Displayed was Success", "Message Displayed was not Success");


            //updating login id to the user information tab in excel sheet.
            Map<Integer, Map<String, String>> updateDataIntoExcel = new HashMap<>();
            Map<String, String> columnsValueData = new HashMap<>();
            columnsValueData.put("LOGIN_ID", userInformation.getLoginId());
            updateDataIntoExcel.put(1, columnsValueData);

            testNGTestCase.updateTestCaseDataIntoExcelFile(this.getClass().getName(), "UserInformation", updateDataIntoExcel);

            //logout the application.
            LoginPage loginPage = userInformationPage.logout();
            loginPage.load();

            //getting username and password
            String userName = userInformation.getLoginId();
            EmailParser emailParser = new EmailParser(ConfigManager.getEmailHostName(), ConfigManager.getEmailUserName(), ConfigManager.getEmailPassword());
            String password = emailParser.getTempPassword("Certain Temporary Password notification", userName);
            //emailParser.cleanUpUsedMails("Certain Temporary Password notification");
            emailParser.disconnect();
            emailParser = null;
            if (password == null || password.isEmpty())
                reporter.fail("Email Message does not received or password was not in the Email.");

            reporter.info("username value " + userName);
            reporter.info("password value " + password);
            //login to the application.
            ResetPasswordFirstLoginPage resetPasswordFirstLoginPage =
                    loginPage.loginAsNewUser(userName, password);

            Assert.assertElementDisplayed(ResetPasswordFirstLoginPageConstants.RESET_PASSWORD_LABEL);


            dashboardPage = resetPasswordFirstLoginPage.resetPassword(userInformation, password);

            //click on don't show button.
            dashboardPage.clickDontShowButtonIfPresent();
            // Assert.assertElementDisplayed(DashboardPageConstants.DASHBOARD_HEADER);

            dashboardPage.clickAccountSettingFromDropDown();

            EventListPage eventListPage = accountSettingsPage.clickEvents();
            eventListPage.clickDontShowButtonIfPresent();

            EventDetailsPage eventDetailsPage = eventListPage.clickAddEvent();

            // add new event.
           // eventDetailsPage.fillEventDetails(event);
            eventDetailsPage.testFillEventDetails(event, 5);
            eventDetailsPage.fillBusinessAttributesAndFields(eventAttributes);
            eventListPage = eventDetailsPage.clickSave();

            /*message = webDriver.element().getMessageTitle();
            reporter.info("Message Displayed = " + message);
            if (message != null)
                Assert.assertTrue((message.contains("Success")), "Message Displayed was Success", "Message Displayed was not Success");
*/
            //enable all products at event level.
            EventOptionsPage eventOptionsPage = eventListPage.clickConfigureFromPlanDropdown();
            eventOptionsPage.enableAllProducts();
            eventOptionsPage.unCheckDoubleBookingSpeaker();
            eventOptionsPage.enableRegistrationGroupAsPromoCode();

            message = webDriver.element().getMessageTitle();
            reporter.info("Message Displayed = " + message);
            Assert.assertTrue((message.contains("Success") || message.contains("Warning")), "Message Displayed was Success", "Message Displayed was not Success");

            eventListPage = eventOptionsPage.clickEvents();
            eventDetailsPage = eventListPage.clickAddEvent();

            eventDetailsPage.testFillEventDetails(event1, 5);
            eventDetailsPage.fillBusinessAttributesAndFields(eventAttributes);
            eventListPage = eventDetailsPage.clickSave();

            /*message = webDriver.element().getMessageTitle();
            reporter.info("Message Displayed = " + message);
            if (message != null)
                Assert.assertTrue((message.contains("Success")), "Message Displayed was Success", "Message Displayed was not Success");
*/
            //enable all products at event level.
            eventOptionsPage = eventListPage.clickConfigureFromPlanDropdown();
            eventOptionsPage.enableAllProducts();
            eventOptionsPage.unCheckDoubleBookingSpeaker();
            eventOptionsPage.enableRegistrationGroupAsPromoCode();

            message = webDriver.element().getMessageTitle();
            reporter.info("Message Displayed = " + message);
            Assert.assertTrue((message.contains("Success") || message.contains("Warning")), "Message Displayed was Success", "Message Displayed was not Success");

            eventCode1 = event.getEventCode();
            eventCode2 = event1.getEventCode();
        }
        else{
            QuickSearchPage quickSearchPage = accountSettingsPage.clickEventsUnderSearchIcon();
            //webDriver.switchToLatestTabOrWindow();
            quickSearchPage.switchToIFrame();
            //quickSearchPage.clickEventsTab();
            quickSearchPage.selectSearchInValue("Event Code");
            quickSearchPage.selectSearchByValue("is equal to");
            quickSearchPage.enterSearchForValue(eventCode);
            quickSearchPage.selectAccount(accountName);
            quickSearchPage.clickSearchButton();
            quickSearchPage.checkNewRecordCheckbox();
            quickSearchPage.selectSearchedRowByAccountCode(accountName);

            webDriver.switchTo().defaultContent();
            webDriver.closePopup();
            Thread.sleep(3000);

            EventOverviewPage eventOverviewPage = new EventOverviewPage();
            accountSettingsPage = quickSearchPage.clickAccountSettingFromDropDown();

            ProductsPage productsPage = accountSettingsPage.clickProductsFromDropDown();
            productsPage.enableAllProducts();

            String message = webDriver.element().getMessageTitle();
            reporter.info("Message Displayed = " + message);
            if (message != null)
                Assert.assertTrue((message.contains("Success") || message.contains("Warning")), "Message Displayed was Success", "Message Displayed was not Success");

            productsPage.saveAnonymizationPolicy();
           // productsPage.clickProductsTab();
            accountSettingsPage = productsPage.clickAccountSettingFromDropDown();
            EventListPage eventListPage = accountSettingsPage.clickEvents();

            if (eventListPage.searchEventWithAddEvent(event1.getEventCode())) {
                EventDetailsPage eventDetailsPage = eventListPage.clickAddEvent();

                eventDetailsPage.testFillEventDetails(event1, 5);
                eventDetailsPage.fillBusinessAttributesAndFields(eventAttributes);
                eventListPage = eventDetailsPage.clickSave();

                //enable all products at event level.
                EventOptionsPage eventOptionsPage = eventListPage.clickConfigureFromPlanDropdown();
                eventOptionsPage.enableAllProducts();
                eventOptionsPage.clickEvents();

            }
            //search event.
            eventListPage.searchEvent(eventCode);
            EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();
            EventOptionsPage eventOptionsPage = eventDashboardPage.clickConfigureFromPlanDropdown();
            eventOptionsPage.enableAllProducts();
            eventOptionsPage.unCheckDoubleBookingSpeaker();
            eventOptionsPage.enableRegistrationGroupAsPromoCode();
            eventCode1 = eventCode;
            eventCode2 = event1.getEventCode();


        }
    }

    @Test(groups = {"regression"}, enabled = true, priority = 2, description = "Add job function ", dependsOnMethods = "testCreateSubAccount")
    public void testAddTagsJobFunctionsIndustriesCustomFields() throws Exception {
        //click on sub accounts menu item.
        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();

        ManagementTagsPage managementTagsPage = accountSettingsPage.clickTagsLink();

        String tagName[] = ConfigProperties.getTags().split(",");
        if (tagName.length > 1) {
            for (int i = 0; i < tagName.length; i++) {
                AddTags addTags = new AddTags(tagName[i], tagName[i], "Sessions");
                managementTagsPage.addTags(addTags);
            }
        } else {
            AddTags addTags = new AddTags(ConfigProperties.getTags(), ConfigProperties.getTags(), "Sessions");
            managementTagsPage.addTags(addTags);
        }

        String industries[] = ConfigProperties.getIndustries().split(",");

        //Create industries
        IndustriesPage industriesPage = managementTagsPage.clickIndustriesLink();
        if (industries.length > 1) {
            for (int i = 0; i < industries.length; i++) {
                industriesPage.enterIndustryName(industries[i]);
                industriesPage.clickAddButton();
            }
        } else {
            industriesPage.enterIndustryName(ConfigProperties.getIndustries());
            industriesPage.clickAddButton();
        }

        //create job functions
        JobFunctionsPage jobFunctionsPage = industriesPage.clickJobFunctionsTab();

        String jobfunctions[] = ConfigProperties.getJobFunctions().split(",");

        if (jobfunctions.length > 1) {
            for (int i = 0; i < jobfunctions.length; i++) {
                jobFunctionsPage.enterJobFuncitonName(jobfunctions[i]);
                jobFunctionsPage.clickAddButton();

            }
        } else {
            jobFunctionsPage.enterJobFuncitonName(ConfigProperties.getJobFunctions());
            jobFunctionsPage.clickAddButton();

        }

        EventListPage eventListPage = jobFunctionsPage.clickEvents();

        eventListPage.searchEvent(eventCode1);
        EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();
        //webDriver.waitFor().titleContains("Certain: Event Summary Page");
        SessionManagementPage sessionManagementPage = eventDashboardPage.clickSpeakerSessionItemFromManageMenu();
        sessionManagementPage.fillSetupSessionLayoutConference();

        sessionManagementPage.clickSessionDontShowButtonIfPresent();
        sessionManagementPage.clickSessionDontShowButtonIfPresent();
        //Session setting
        //create industries list
        SessionSetupPage sessionSetupPage = sessionManagementPage.clickOnSetupTab();
        IndustryListPage industryListPage = sessionSetupPage.clickOnIndustriesTab();

        industryListPage.selectIndustryDropdown(ConfigProperties.getIndustries());

        //create job functions list
        JobFunctionListPage jobFunctionListPage = sessionSetupPage.clickOnJobFunctionsTab();

        jobFunctionListPage.selectJobFunctionDropdown(ConfigProperties.getJobFunctions());

        SessionCustomFieldsPage sessionCustomFieldsPage = jobFunctionListPage.clickOnCustomFieldsTab();

        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CustomFieldsDetails");
        testData = new TestData(testCaseData.getTestCases());

        //Type of custom field and no of options required
        for (int i = 0; i < 6; i++) {
            AddCustomFieldsData addCustomFieldsData = testData.getCustomFieldDetails().get(i);
            if (!sessionCustomFieldsPage.isCustomFieldFound(addCustomFieldsData)) {
                sessionCustomFieldsPage.addCustomFieldWithData(addCustomFieldsData);
            }
        }

        SessionLayoutPage sessionLayoutPage = sessionCustomFieldsPage.clickOnSessionLayoutTab();
        sessionLayoutPage.clickSessionDontShowButtonIfPresent();

        sessionLayoutPage.clickStandardSessionLink();

        AddCustomFieldsPage addCustomFieldsPage = sessionLayoutPage.clickOnExistingCustomFields();
        addCustomFieldsPage.addAllStandardAndCustomFields();
        sessionLayoutPage = addCustomFieldsPage.addSections();
        sessionLayoutPage.saveLayout();

        String message = webDriver.element().getMessageTitle();
        reporter.info("Message Displayed = " + message);
        if (message != null)
            Assert.assertTrue(message.contains("Success"), "Standard and custom fields are added successfully", "Standard and Custom fields are not added successfully in Session layout");

        //Setup all the things in simple session
        eventListPage = sessionLayoutPage.clickEvents();

        eventListPage.searchEvent(eventCode2);
        eventDashboardPage = eventListPage.selectSearchedEvent();
        // webDriver.waitFor().titleContains("Certain: Event Summary Page");
        sessionManagementPage = eventDashboardPage.clickSpeakerSessionItemFromManageMenu();

        sessionManagementPage.fillSetupSessionLayoutSimple();
        sessionManagementPage.clickSessionDontShowButtonIfPresent();
        sessionManagementPage.clickSessionDontShowButtonIfPresent();
        //Session setting
        //create industries list
        sessionSetupPage = sessionManagementPage.clickOnSetupTab();
        industryListPage = sessionSetupPage.clickOnIndustriesTab();

        industryListPage.selectIndustryDropdown(ConfigProperties.getIndustries());

        //create job functions list
        jobFunctionListPage = sessionSetupPage.clickOnJobFunctionsTab();

        jobFunctionListPage.selectJobFunctionDropdown(ConfigProperties.getJobFunctions());

        sessionCustomFieldsPage = jobFunctionListPage.clickOnCustomFieldsTab();

        //Type of custom field and no of options required
        for (int i = 0; i < 6; i++) {
            AddCustomFieldsData addCustomFieldsData = testData.getCustomFieldDetails().get(i);
            if (!sessionCustomFieldsPage.isCustomFieldFound(addCustomFieldsData)) {
                sessionCustomFieldsPage.addCustomFieldWithData(addCustomFieldsData);
            }
        }

        sessionLayoutPage = sessionCustomFieldsPage.clickOnSessionLayoutTab();
        sessionLayoutPage.clickSessionDontShowButtonIfPresent();

        sessionLayoutPage.clickStandardSessionLink();

        addCustomFieldsPage = sessionLayoutPage.clickOnExistingCustomFields();
        addCustomFieldsPage.addAllStandardAndCustomFields();
        sessionLayoutPage = addCustomFieldsPage.addSections();
        sessionLayoutPage.saveLayout();

        message = webDriver.element().getMessageTitle();
        reporter.info("Message Displayed = " + message);
        if (message != null)
            Assert.assertTrue(message.contains("Success"), "Standard and custom fields are added successfully", "Standard and Custom fields are not added successfully in Session layout");

    }

    @Test(groups = {"regression", "UAT-SET-1"}, enabled = true, priority = 3, description = "Create Simple Session,Speaker,Industry list,Job functions,Custom fields,Track,Location", dependsOnMethods = "testAddTagsJobFunctionsIndustriesCustomFields")
    public void testCreateConferenceSession() throws Exception {
        //click on sub accounts menu item.
        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();

        EventListPage eventListPage = accountSettingsPage.clickEvents();

        XLSTestCaseData addSessionTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "AddSessionData");
        TestData addSessionTestData = new TestData(addSessionTestCaseData.getTestCases());
        AddSessionData session = addSessionTestData.getSessionDetails().get(0);

        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "ImportSpeaker");
        testData = new TestData(testCaseData.getTestCases());
        ImportSpeaker importSpeakerOne = testData.getImportSpeaker().get(0);

        //search event
        eventListPage.searchEvent(eventCode1);
        EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();
        //webDriver.waitFor().titleContains("Certain: Event Summary Page");
        SessionManagementPage sessionManagementPage = eventDashboardPage.clickSpeakerSessionItemFromManageMenu();

        sessionManagementPage.fillSetupSessionLayoutConference();

        String sessionCode = session.getSessionCode();
        reporter.info("Session Code : " + sessionCode);
        //add new session
        if (!sessionManagementPage.isSessionFoundInSessionsList(sessionCode)) {
            AddSessionPage addSessionPage = sessionManagementPage.clickAddSessionButton();
            addSessionPage.fillAddNewSessionDetails(session);
            addSessionPage.fillTagsIndustryTrackDetails(tags, track, industries, functions);
            addSessionPage.clickSaveButton();

            try {
                String message = webDriver.element().getMessageTitle();
                reporter.info("Message Displayed = " + message);
                if (message != null)
                    Assert.assertTrue(message.contains("Success"), "Session added successfully", "Session not added successfully");
            } catch (Exception e) {
            }
            webDriver.waitFor().waitforAngularJS();
        } else {
            reporter.info("Session already created using session code " + sessionCode);
        }
        //import speaker files
        SpeakerManagementPage speakerManagementPage = sessionManagementPage.clickSpeakerTabInSessionPage();

        //import speaker
        SpeakerManagementImportPage speakerManagementImportPage = speakerManagementPage.clickImportButton();

        speakerManagementImportPage.switchToLatestTabOrWindow();
        speakerManagementImportPage.importFile(importSpeakerOne.getFileName());

        //create speaker type
        speakerManagementPage.clickSpeakerTypesTab();
        speakerManagementPage.enterSpeakerTypeName("Speaker1");
        speakerManagementPage.clickAddButton();
    }

    @Test(groups = {"regression", "UAT-SET-1"}, enabled = true, priority = 4, description = "Create Simple Session,Speaker,Industry list,Job functions,Custom fields,Track,Location", dependsOnMethods = "testAddTagsJobFunctionsIndustriesCustomFields")
    public void testCreateSimpleSession() throws Exception {
        //click on sub accounts menu item.
        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();

        EventListPage eventListPage = accountSettingsPage.clickEvents();

        XLSTestCaseData addSessionTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "AddSessionData");
        TestData addSessionTestData = new TestData(addSessionTestCaseData.getTestCases());
        AddSessionData session = addSessionTestData.getSessionDetails().get(0);

        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "ImportSpeaker");
        testData = new TestData(testCaseData.getTestCases());
        ImportSpeaker importSpeakerOne = testData.getImportSpeaker().get(0);

        //search event
        eventListPage.searchEvent(eventCode2);
        EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();
        //webDriver.waitFor().titleContains("Certain: Event Summary Page");
        SessionManagementPage sessionManagementPage = eventDashboardPage.clickSpeakerSessionItemFromManageMenu();

        sessionManagementPage.fillSetupSessionLayoutSimple();

        String sessionCode = session.getSessionCode();
        reporter.info("Session Code : " + sessionCode);

        if (!sessionManagementPage.isSessionFoundInSimpleSessionsList(sessionCode)) {
            //add new session
            AddSessionPage addSessionPage = sessionManagementPage.clickAddSessionButton();
            addSessionPage.fillSessionDetailsForSimple(session);
            addSessionPage.fillTagsIndustryTrackDetails(tags, track, industries, functions);
            addSessionPage.clickSaveButton();

            try {
                String message = webDriver.element().getMessageTitle();
                reporter.info("Message Displayed = " + message);
                if (message != null)
                    Assert.assertTrue(message.contains("Success"), "Session added successfully", "Session not added successfully");
            } catch (Exception e) {

            }
            webDriver.waitFor().waitforAngularJS();
        } else {
            reporter.info("Session already created using session code " + sessionCode);
        }
        //import speaker files
        SpeakerManagementPage speakerManagementPage = sessionManagementPage.clickSpeakerTabInSessionPage();

        //import speaker
        SpeakerManagementImportPage speakerManagementImportPage = speakerManagementPage.clickImportButton();

        speakerManagementImportPage.switchToLatestTabOrWindow();
        speakerManagementImportPage.importFile(importSpeakerOne.getFileName());

        //create speaker type
        speakerManagementPage.clickSpeakerTypesTab();
        speakerManagementPage.enterSpeakerTypeName("Speaker1");
        speakerManagementPage.clickAddButton();
    }

    @Test(groups = {"regression", "UAT-SET-1"}, enabled = false, priority = 5, description = "Create Registration and assign Payment", dependsOnMethods = "testCreateSubAccount")
    public void testPaymentTransaction() throws Exception {
        //click on sub accounts menu item.
        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();

        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "TaxData");
        testData = new TestData(testCaseData.getTestCases());
        TaxCode taxCode = testData.getTaxCodeData().get(0);

        //get e-commerce account details
        XLSTestCaseData eCommerceAccountTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "ECommerceAccountDetail");
        TestData eCommerceAccountTestData = new TestData(eCommerceAccountTestCaseData.getTestCases());
        ECommerceAccount eCommerceAccount = eCommerceAccountTestData.getECommerceAccount().get(0);

        //get attendee type value
        XLSTestCaseData attendeeTypeTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "AttendeeTypesData");
        TestData attendeeTypeTestData = new TestData(attendeeTypeTestCaseData.getTestCases());
        AttendeeType attendeeType1 = attendeeTypeTestData.getCreateAttendeeType().get(2);
        AttendeeType attendeeType2 = attendeeTypeTestData.getCreateAttendeeType().get(1);
        AttendeeType attendeeType = attendeeTypeTestData.getCreateAttendeeType().get(0);

        //get attendee fee details
        XLSTestCaseData attendeeTypeFeeDetailTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "AttendeeTypeFeeDetail");
        TestData attendeeTypeFeeDetailTestData = new TestData(attendeeTypeFeeDetailTestCaseData.getTestCases());
        AttendeeTypeFee attendeeTypeFee = attendeeTypeFeeDetailTestData.getAttendeeTypeFee().get(0);
        AttendeeTypeFeeDetail attendeeTypeFeeDetail1 = attendeeTypeFeeDetailTestData.getAttendeeTypeFeeDetail().get(0);

        //get create reg data
        XLSTestCaseData registrationTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CreateRegistrationData");
        TestData registrationData = new TestData(registrationTestCaseData.getTestCases());
        CreateRegistrationData createRegistrationData = registrationData.getCreateRegistrationData().get(27);

        //get charge data
        XLSTestCaseData paymentTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "PaymentOrRefundInformation");
        TestData paymentData = new TestData(paymentTestCaseData.getTestCases());
        PaymentOrRefundInformationData payment = paymentData.getPaymentOrRefundInformationData().get(0);

        FinancialDataSettingPage financialDataSettingPage = accountSettingsPage.clickFinancialSettingLink();

        /*if (!financialDataSettingPage.isTaxCodeFound(taxCode.getCode())) {
            TaxCodeSettingsPage taxCodeSettingsPage = financialDataSettingPage.clickTaxCodeAddNewButton();
            taxCodeSettingsPage.fillTaxDetails(taxCode);
            financialDataSettingPage = taxCodeSettingsPage.clickSaveButton();
        }*/
        //Add tax code
        if (!financialDataSettingPage.isAccountsFound(eCommerceAccount.getName())) {
            ECommerceAccountDetailsPage eCommerceAccountDetailsPage = financialDataSettingPage.clickAddNewECommerceAccountButton();

            eCommerceAccountDetailsPage.enterDetails(eCommerceAccount);
            financialDataSettingPage = eCommerceAccountDetailsPage.clickSaveButton();

            String messageText = webDriver.element().getMessageText();
            reporter.info("Message Displayed = " + messageText);
            Verify.verifyTrue((messageText.contains("The payment processing account was created successfully")), eCommerceAccount.getOnlineProcessor() + " Payment Account Created successfully", "Error in creating Payment Account");
        }
        accountSettingsPage = financialDataSettingPage.clickAccountSettingFromDropDown();

        EventListPage eventListPage = accountSettingsPage.clickEvents();
        //search event
        eventListPage.searchEvent(eventCode1);
        EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();

        EventOverviewPage eventOverviewPage = eventDashboardPage.clickEventSetupFromPlanMenu();
        AttendeeTypesPage attendeeTypesPage = eventOverviewPage.clickAttendeeTypesTabInLeftPanel();

        if (!attendeeTypesPage.isAttendeeFound(attendeeType1.getCode())) {
            //attendeeTypesPage.clickAddNew();
            attendeeTypesPage.addAttendee(attendeeType1);
        }

        if (!attendeeTypesPage.isAttendeeFound(attendeeType2.getCode())) {
            attendeeTypesPage.addExhibitorAttendee(attendeeType2);
        }

        if (!attendeeTypesPage.isAttendeeFound(attendeeType.getCode())) {
            attendeeTypesPage.clickAddNew();
            //add attendee type code here
            attendeeTypesPage.addAttendeeTypeWithRegistrationOrCancellationFee(attendeeType, attendeeTypeFee);
            attendeeTypesPage.enterRegistrationFeeDetailRow(attendeeTypeFeeDetail1, 1);
            attendeeTypesPage.clickSave();
            String messageText = webDriver.element().getMessageText();
            reporter.info("Message Displayed = " + messageText);
            Verify.verifyTrue((messageText.contains("The Attendee Type code has been created.")), eCommerceAccount.getOnlineProcessor() + " Payment Account Created successfully", "Error in creating Payment Account");
        }
        //create registration
        eventDashboardPage = attendeeTypesPage.clickEventDashboardLink();
        CreateRegistrationPage createRegistrationPage = eventDashboardPage.clickAddRegistration();
        createRegistrationPage.addNewRegistration(createRegistrationData);

        FinancialsPage financialsPage = createRegistrationPage.clickFinancialsTab();
        PaymentInformationPage paymentInformationPage = financialsPage.clickPaymentTab();

        paymentInformationPage.fillPaymentOrRefundInformation(payment);
        paymentInformationPage.clickSaveButton();

        String messageText = webDriver.element().getMessageText();
        reporter.info("Message Displayed = " + messageText);
        Assert.assertTrue(messageText.contains("Completed successfully."), "payment information successfully modified", "payment information not modified");
        //Assert.assertTrue(messageText.contains("The payments were distributed among the registrations on this order."), "Payment information successfully modified", "Payment information not modified");

    }

    @Test(groups = {"apidata", "UAT-SET-1"}, enabled = false, priority = 10, description = "Create a Block", dependsOnMethods = "testCreateSubAccount")
    public void testCreateBlockAndRoomType() throws Exception {

        //getting hotel data from excel sheet
        XLSTestCaseData createHotelTestCase = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CreateHotelData");
        TestData createHotelTestData = new TestData(createHotelTestCase.getTestCases());
        HotelData hotelData0 = createHotelTestData.getCreateHotelData().get(0);
        HotelData hotelData1 = createHotelTestData.getCreateHotelData().get(1);

        //getting block data from excel sheet
        XLSTestCaseData accommodationBlockInformationTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "AccommodationBlockInformation");
        TestData accommodationBlockInformationData = new TestData(accommodationBlockInformationTestCaseData.getTestCases());
        AccommodationBlockInformation blockInformation = accommodationBlockInformationData.getBlockInformation().get(0);

        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();
        EventListPage eventListPage = accountSettingsPage.clickEvents();

        //search event.
        eventListPage.searchEvent(eventCode1);
        EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();

        BlocksPage blocksPage = eventDashboardPage.clickAccommodationTab();
        HotelsPage hotelsPage = blocksPage.clickHotelsSubTab();
        if (!hotelsPage.isHotelNameFound(hotelData0.getHotelCode())) {
            hotelsPage.createHotel(hotelData0);
        }

        //create room types
        blocksPage = hotelsPage.clickAccommodationLink();
        RoomTypesPage roomTypesPage = blocksPage.clickRoomTypesSubTab();
        roomTypesPage.createRoomType(hotelData0);

        //click accommodation link
        blocksPage = roomTypesPage.clickAccommodationLink();
        if (!blocksPage.isHotelNameFound(hotelData0.getHotelCode())) {
            AccommodationBlockInformationPage accommodationBlockInformationPage = blocksPage.clickOnAddNewButton();
            //create blocks
            blocksPage = accommodationBlockInformationPage.testCreateBlocks(blockInformation, 4);
            blocksPage.waitForWindowToDispose();

        }

        roomTypesPage = blocksPage.clickRoomTypesSubTab();
        String uniqueCode = roomTypesPage.randomString(6);
        roomTypesPage.createRoomTypeUnique(hotelData1, uniqueCode);

        ExcelDataPool excelDataPool = new ExcelDataPool();

        Map<Integer, Map<String, String>> updateDataInExcel = new HashMap<>();
        Map<String, String> columnsValue = new HashMap<>();
        columnsValue.put("ROOM_TYPE_CODE", uniqueCode);
        updateDataInExcel.put(2, columnsValue);
        excelDataPool.writeIntoWorkBook("./src/test/resources/datasheets/",
                "ApiTestDataSprint.xlsx", "CreateHotelData", updateDataInExcel);

        eventDashboardPage = roomTypesPage.clickEventDashboardLink();
        EventOverviewPage eventOverviewPage = eventDashboardPage.clickEventSetupFromPlanMenu();

        EventSuppliersPage eventSuppliersPage = eventOverviewPage.clickSuppliersTab();
        eventSuppliersPage.clickAddSuppliersButton();
        eventSuppliersPage.selectSuppliersValue(blockInformation.getHotelName());
        eventSuppliersPage.selectGlAccountValue("4120 - Room Rental");
        eventSuppliersPage.enterContractValue("70");
        eventSuppliersPage.enterActualValue("80");
        eventSuppliersPage.clickSaveAndNewButton();

        eventSuppliersPage.selectSuppliersValue(blockInformation.getHotelName());
        eventSuppliersPage.selectGlAccountValue("4320 - Car Rental");
        eventSuppliersPage.enterContractValue("60");
        eventSuppliersPage.enterActualValue("90");
        eventSuppliersPage.clickSaveAndNewButton();

        webDriver.closePopup();

    }

    @Test(groups = {"apidata", "UAT-SET-1"}, enabled = false, priority = 6, description = "Create Appointment Preference and Rotation", dependsOnMethods = "testCreateSubAccount")
    public void testCreateAppointmentPreferenceAndRotation() throws Exception {

        //get appointment block data
        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "AddAppointmentBlock");
        testData = new TestData(testCaseData.getTestCases());
        AddAppointmentBlock addAppointmentBlock = testData.getAddAppointmentBlockData().get(0);

        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CreateAppointmentRotationsData");
        testData = new TestData(testCaseData.getTestCases());
        AppointmentRotationsData appointmentRotationsData = testData.getAppointmentRotationsData().get(0);

        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CreateRegistrationData");
        testData = new TestData(testCaseData.getTestCases());
        CreateRegistrationData createRegistrationData0 = testData.getCreateRegistrationData().get(25);
        CreateRegistrationData createRegistrationData1 = testData.getCreateRegistrationData().get(26);

        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "TemplateData");
        testData = new TestData(testCaseData.getTestCases());
        Templates templates = testData.getTemplatesData().get(0);

        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();
        EventListPage eventListPage = accountSettingsPage.clickEvents();

        //search event.
        eventListPage.searchEvent(eventCode1);
        EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();
        EventOptionsPage eventOptionsPage = eventDashboardPage.clickConfigureFromPlanDropdown();
        eventOptionsPage.checkAppointmentRotationCheckbox();
        eventOptionsPage.clickSaveButton();
        eventOptionsPage.closeMessage();

        AppointmentsOverviewPage appointmentsOverviewPage = eventDashboardPage.clickAppointmentsFromEngageDropdown();
        AppointmentTemplatesPage appointmentTemplatesPage = appointmentsOverviewPage.clickTemplatesTab();
        AppointmentRotationsPage appointmentRotationsPage = appointmentTemplatesPage.clickRotationsTab();

        if (!appointmentRotationsPage.isRotationNamePresent(appointmentRotationsData.getRotationName())) {
            appointmentRotationsPage.clickEventDashboardLink();
            CreateRegistrationPage createRegistrationPage = eventDashboardPage.clickAddRegistration();
            //create registration
            createRegistrationPage.addNewRegistration(createRegistrationData0);
            createRegistrationPage.clickAddRegistrationButton();
            createRegistrationPage.addNewRegistration(createRegistrationData1);

            eventDashboardPage = createRegistrationPage.clickEventDashboardLink();
            appointmentsOverviewPage = eventDashboardPage.clickAppointmentsFromEngageDropdown();
            appointmentTemplatesPage = appointmentsOverviewPage.clickTemplatesTab();
            EditAppointmentTemplatesPage editAppointmentTemplatesPage = appointmentTemplatesPage.clickEditDefaultTemplatesButton();
            AddAppointmentBlockPage addAppointmentBlockPage = editAppointmentTemplatesPage.clickAddBlockButton();
            addAppointmentBlockPage.waitForWindowToAppear();
            addAppointmentBlockPage.createAppointmentBlock(addAppointmentBlock);
            addAppointmentBlockPage.clickSaveButton();

            editAppointmentTemplatesPage = new EditAppointmentTemplatesPage();
            editAppointmentTemplatesPage.waitForWindowToDispose();
            editAppointmentTemplatesPage.addTemplatesDetails(templates);
            editAppointmentTemplatesPage.clickSaveButton();

            AppointmentConfigurationPage appointmentConfigurationPage = editAppointmentTemplatesPage.clickConfigureTab();
            //allow appointment preferences
            appointmentConfigurationPage.clickEditIconByAttendeeType("Attendee");
            appointmentConfigurationPage.selectAttendeeTypeCheckbox("Attendee", "Attendee");
            appointmentConfigurationPage.checkAllowPreferencesCheckboxByAttendeeType("Attendee");
            Thread.sleep(2000);

            //save appointment eligibility
            appointmentConfigurationPage.clickSaveButtonInAppointmentEligibility();

            String message = webDriver.element().getMessageTitle();
            reporter.info("message text---------" + message);
            Assert.assertTrue(message.contains("Success") || (message.contains("Warning")), "Appointment eligibility set successfully", "Appointment eligibility not set successfully");

            webDriver.waitFor().waitforAngularJS();

            AppointmentSearchPage appointmentSearchPage = appointmentConfigurationPage.clickSearchTab();

            String registrantName = createRegistrationData1.getContactInformation().getFirstName() + " " + createRegistrationData1.getContactInformation().getLastName();

            appointmentSearchPage.enterValueInSearchTextBox(createRegistrationData1.getContactInformation().getEmail());
            appointmentSearchPage.clickSearchButton();

            AppointmentSearchCalendarPage appointmentSearchCalendarPage = appointmentSearchPage.clickViewAppointmentCalendar(registrantName);
            AppointmentFormPage appointmentFormPage = appointmentSearchCalendarPage.clickAddAppointmentButton();
            appointmentFormPage.waitForWindowToAppear();

            appointmentFormPage.selectAttendeeTypes(createRegistrationData0.getAttendeeType());
            appointmentFormPage.selectOrganization(createRegistrationData0.getContactInformation().getOrganization());
            String registrantName1 = createRegistrationData0.getContactInformation().getFirstName() + " " + createRegistrationData0.getContactInformation().getLastName();
            appointmentFormPage.selectTargetRegistrant(registrantName1);
            appointmentFormPage.selectStartDate(appointmentFormPage.getDateByDays(1));
            appointmentFormPage.selectStartTime(addAppointmentBlock.getFromTime());

            Thread.sleep(1000);
            appointmentSearchCalendarPage = appointmentFormPage.clickAppointmentSaveButton();

            message = webDriver.element().getMessageTitle();
            reporter.info("message text---------" + message);
            Assert.assertTrue(message.contains("Success") || (message.contains("Warning")), "Appointment eligibility set successfully", "Appointment eligibility not set successfully");

            appointmentTemplatesPage = appointmentConfigurationPage.clickTemplatesTab();
            appointmentRotationsPage = appointmentTemplatesPage.clickRotationsTab();
            appointmentRotationsPage.clickOnAddNewButton();
            //create rotations
            appointmentRotationsPage.createAppointmentRotations(appointmentRotationsData);
            appointmentRotationsPage.clickSaveButton();

            String rotation = appointmentRotationsData.getRotationName();
            reporter.info("Rotation Name: " + rotation);
        }
    }

    @Test(groups = {"apidata", "UAT-SET-1"}, enabled = false, priority = 8, description = "Create Appointment Preference and Rotation", dependsOnMethods = "testCreateSubAccount")
    public void testCreateRegistration() throws Exception {

        //get Registration Data
        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CreateRegistrationData");
        TestData getRegistrationData = new TestData(testCaseData.getTestCases());

        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();
        EventListPage eventListPage = accountSettingsPage.clickEvents();

        eventListPage.searchEvent(eventCode1);
        EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();

        eventDashboardPage.clickTabularButton();
        int regCount = eventDashboardPage.getRegistrationByStatusTable("Total");
        webDriver.switchTo().defaultContent();
        if (regCount >= 25) {
            reporter.info("no need to create registrations.");
        } else if (regCount < 25) {
            int var = 25 - regCount;
            CreateRegistrationPage createRegistrationPage = eventDashboardPage.clickAddRegistration();

            for (int registrationCount = 0; registrationCount < var; registrationCount++) {

                CreateRegistrationData createRegistrationData = getRegistrationData.getCreateRegistrationData().get(registrationCount);
                createRegistrationPage.addNewRegistrationWithRequiredField(createRegistrationData);
                if (registrationCount != var - 1) {
                    eventDashboardPage.clickAddRegistration();
                }
            }
        }
    }

    @Test(groups = {"apidata", "UAT-SET-1"}, enabled = false, priority = 11, description = "Create Event Ques,Profile Ques,Reg Ques", dependsOnMethods = "testCreateSubAccount")
    public void testCreateQuestions() throws Exception {

        //Getting data from excel
        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();

        //get custom question details
        XLSTestCaseData questionTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CustomQuestionData");
        TestData questionData = new TestData(questionTestCaseData.getTestCases());

        //profile questions
        CustomProfileDataPage customProfileDataPage = accountSettingsPage.clickOnProfileDataFromRegistrationMenu();
        QuestionsPage questionsPage = new QuestionsPage();
        for (int i = 0; i < 3; i++) {
            CreateQuestionData createQuestionData0 = questionData.getCreateQuestionData().get(i);
            if (!customProfileDataPage.isQuestionFound(createQuestionData0.getName())) {
                questionsPage = customProfileDataPage.clickAddNewButton();
                questionsPage.switchToLatestTabOrWindow();
                questionsPage.createQuestionOfTypeText(createQuestionData0);
            }

        }
        accountSettingsPage = customProfileDataPage.clickAccountSettingFromDropDown();
        CustomEventDataPage customEventDataPage = accountSettingsPage.clickOnEventDataFromManagementMenu();
        for (int i = 3; i < 7; i++) {
            //event questions
            CreateQuestionData createQuestionData1 = questionData.getCreateQuestionData().get(i);
            if (!customEventDataPage.isQuestionFound(createQuestionData1.getName())) {
                questionsPage = customEventDataPage.clickAddNewButton();

                questionsPage.switchToLatestTabOrWindow();
                Thread.sleep(2000);
                questionsPage.createQuestionOfTypeText(createQuestionData1);
            }

        }

        customEventDataPage = new CustomEventDataPage();
        EventListPage eventListPage = customEventDataPage.clickEventsTopMenuLink();

        //search event
        //  EventListPage eventListPage = accountSettingsPage.clickEvents();
        eventListPage.searchEvent(eventCode1);
        EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();

        EventDetailsPage eventDetailsPage = eventDashboardPage.clickEventDetailsTab();
        CustomRegistrationQuestionsPage customRegistrationQuestionsPage = eventDetailsPage.clickQuestionsTabInLeftPannel();
        for (int i = 7; i < 10; i++) {
            CreateQuestionData createQuestionData2 = questionData.getCreateQuestionData().get(i);
            if (!customRegistrationQuestionsPage.isQuestionFound(createQuestionData2.getName())) {
                questionsPage = customRegistrationQuestionsPage.clickAddNewButton();
                questionsPage.switchToLatestTabOrWindow();
                Thread.sleep(2000);
                questionsPage.createQuestionOfTypeText(createQuestionData2);
            }


        }
        TravelInformationPage travelInformationPage = customRegistrationQuestionsPage.clickTravelTab();
        CreateQuestionData createQuestionData3 = questionData.getCreateQuestionData().get(11);
        travelInformationPage.selectItemValue("25");
        travelInformationPage.editQuestionsByText(createQuestionData3.getName());
        questionsPage.switchToLatestTabOrWindow();
        questionsPage.setDefaultAnswer(createQuestionData3.getDefaultAnswer());
        questionsPage.clickSaveAndClose();
        questionsPage.waitForWindowToDispose();

        createQuestionData3 = questionData.getCreateQuestionData().get(12);
        travelInformationPage.editQuestionsByText(createQuestionData3.getName());
        questionsPage.switchToLatestTabOrWindow();
        questionsPage.setDefaultAnswer(createQuestionData3.getDefaultAnswer());
        questionsPage.clickSaveAndClose();
        questionsPage.waitForWindowToDispose();

        createQuestionData3 = questionData.getCreateQuestionData().get(10);

        if (!travelInformationPage.isQuestionFound(createQuestionData3.getName())) {
            questionsPage = travelInformationPage.clickAddNewButton();
            questionsPage.switchToLatestTabOrWindow();
            Thread.sleep(2000);
            questionsPage.createQuestionOfTypeText(createQuestionData3);
            questionsPage.waitForWindowToDispose();
        }


    }

    @Test(groups = {"regression"}, enabled = false, priority = 9, description = "Create Agenda With Fee'", dependsOnMethods = "testCreateSubAccount")
    public void testCreateAgendaWithFee() throws Exception {

        //getting agenda data from excel sheet
        XLSTestCaseData agendaTestCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "AgendaDetails");
        testData = new TestData(agendaTestCaseData.getTestCases());
        AgendaData agendaData = testData.getAgendaData().get(0);

        //get agendaFee and fee details
        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "AgendaFeeData");
        testData = new TestData(testCaseData.getTestCases());
        AgendaFeeDetails agendaFeeDetails = testData.getAgendaFeeDetails().get(0);

        AccountSettingsPage accountSettingsPage = new AccountSettingsPage();

        EventListPage eventListPage = accountSettingsPage.clickEvents();

        testCaseData = testNGTestCase.getTestCaseDataFromExcelFile(this.getClass().getName(), "CreateEventsData");
        testData = new TestData(testCaseData.getTestCases());
        Event event = testData.getEventData().get(0);

        reporter.info("--------" + eventCode1);

        eventListPage.searchEvent(eventCode1);
        EventDashboardPage eventDashboardPage = eventListPage.selectSearchedEvent();

        EventOverviewPage eventOverviewPage = eventDashboardPage.clickEventSetupFromPlanMenu();
        AgendaPage agendaPage = eventOverviewPage.clickAgendaInLeftPanel();

        EventDetailsPage eventDetailsPage = agendaPage.clickDetailsTab();
        eventDetailsPage.updateEventDetails(event, 5);

        eventDetailsPage.clickAgendaInLeftPanel();
        ScheduledPage scheduledPage = agendaPage.clickOnScheduledTab();

        if (accountCode == null || "".equals(accountCode.trim())) {
            agendaPage = scheduledPage.clickAddNewAgendaButton();
            agendaPage.fillScheduledAgendaDetails(agendaData);

            agendaPage.fillScheduledAndMerchandiseFeeDetails(agendaFeeDetails);
            agendaPage.enterRegistrationFeeDetails(agendaFeeDetails, 1);
            agendaPage.clickSaveButton();

        } else {
            scheduledPage.searchCustomAgendaList(agendaData.getAgendaName());
            List<String> actualFiltered = scheduledPage.getAgendaListNames();
            if (!scheduledPage.isValueFiltered(actualFiltered, agendaData.getAgendaName())) {
                agendaPage = scheduledPage.clickAddNewAgendaButton();
                agendaPage.fillScheduledAgendaDetails(agendaData);

                agendaPage.fillScheduledAndMerchandiseFeeDetails(agendaFeeDetails);
                agendaPage.enterRegistrationFeeDetails(agendaFeeDetails, 1);
                agendaPage.clickSaveButton();
            }
        }
        EventOptionsPage eventOptionsPage = eventDetailsPage.clickConfigureFromPlanDropdown();
        eventOptionsPage.unCheckDoubleBookingSpeaker();
        eventOptionsPage.enableRegistrationGroupAsPromoCode();

    }

}
