import Utilities.DBconnectUtil;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import Utilities.*;

public class BaseTest {

    protected static PropertyFactory DBpropertyFactory = Propertyfile.getDBProperty();

    protected static String workingDirPath = null;
//    protected static Cookie cookie1 =null;
//    protected static Set<Cookie> cookie=null;
    protected static List<String> tcToBeUpdated = new ArrayList<String>();
    RestAssuredUtil request=null;
    protected static String uiFlag=null, slackNotificationFlag=null;

    @BeforeSuite(alwaysRun = true)
    public void startBeforeSuiteExecution(ITestContext ctx) throws Exception {
        workingDirPath = System.getProperty("user.dir");

        BaseTest.emptyFolder(workingDirPath + "/errorScreenShot", false, ".png");
        BaseTest.emptyFolder(workingDirPath + "/data/GenericBulk/RunTimeUploadCSV", false, ".csv");

        DBconnectUtil.getInstance(DBpropertyFactory);


        uiFlag = System.getProperty("uiOpen");
        slackNotificationFlag = System.getProperty("sn");

        System.out.println("UI Open flag : "+uiFlag);
        System.out.println("Slack Notification flag : "+slackNotificationFlag);

        if(uiFlag.equalsIgnoreCase("true")){
            String username			= DBpropertyFactory.getProperty("DB_USER_ID");
            String password			= DBpropertyFactory.getProperty("DB_PASSWORD");


        }
    }

    @BeforeClass
    public void beforeClassTest(ITestContext method) throws Exception {
        ExtentReportUtil.startParent(this.getClass().getSimpleName(), this.getClass().getSimpleName());
    }

    @BeforeMethod
    public void beforeMethodTest(Method method) throws Exception {
        ExtentReportUtil.startChild(this.getClass().getSimpleName(), method.getName(), "");
    }

    @AfterClass(alwaysRun = true)
    public void initReport() throws Exception {
        ExtentReportUtil.closeTest();
        System.gc();
    }

    @AfterSuite(alwaysRun = false)
    public void stopTestExecution(ITestContext ctx) throws Exception {
        DBconnectUtil.closeConnection1();
        ExtentReportUtil.closeReport();
        BaseTest.emptyFolder(workingDirPath + "/allure-results/", false, ".json");
        SlackIntegrationUtil st = new SlackIntegrationUtil();
        if (uiFlag.equalsIgnoreCase("true")) {
            if(slackNotificationFlag.equalsIgnoreCase("true")) {
                st.sendTestExecutionReportToSlack("./Reports/ExtentReport.html","Extents Report","UI Extents");
                st.sendTestExecutionReportToSlack("./target/surefire-reports/customized-emailable-report.html","TestNG Emailable Report","UI TestNG");
            }
        }else {
            if(slackNotificationFlag.equalsIgnoreCase("true")) {
                st.sendTestExecutionReportToSlack("./Reports/ExtentReport.html","Extents Report","API Extents");
                st.sendTestExecutionReportToSlack("./target/surefire-reports/customized-emailable-report.html","TestNG Emailable Report","API TestNG");
            }
        }


    }

    @AfterMethod
    public void afterMethod(ITestResult result, Method method) throws Exception {

//        if (result.getStatus() == ITestResult.FAILURE) {
//            if ()
//                ExtentReportUtil
//                        .logFail(method.getName() + " test execution failed " + result.getThrowable().toString());
//            else
//                ExtentReportUtil.logFailWOScrnsht(
//                        method.getName() + " test execution failed " + result.getThrowable().toString());
//        }
        if (result.getStatus() == ITestResult.SKIP) {
            ExtentReportUtil.logSkip(method.getName() + " test execution skipped " + result.getThrowable());
        } else {
            ExtentReportUtil.logPass(method.getName() + " test execution passed");
        }

        ExtentReportUtil.closeChildTest(this.getClass().getSimpleName() + "." + method.getName());

    }

    public static boolean emptyFolder(String path, boolean complete, String fileType) throws  Exception
    {
        if(path == null)
            return false;

        File folder = new File(path);

        if(complete)
            return folder.delete();
        else {
            boolean success_status = false;
            File[] listOfFiles = folder.listFiles();

            if (listOfFiles==null || fileType == null)
                return false;

            for (File file : listOfFiles) {
                if (file.getPath().contains(fileType))
                    success_status = file.delete();
            }
            return success_status;
        }
    }
}
