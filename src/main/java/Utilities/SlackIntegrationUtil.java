package Utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SlackIntegrationUtil {
    private static String webHooksURL="x";
    private static String oauthToken="y";
    private static String slackChannel="b";
    // Send test execution status to the Slack channel


    // Upload test execution reports to the Slack channel
    public void sendTestExecutionReportToSlack(String testReportPath,String fileName,String suite) throws Exception {
        String url = "https://slack.com/api/files.upload";
        try {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "multipart/form-data");
            headers.put("Authorization", "Bearer "+oauthToken);
            headers.put("Accept", "*/*");
            byte[] fileContent = FileUtils.readFileToByteArray(new File(testReportPath));
            Response response = RestAssured
                    .given()
                    .headers(headers)
                    .multiPart("file", new File(testReportPath), "multi-part/form-data")
//                    .formParam("token", oauthToken)
                    .formParam("filetype", "html")
                    .formParam("filename", fileName)
                    .formParam("title", fileName)
                    .formParam("initial_comment", "@channel \n\n Please download and analyse "+suite+" execution report")
                    .formParam("channels", slackChannel)
                    .body(fileContent)
                    .post(url)
                    .thenReturn();

            System.out.println(response.asString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  void main(String args[]) throws Exception {
        SlackIntegrationUtil st = new SlackIntegrationUtil();
//		st.sendTestExecutionStatusToSlack("Slack test Automation msg");
//		st.sendTestExecutionReportToSlack("./Reports/ExtentReport.html","");
//		st.sendTestExecutionReportToSlack("./test-output/customized-emailable-report.html","");
    }
}
