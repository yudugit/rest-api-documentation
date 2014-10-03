package com.yudu.rest.model;

/**
 * @author rjm
 */
public class RequestDetails {
    private String apiKey;
    private String sharedSecret;
    private String serviceHost;
    private String serviceBasePath;
    private String resourcePath;
    private String queryString;
    private String requestBody;
    private HttpMethod httpMethod;

    public RequestDetails(String apiKey, String sharedSecret, String serviceHost, String serviceBaseURI,
                          String resourcePath, String queryString, String requestBody, HttpMethod httpMethod) {
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        this.serviceHost = serviceHost;
        this.serviceBasePath = serviceBaseURI;
        this.httpMethod = httpMethod;
        this.resourcePath = resourcePath;
        this.queryString = queryString;
        this.requestBody = requestBody;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public String getServiceBasePath() {
        return serviceBasePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
