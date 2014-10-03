package com.yudu.rest.request;

import com.yudu.rest.exception.RequestBuilderException;
import org.apache.http.HttpMessage;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * @author rjm
 */
public class RestRequest extends Headered {

    private String requestURI;
    private String stringToSign;
    private String signature;
    private HttpUriRequest request;

    public RestRequest(String requestURI, String authentication, String stringToSign, String signature,
                       String requestBody, HttpRequestBuilder requestBuilder) {
        this.requestURI = requestURI;
        this.stringToSign = stringToSign;
        this.signature = signature;
        try {
            this.request = requestBuilder.buildRequest(requestURI, authentication, signature, requestBody);
        } catch (IOException e) {
            throw new RequestBuilderException("Caught IO Exception while building request.", e);
        }
    }

    public RestResponse send()
    {
        HttpClient client = HttpClientBuilder.create().build();
        try {
            return new RestResponse(client.execute(request));
        } catch (IOException e) {
            throw new RequestBuilderException("Caught IO Exception while sending request.", e);
        }
    }

    public String getRequestURI()
    {
        return requestURI;
    }

    public String getStringToSign()
    {
        return stringToSign;
    }

    public String getSignature()
    {
        return signature;
    }

    @Override
    protected HttpMessage getHttpMessage()
    {
        return request;
    }
}
