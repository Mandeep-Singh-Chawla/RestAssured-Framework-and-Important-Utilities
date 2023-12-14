package Utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.testng.Assert;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtentReportUtil{

    static String methodName;

    static Calendar cal = Calendar.getInstance();
    static SimpleDateFormat FormatDateValue = new SimpleDateFormat("MM-dd-yyyy hh-mm-ss a");

    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
    static String filePath = System.getProperty("user.dir") + "/Reports/ExtentReport.html";

    private static ExtentReports extent;
    private static ExtentHtmlReporter htmlReporter;

    public static Map<Long, String> threadToExtentTestMap = new HashMap<Long, String>();
    public static Map<String, ExtentTest> nameToTestMap = new HashMap<String, ExtentTest>();

    public static Map<Long, String> threadToExtentTestChildMap = new HashMap<Long, String>();
    public static Map<String, ExtentTest> nameToTestChildMap = new HashMap<String, ExtentTest>();

    private synchronized static ExtentReports getExtentReport() {
        if (extent == null) {
            htmlReporter = new ExtentHtmlReporter(filePath);
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
        }
        return extent;
    }

    public synchronized static ExtentTest startParent(String testName, String testDescription) {

        // if this test has already been created return
        if (!nameToTestMap.containsKey(testName)) {
            Long threadID = Thread.currentThread().getId();
            ExtentTest test = getExtentReport().createTest(testName, testDescription);

            nameToTestMap.put(testName, test);
            threadToExtentTestMap.put(threadID, testName);
        }
        return nameToTestMap.get(testName);
    }

    public synchronized static ExtentTest startChild(String parentName,String testName, String testDescription) {
        String childName = parentName+"."+testName;
        // if this test has already been created return
        if (!nameToTestChildMap.containsKey(childName)) {
            Long threadID = Thread.currentThread().getId();
            ExtentTest test = startParent(parentName).createNode(testName, testDescription);
            test.assignCategory(parentName);
            nameToTestChildMap.put(childName, test);
            threadToExtentTestChildMap.put(threadID, childName);
        }
        return nameToTestChildMap.get(childName);
    }

    public synchronized static ExtentTest startParent(String testName) {
        return startParent(testName, "");
    }

    public synchronized static ExtentTest getTest() {
        Long threadID = Thread.currentThread().getId();

        if (threadToExtentTestMap.containsKey(threadID)) {
            String testName = threadToExtentTestMap.get(threadID);
            return nameToTestMap.get(testName);
        }
        System.out.println("Class test not found to flush");
        //system log, this shouldnt happen but in this crazy times if it did happen log it.
        return null;
    }

    public synchronized static ExtentTest getChildTest() {
        Long threadID = Thread.currentThread().getId();

        if (threadToExtentTestChildMap.containsKey(threadID)) {
            String testName = threadToExtentTestChildMap.get(threadID);
            return nameToTestChildMap.get(testName);
        }
        System.out.println("Child test not found to flish");
        //system log, this shouldnt happen but in this crazy times if it did happen log it.
        return null;
    }

    public synchronized static void closeChildTest(String testName) {

        if (!testName.isEmpty()) {
            ExtentTest test = getChildTest();
            test.getExtent().flush();
        }
    }

    public synchronized static void closeTest(String testName) {

        if (!testName.isEmpty()) {
            ExtentTest test = getTest();
            test.getExtent().flush();
        }
    }

    public synchronized static void closeTest(ExtentTest test) {
        if (test != null) {
            test.getExtent().flush();
        }
    }

    public synchronized static void closeTest()  {
        ExtentTest test = getTest();
        closeTest(test);
    }

    public synchronized static void closeReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static void logTest(Status logStatus, String description, Object... b) throws IOException {
        if (logStatus.toString().equalsIgnoreCase("PASS"))
            ExtentReportUtil.getChildTest().log(logStatus, "<b><font face=\"Courier New\" color = \"green\">" + description + "</font></b>");
        else if (logStatus.toString().equalsIgnoreCase("FAIL")) {
            if (b.length > 0) {
                ExtentReportUtil.getChildTest().log(logStatus, "<b><font face=\"Courier New\" color = \"red\">" + description + "</font></b>"
                        + ExtentReportUtil.getChildTest().addScreenCaptureFromPath((String) b[0]) + (String) b[1]);
            }
            ExtentReportUtil.getChildTest().log(logStatus, "<b><font face=\"Courier New\" color = \"red\">" + description + "</font></b>"
                    + ExtentReportUtil.getChildTest().addScreenCaptureFromPath((String) b[0]) + (String) b[1]);
        } else if (logStatus.toString().equalsIgnoreCase("SKIP")) {
            ExtentReportUtil.getChildTest().log(logStatus, "<b><font face=\"Courier New\" color = \"blue\">" + description + "</font></b>"
                    + ExtentReportUtil.getChildTest().addScreenCaptureFromPath((String) b[0]) + (String) b[1]);
        }
    }

    public static void logFail(String description) throws Exception {
        // TODO Auto-generated method stub
        ExtentReportUtil.getChildTest().log(Status.FAIL, "<b><font face=\"Courier New\" color = \"red\">" + description + "</font></b>");
//        addScreenShot();
    }

    public static void logFailWOScrnsht(String description) throws Exception {
        // TODO Auto-generated method stub
        ExtentReportUtil.getChildTest().log(Status.FAIL, "<b><font face=\"Courier New\" color = \"red\">" + description + "</font></b>");
    }

    public static void logSkip(String description) {
        // TODO Auto-generated method stub
        ExtentReportUtil.getChildTest().log(Status.SKIP, "<b><font face=\"Courier New\" color = \"yellow\">" + description + "</font></b>");

    }

//    public static void addScreenShot() throws Exception {
//        // TODO Auto-generated method stub
//        ExtentReportUtil.getChildTest().addScreenCaptureFromPath(ExtentReportUtil.getScreenhot());
//        ExtentReportUtil.getChildTest().addScreenCaptureFromPath(ExtentReportUtil.getScreenhot_ByteCode());
//    }

    public static void attachLogFile(String bPath, String aPath) throws Exception {
        ExtentReportUtil.getChildTest().log(Status.PASS, "Click to view logs "+"<a href='"+bPath+"'>b</a>"+" "+"<a href='"+aPath+"'>Admin</a>");
    }

    public static void logInfo(String description) {
        // TODO Auto-generated method stub
        ExtentReportUtil.getChildTest().log(Status.INFO, "<b><font face=\"Courier New\" color = \"black\">" + description + "</font></b>");

    }

    public static void logPass(String description) {
        // TODO Auto-generated method stub
        ExtentReportUtil.getChildTest().log(Status.PASS, "<b><font face=\"Courier New\" color = \"green\">" + description + "</font></b>");

    }

    public static void logAssert(Boolean status, String description) throws Exception {
        // TODO Auto-generated method stub
        if(status) {
            ExtentReportUtil.getChildTest().log(Status.PASS, "<b><font face=\"Courier New\" color = \"green\">" + description + "</font></b>");
        }
        else {
            ExtentReportUtil.getChildTest().log(Status.FAIL, "<b><font face=\"Courier New\" color = \"red\">" + description + "</font></b>");
//			addScreenShot();
        }
        Assert.assertTrue(status, description);
    }

    public static void logAssertFalse(Boolean status, String description) throws Exception {
        // TODO Auto-generated method stub
        if(status) {
            ExtentReportUtil.getChildTest().log(Status.FAIL, "<b><font face=\"Courier New\" color = \"red\">" + description + "</font></b>");
//			addScreenShot();
        }
        else {
            ExtentReportUtil.getChildTest().log(Status.PASS, "<b><font face=\"Courier New\" color = \"green\">" + description + "</font></b>");
        }
        Assert.assertFalse(status, description);
    }

    public static void logAssert(Boolean status) throws Exception {
        // TODO Auto-generated method stub
        if(status) {
            ExtentReportUtil.getChildTest().log(Status.PASS, "<b><font face=\"Courier New\" color = \"green\">" + "Test Passed" + "</font></b>");
        }
        else {
            ExtentReportUtil.getChildTest().log(Status.FAIL, "<b><font face=\"Courier New\" color = \"red\">" + "Test Failed" + "</font></b>");
        }
        Assert.assertTrue(status);
    }

    public static void logSoftAssert(Boolean status, String description) throws Exception {
        // TODO Auto-generated method stub
        if(status) {
            ExtentReportUtil.getChildTest().log(Status.PASS, "<b><font face=\"Courier New\" color = \"green\">" + description + "</font></b>");
        }
        else {
            ExtentReportUtil.getChildTest().log(Status.FAIL, "<b><font face=\"Courier New\" color = \"red\">" + description + "</font></b>");
//			addScreenShot();
        }
    }

    public static void logAssertFailMobile(String imagePath,String msg) throws Exception {
        ExtentReportUtil.getChildTest().log(Status.FAIL, "<b><font face=\"Courier New\" color = \"red\">" + msg+ "</font></b>");
        ExtentReportUtil.getChildTest().addScreenCaptureFromPath(imagePath);
        Assert.assertTrue(false);
    }




}
