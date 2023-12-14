package Utilities;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
/**
 *
 */
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;

public class RestAssuredUtil  {

    RequestSpecification spec;

    public RestAssuredUtil(String baseURL) {
        // TODO Auto-generated constructor stub
        RestAssured.baseURI = baseURL;
        spec = new RequestSpecBuilder().setBaseUri(baseURL).build();

    }

    /**
     * Sends a GET request to the specified endpoint and validates the response code.
     *
     * @param endPoint the endpoint to send the request to
     * @param responseCode the expected response code
     * @return the response body as a string
     * @throws Exception if an error occurs while sending the request
     */
    public String getRequest(String endPoint, int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : GET");
        Response response = RestAssured.given().spec(spec).get(endPoint);
        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());
        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());
        return response.getBody().asString();
    }

    public static void logInfo(String description) {
        ExtentReportUtil.logInfo(description);
    }

    /**
     * Sends a GET request to the specified endpoint with the given query parameters and validates the response code.
     * @param endPoint the endpoint to send the request to
     * @param queryParam the query parameters to include in the request
     * @param responseCode the expected response code
     * @return the response body as a string
     * @throws Exception if an error occurs while sending the request or validating the response
     */
    public String getRequest(String endPoint, Map<String, String> queryParam, int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : GET");
        logInfo("Query Param : " + queryParam);
        Response response = RestAssured.given().spec(spec).queryParams(queryParam).get(endPoint);
        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a GET request to the specified endpoint with the given query parameters and headers.
     *
     * @param endPoint The endpoint to send the request to.
     * @param queryParam The query parameters to include in the request.
     * @param headers The headers to include in the request.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String getRequest(String endPoint, Map<String, String> queryParam, Map<String, String> headers,
                             int responseCode) throws Exception {
        Response response = null;
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : GET");
        if (queryParam != null && headers != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request Headers : " + headers);
            response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).get(endPoint);
        } else if (queryParam != null) {
            logInfo("Query Param : " + queryParam);
            response = RestAssured.given().spec(spec).queryParams(queryParam).get(endPoint);
        } else {
            logInfo("Request Headers : " + headers);
            response = RestAssured.given().spec(spec).headers(headers).get(endPoint);
        }
        //Temp fix
//		for(int i=0;i<2;i++) {
//			if (response.getStatusCode() != responseCode) {
//				TimeUnit.SECONDS.sleep(10);
//				logInfo("API Hitting Attempt::"+(i+2));
//				if (queryParam != null && headers != null) {
//					response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).get(endPoint);
//				} else if (queryParam != null) {
//					response = RestAssured.given().spec(spec).queryParams(queryParam).get(endPoint);
//				} else {
//					response = RestAssured.given().spec(spec).headers(headers).get(endPoint);
//				}
//			}
//			else{
//				break;
//			}
//		}
        logInfo(response.getStatusCode() + " " +response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());
        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a GET request to the specified endpoint with the given headers and returns the response body as a string.
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If the response code does not match the expected response code.
     */
    public String getRequestWoLogs(String endPoint, Map<String, String> headers, int responseCode) throws Exception {
        Response response = null;
        response = RestAssured.given().spec(spec).headers(headers).get(endPoint);
        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());
        return response.getBody().asString();
    }

    /**
     * Sends a GET request to the specified endpoint with the given query parameters and headers.
     *
     * @param endPoint The endpoint to send the request to.
     * @param queryParam The query parameters to include in the request.
     * @param headers The headers to include in the request.
     * @return The response received from the server.
     * @throws Exception If an error occurs while sending the request.
     */
    public Response getRequest(String endPoint, Map<String, String> queryParam, Map<String, String> headers)
            throws Exception {
        Response response = null;
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : GET");
        if (queryParam != null && headers != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request Headers : " + headers);
            response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).get(endPoint);
        } else if (queryParam != null) {
            logInfo("Query Param : " + queryParam);
            response = RestAssured.given().spec(spec).queryParams(queryParam).get(endPoint);
        } else {
            logInfo("Request Headers : " + headers);
            response = RestAssured.given().spec(spec).headers(headers).get(endPoint);
        }
        logInfo(response.getStatusCode() + response.getBody().asString());
        logInfo("Response : " + response.getBody().asString());

        return response;

    }

    /**
     * Sends a POST request to the specified endpoint with the given request body and validates the response code.
     * @param endPoint the endpoint to send the request to
     * @param body the request body as a JSONObject
     * @param responseCode the expected response code
     * @return the response body as a string
     * @throws Exception if an error occurs while sending the request or validating the response
     */
    public String postRequest(String endPoint, JSONObject body, int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).body(body.toString()).post(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a POST request to the specified endpoint with the given headers and body, and validates the response code.
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param body The body of the request in JSON format.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request or validating the response.
     */
    public String postRequest(String endPoint, Map<String, String> headers, JSONObject body, int responseCode)
            throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        logInfo("Headers : " + headers.toString());
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).post(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a POST request to the specified endpoint with the given headers and body, and validates the response code.
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param body The body of the request.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String postRequest(String endPoint, Map<String, String> headers, Object body, int responseCode)
            throws Exception {
        String s = RestAssured.baseURI + endPoint;
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        logInfo("Headers : " + headers.toString());
        // report.logInfo("Request body : "+body.toString());
        Response response = RestAssured.given().headers(headers).body(body).log().all().post(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }
    /**
     * Sends a POST request to the specified endpoint with the given headers, query parameters, request body and expected response code.
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param queryParam The query parameters to include in the request.
     * @param body The request body to include in the request.
     * @param responseCode The expected response code for the request.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String postRequest(String endPoint, Map<String, String> headers, Map<String, String> queryParam, Object body, int responseCode)
            throws Exception {
        String s = RestAssured.baseURI + endPoint;
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        logInfo("Headers : " + headers.toString());
        logInfo("Headers : " + queryParam.toString());
        logInfo("Request body : " + body.toString());
        // report.logInfo("Request body : "+body.toString());
        Response response = RestAssured.given().headers(headers).queryParams(queryParam).body(body).log().all().post(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a POST request to the specified endpoint with the given headers and body.
     * Retries the request up to two times if the response code does not match the expected code.
     * Logs the request URL, method, headers, and body, as well as the response status code and body.
     * Validates that the response status code matches the expected code and throws an exception if it does not.
     * @param endPoint the endpoint to send the request to
     * @param headers the headers to include in the request
     * @param body the body of the request
     * @param responseCode the expected response code
     * @return the response body as a string
     * @throws Exception if the response status code does not match the expected code
     */
    public String postRequest(String endPoint, Map<String, String> headers, String body, int responseCode)
            throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body).post(endPoint);
        //temp fix
        for (int i = 0; i < 2; i++) {
            if (response.getStatusCode() != responseCode) {
                logInfo("API Hitting Attempt::"+(i+2));
                TimeUnit.SECONDS.sleep(10);
                response = RestAssured.given().spec(spec).headers(headers).body(body).post(endPoint);
            }
            else{
                break;
            }
        }
        logInfo(response.getStatusCode() + response.asString());
        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());
        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());
        return response.getBody().asString();
    }

    /**
     * Sends a POST request to the specified endpoint with the given headers and body, without performing an assertion on the response code.
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param body The body of the request.
     * @param responseCode The expected response code.
     * @return A boolean indicating whether the actual response code matches the expected response code.
     * @throws Exception If an error occurs while sending the request.
     */
    public boolean postRequestWOAssert(String endPoint, Map<String, String> headers, String body, int responseCode)
            throws Exception {
        boolean flag = false;
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body).post(endPoint);
        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        if (response.getStatusCode() == responseCode) {
            flag = true;
        }

        return flag;
    }

    /**
     * Sends a POST request to the specified endpoint with the given headers and body.
     *
     * @param endPoint the endpoint to send the request to
     * @param headers the headers to include in the request
     * @param body the body of the request
     * @return the response received from the server
     * @throws Exception if an error occurs while sending the request
     */
    public Response postRequest(String endPoint, Map<String, String> headers, String body) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body).post(endPoint);
        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());

        return response;
    }

    /**
     * Sends a POST request to the specified endpoint with the given headers and body, and returns the response body as a string.
     *
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param body The body of the request.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If the response code does not match the expected response code.
     */
    public String jwtToken(String endPoint, Map<String, String> headers, String body, int responseCode)
            throws Exception {
        Response response = RestAssured.given().spec(spec).headers(headers).body(body).post(endPoint);
        logInfo(response.getStatusCode() + response.asString());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

//	public String postRequest(String endPoint, Map<String, String> queryParam, Map<String, String> headers, Object body,
//			int responseCode) throws Exception {
//		logInfo("Request URL : " + RestAssured.baseURI + endPoint);
//		logInfo("Request Method : POST");
//		Response response;
//		if (queryParam != null && headers != null) {
//			logInfo("Query Param : " + queryParam);
//			logInfo("Request Headers : " + headers);
//			logInfo("Request body : " + body.toString());
//			response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).body(body.toString())
//					.post(endPoint);
//		} else if (queryParam == null && headers == null) {
//			logInfo("Request body : " + body.toString());
//			response = RestAssured.given().spec(spec).body(body.toString()).post(endPoint);
//		} else if (queryParam != null) {
//			logInfo("Query Param : " + queryParam);
//			logInfo("Request body : " + body.toString());
//			response = RestAssured.given().spec(spec).queryParams(queryParam).body(body.toString()).post(endPoint);
//		} else {
//			logInfo("Request Headers : " + headers);
//			logInfo("Request body : " + body.toString());
//			response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).post(endPoint);
//		}
//
//		logInfo("Response : " + response.getBody().asString());
//		logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());
//
//		Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());
//
//		return response.getBody().asString();
//	}
//
    /**
     * Sends a POST request to the specified endpoint with the given query parameters, headers, body and expected response code.
     *
     * @param endPoint the endpoint URL to send the request to
     * @param queryParam the query parameters to include in the request (can be null)
     * @param headers the headers to include in the request (can be null)
     * @param body the body of the request
     * @param responseCode the expected response code
     * @return the response body as a string
     * @throws Exception if an error occurs while sending the request
     */
    public String postRequest(String endPoint, Map<String, String> queryParam, Map<String, String> headers, String body,
                              int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        Response response;
        if (queryParam != null && headers != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + body.toString());
            response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).body(body.toString())
                    .post(endPoint);
        } else if (queryParam == null && headers == null) {
            logInfo("Request body : " + body.toString());
            response = RestAssured.given().spec(spec).body(body.toString()).post(endPoint);
        } else if (queryParam != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request body : " + body.toString());
            response = RestAssured.given().spec(spec).queryParams(queryParam).body(body.toString()).post(endPoint);
        } else {
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + body.toString());
            response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).post(endPoint);
        }

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    public String postRequestForMC(String endPoint, Map<String, String> queryParam, Map<String, String> headers, Map<String, String> files,
                                   int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        Response response;
        logInfo("Query Param : " + queryParam);
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + files.toString());

        response = RestAssured.given().queryParams(queryParam).headers(headers)
                .multiPart("workFlowRequest", files.get("workFlowRequest"))
                .multiPart("supportingDoc", new File(files.get("supportingDoc")), "text/csv")
                .multiPart("multipartFiles", new File(files.get("multipartFiles")), "text/csv")
                .log().all()
                .post(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    public String postRequestWoMC(String endPoint, Map<String, String> queryParam, Map<String, String> headers, Map<String, String> files,
                                  int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        Response response;
        logInfo("Query Param : " + queryParam);
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + files.toString());

        response = RestAssured.given().queryParams(queryParam).headers(headers)
                .multiPart("uploadFile", new File(files.get("multipartFiles")), "text/csv")
                .log().all()
                .post(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }


    /**
     * Sends a POST request to the specified endpoint with the given query parameters and headers.
     *
     * @param endPoint the endpoint to send the request to
     * @param queryParam the query parameters to include in the request
     * @param headers the headers to include in the request
     * @param responseCode the expected response code for the request
     * @return the response body as a string
     * @throws Exception if an error occurs while sending the request
     */
    public String postRequest(String endPoint, Map<String, String> queryParam, Map<String, String> headers,
                              int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        Response response;
        JSONObject body = new JSONObject();
        if (queryParam != null && headers != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request Headers : " + headers);
            response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).body(body.toString())
                    .post(endPoint);
        } else if (queryParam != null) {
            logInfo("Query Param : " + queryParam);
            response = RestAssured.given().spec(spec).queryParams(queryParam).body(body.toString()).post(endPoint);
        } else {
            logInfo("Request Headers : " + headers);
            response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).post(endPoint);
        }
        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());
        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());
        System.out.println(response.getBody().asString());
        return response.getBody().asString();
    }

    /**
     * Sends a POST request with form data to the specified endpoint and validates the response code.
     *
     * @param endPoint The endpoint URL to send the request to.
     * @param queryParam The query parameters to include in the request URL.
     * @param headers The headers to include in the request.
     * @param bodyFormData The form data to include in the request body.
     * @param responseCode The expected response code to validate against the actual response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String postRequest_FormData(String endPoint, Map<String, String> queryParam, Map<String, String> headers,
                                       Map<String, File> bodyFormData, int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        Response response;
        if (queryParam != null && headers != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + bodyFormData);
            response = RestAssured.given().spec(spec).params(bodyFormData).queryParams(queryParam).headers(headers)
                    .body(bodyFormData.toString()).post(endPoint);
        } else if (queryParam != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request body : " + bodyFormData);
            response = RestAssured.given().spec(spec).params(bodyFormData).queryParams(queryParam)
                    .body(bodyFormData.toString()).post(endPoint);
        } else {
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + bodyFormData);
            response = RestAssured.given().spec(spec).params(bodyFormData).headers(headers)
                    .body(bodyFormData.toString()).post(endPoint);
        }

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a POST request with a file upload to the specified endpoint using RestAssured library.
     *
     * @param endPoint The endpoint to send the request to.
     * @param queryParam The query parameters to include in the request URL.
     * @param headers The headers to include in the request.
     * @param filePath The path of the file to upload.
     * @param responseCode The expected HTTP response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String postRequestUploadFile(String endPoint, Map<String, String> queryParam, Map<String, String> headers,
                                        String filePath, int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        Response response;
        if (queryParam != null && headers != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + filePath);
            response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers)
                    .multiPart("uploadFile", new File(filePath)).post(endPoint);
        } else if (queryParam != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request body : " + filePath);
            response = RestAssured.given().spec(spec).queryParams(queryParam)
                    .multiPart("uploadFile", new File(filePath)).post(endPoint);
        } else {
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + filePath);
            response = RestAssured.given().spec(spec).headers(headers).multiPart("uploadFile", new File(filePath))
                    .post(endPoint);
        }

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());
        Thread.sleep(2000);
        return response.getBody().asString();
    }

    /**
     * Sends a POST request to upload a file to the specified endpoint with the given headers and file map.
     *
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param fileMap The map containing the file type and file path.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String postRequestUploadFile(String endPoint, Map<String, String> headers, Map<String, String> fileMap,
                                        int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        String fileType = fileMap.get("UrlKey");
        String filePath = fileMap.get("FilePath");

        logInfo("filePath : "+filePath);
        logInfo("fileType : "+fileType);

        Response response;
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + filePath);
        response = RestAssured.given().spec(spec).headers(headers).multiPart(fileType, new File(filePath))
                .post(endPoint);

        logInfo("Response : " + response.getStatusCode() +" - "+ response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a PUT request to the specified endpoint with the given headers and body, and validates the response code.
     *
     * @param endPoint The endpoint URL to send the request to.
     * @param headers The headers to include in the request.
     * @param body The JSON object to include in the request body.
     * @param responseCode The expected response code to validate against the actual response code.
     * @return The response body as a string.
     * @throws Exception If there is an error sending the request or validating the response code.
     */
    public String putRequest(String endPoint, Map<String, String> headers, JSONObject body, int responseCode)
            throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PUT");
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).put(endPoint);
        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a PUT request to the specified endpoint with the given headers and body, and validates the response code.
     *
     * @param endPoint The endpoint URL to send the request to.
     * @param headers The headers to include in the request.
     * @param body The body of the request.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String putRequest(String endPoint, Map<String, String> headers, String body, int responseCode)
            throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PUT");
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body).put(endPoint);
        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a PUT request to the specified endpoint with the given query parameters, headers, body and expected response code.
     *
     * @param endPoint The endpoint to send the request to.
     * @param queryParam The query parameters to include in the request.
     * @param headers The headers to include in the request.
     * @param body The body of the request.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String putRequest(String endPoint, Map<String, String> queryParam, Map<String, String> headers, String body,
                             int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PUT");
        Response response;
        if (queryParam != null && headers != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + body.toString());
            response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).body(body).put(endPoint);
        } else if (queryParam != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request body : " + body.toString());
            response = RestAssured.given().spec(spec).queryParams(queryParam).body(body).put(endPoint);
        } else {
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + body.toString());
            response = RestAssured.given().spec(spec).headers(headers).body(body).put(endPoint);
        }
        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a PUT request to the specified endpoint with the given query parameters and headers.
     *
     * @param endPoint The endpoint to send the request to.
     * @param queryParam The query parameters to include in the request.
     * @param headers The headers to include in the request.
     * @param responseCode The expected response code for the request.
     * @return The response body as a string.
     * @throws Exception If there is an error sending the request.
     */
    public String putRequest(String endPoint, Map<String, String> queryParam, Map<String, String> headers,
                             int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PUT");
        Response response;
        JSONObject body = new JSONObject();
        if (queryParam != null && headers != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request Headers : " + headers);
            response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).body(body.toString())
                    .put(endPoint);
        } else if (queryParam != null) {
            logInfo("Query Param : " + queryParam);
            response = RestAssured.given().spec(spec).queryParams(queryParam).body(body.toString()).put(endPoint);
        } else {
            logInfo("Request Headers : " + headers);
            response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).put(endPoint);
        }
        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a DELETE request to the specified endpoint with the given headers and validates the response code.
     *
     * @param endPoint The endpoint URL to send the DELETE request to.
     * @param headers The headers to include in the request.
     * @param responseCode The expected response code to validate against the actual response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request or validating the response code.
     */
    public String deleteRequest(String endPoint, Map<String, String> headers, int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : DELETE");
        logInfo("Request Headers : " + headers);
        Response response = RestAssured.given().spec(spec).headers(headers).delete(endPoint);

        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a DELETE request to the specified endpoint with the given headers and query parameters.
     * Validates the response code against the expected response code.
     *
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param queryParam The query parameters to include in the request.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String deleteRequest(String endPoint, Map<String, String> headers, Map<String, String> queryParam,
                                int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : DELETE");
        logInfo("Query Param : " + queryParam);
        logInfo("Request Headers : " + headers);
        Response response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).delete(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a PATCH request to the specified endpoint with the given headers and body, and validates the response code.
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param body The body of the request in JSON format.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If there is an error sending the request or validating the response.
     */
    public String patchRequest(String endPoint, Map<String, String> headers, JSONObject body, int responseCode)
            throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PATCH");
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).patch(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Updates the value of a key in a JSON object recursively.
     *
     * @param obj1      the JSON object to update
     * @param keyString the key to update
     * @param newValue  the new value to set for the key
     * @return the updated JSON object
     * @throws Exception if there is an error updating the JSON object
     */
    public JSONObject updateJson(JSONObject obj1, String keyString, String newValue) throws Exception {
        // get the keys of json object
        Iterator iterator = obj1.keys();
        String key = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            // if the key is a string, then update the value
            if ((obj1.optJSONArray(key) == null) && (obj1.optJSONObject(key) == null)) {
                if ((key.equals(keyString))) {
                    // put new value
                    obj1.put(key, newValue);
                    return obj1;
                }
            }

            // if it's jsonobject
            if (obj1.optJSONObject(key) != null) {
                updateJson(obj1.getJSONObject(key), keyString, newValue);
            }

            // if it's jsonarray
            if (obj1.optJSONArray(key) != null) {
                JSONArray jArray = obj1.getJSONArray(key);
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        updateJson(jArray.getJSONObject(i), keyString, newValue);
                    } catch (Exception e) {
                        // TODO: handle exception
                        break;
                    }

                }
            }
        }
        return obj1;
    }

    /**
     * Sends a PUT request to the specified endpoint with the given query parameters, headers, and body.
     * Validates the response code and returns the response body as a string.
     *
     * @param endPoint The endpoint URL to send the request to.
     * @param queryParam The query parameters to include in the request URL.
     * @param headers The headers to include in the request.
     * @param body The body of the request.
     * @param responseCode The expected response code to validate against the actual response code.
     * @return The response body as a string.
     * @throws Exception If there is an error sending the request or validating the response code.
     */
    public String putRequest(String endPoint, Map<String, String> queryParam, Map<String, String> headers,
                             Map<String, String> body, int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PUT");
        Response response;
        if (queryParam != null && headers != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + body);
            response = RestAssured.given().spec(spec).queryParams(queryParam).headers(headers).body(body).put(endPoint);
        } else if (queryParam != null) {
            logInfo("Query Param : " + queryParam);
            logInfo("Request body : " + body);
            response = RestAssured.given().spec(spec).queryParams(queryParam).body(body).put(endPoint);
        } else {
            logInfo("Request Headers : " + headers);
            logInfo("Request body : " + body);
            response = RestAssured.given().spec(spec).headers(headers).body(body).put(endPoint);
        }
        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a PUT request to the specified endpoint with the given headers and body.
     *
     * @param endPoint the endpoint to send the request to
     * @param headers the headers to include in the request
     * @param body the body of the request
     * @return the response received from the server
     * @throws Exception if an error occurs while sending the request
     */
    public Response putRequest(String endPoint, Map<String, String> headers, String body) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PUT");
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().headers(headers).body(body).put(endPoint);
        logInfo(response.getStatusCode() + response.asString());

        logInfo("Response : " + response.getBody().asString());

        return response;
    }

    /**
     * Sends a POST request with form data to the specified endpoint using RestAssured library.
     *
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param bodyFormData The form data to include in the request body.
     * @param responseCode The expected HTTP response code.
     * @return The response body as a string.
     * @throws Exception If an error occurs while sending the request.
     */
    public String postRequestWithFormData(String endPoint,  Map<String, String> headers,
                                          Map<String, String> bodyFormData, int responseCode) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        Response response;
        logInfo("Request Headers : " + headers);
        logInfo("Request body : " + bodyFormData);
        response = RestAssured.given().spec(spec).params(bodyFormData).headers(headers).post(endPoint);
        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a PUT request to the specified endpoint with the given headers and request body.
     * Logs the request URL, method, headers, and body.
     * Returns the response received from the endpoint.
     *
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param body The request body to include in the request.
     * @return The response received from the endpoint.
     * @throws Exception If an error occurs while sending the request.
     */
    public Response putRequest(String endPoint, Map<String, String> headers, JSONObject body)
            throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PUT");
        logInfo("Headers : " + headers.toString());
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).put(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(" expected response code validation with actual " + response.getStatusCode());

        //Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response;
    }

    /**
     * Sends a PUT request to the specified endpoint with the given headers and JSON body.
     * Validates the response code against the expected response code.
     *
     * @param endPoint The endpoint to send the request to.
     * @param headers The headers to include in the request.
     * @param body The JSON body to include in the request.
     * @param responseCode The expected response code.
     * @return The response body as a string.
     * @throws Exception If there is an error sending the request or validating the response code.
     */
    public String putRequest1(String endPoint, Map<String, String> headers, JSONObject body,int responseCode)
            throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PUT");
        logInfo("Headers : " + headers.toString());
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).put(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(" expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }

    /**
     * Sends a POST request to the specified endpoint with the given headers and request body.
     *
     * @param endPoint the endpoint to send the request to
     * @param headers the headers to include in the request
     * @param body the request body as a JSONObject
     * @return the response received from the server
     */
    public Response postRequest(String endPoint, Map<String, String> headers, JSONObject body) {

        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        logInfo("Headers : " + headers.toString());
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().spec(spec).headers(headers).body(body.toString()).post(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(" expected response code validation with actual " + response.getStatusCode());

        //Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response;
    }
    /**
     * Sends a PUT request to the specified endpoint with the given headers, query parameters, request body and expected response code.
     * @param endPoint The endpoint URL to send the request to.
     * @param headers The headers to include in the request.
     * @param queryParam The query parameters to include in the request.
     * @param body The request body to include in the request.
     * @param responseCode The expected response code to validate against the actual response code.
     * @return The response body as a string.
     * @throws Exception If there is an error sending the request or validating the response code.
     */
    public String putRequest(String endPoint, Map<String, String> headers, Map<String, String> queryParam, Object body, int responseCode)
            throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : PUT");
        logInfo("Headers : " + headers.toString());
        logInfo("Params : " + queryParam.toString());
        logInfo("Request body : " + body.toString());
        // report.logInfo("Request body : "+body.toString());
        Response response = RestAssured.given().headers(headers).queryParams(queryParam).body(body).log().all().put(endPoint);

        logInfo("Response : " + response.getBody().asString());
        logInfo(responseCode + " expected response code validation with actual " + response.getStatusCode());

        Assert.assertTrue(response.getStatusCode() == responseCode, response.getBody().asString());

        return response.getBody().asString();
    }
    public Response postRequest(String endPoint, Map<String, String> headers, Map<String, String> queryParam, String body) throws Exception {
        logInfo("Request URL : " + RestAssured.baseURI + endPoint);
        logInfo("Request Method : POST");
        logInfo("Request Headers : " + headers);
        logInfo("Request Params : " + queryParam);
        logInfo("Request body : " + body.toString());
        Response response = RestAssured.given().headers(headers).queryParams(queryParam).body(body).log().all().post(endPoint);
        logInfo(response.getStatusCode() + response.asString());
        logInfo("Response : " + response.getBody().asString());
        return response;
    }

    public List<String[]> convertCSVResponse(String csvString) {
        // Initialize a list to store the rows
        List<String[]> rows = new ArrayList<>();

        // Create a Scanner to read from the CSV string
        try (Scanner scanner = new Scanner(new StringReader(csvString))) {
            // Read the header line
            String headerLine = scanner.nextLine();
            String[] headers = headerLine.split(", ");

            // Read each data row
            while (scanner.hasNextLine()) {
                String dataLine = scanner.nextLine();
                String[] rowData = dataLine.split(", ");
                rows.add(rowData);
            }

            // Print the resulting list of arrays
            for (String[] row : rows) {
                System.out.println(Arrays.toString(row));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }
}
