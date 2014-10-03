package com.yudu.rest.request;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

/**
 * @author rjm
 */
public abstract class HttpRequestBuilder
{
    private static final String CONTENT_TYPE = "application/vnd.yudu+xml";

    public abstract HttpUriRequest buildRequest(String uri, String authentication, String signature, String body) throws UnsupportedEncodingException;

    public static HttpRequestBuilder get() {
        return new HttpRequestBuilder() {
            @Override
            public HttpUriRequest buildRequest(String uri, String authentication, String signature, String body) {
                HttpGet request = new HttpGet(uri);
                addAuthenticationHeaders(request, authentication, signature);
                return request;
            }
        };
    }

    public static HttpRequestBuilder put() {
        return new HttpRequestBuilder() {
            @Override
            public HttpUriRequest buildRequest(String uri, String authentication, String signature, String body) throws UnsupportedEncodingException
            {
                HttpPut request = new HttpPut(uri);
                addAuthenticationHeaders(request, authentication, signature);
                addRequestBodyAndContentType(request, body);
                return request;
            }
        };
    }

    public static HttpRequestBuilder post() {
        return new HttpRequestBuilder() {
            @Override
            public HttpUriRequest buildRequest(String uri, String authentication, String signature, String body) throws UnsupportedEncodingException
            {
                HttpPost request = new HttpPost(uri);
                addAuthenticationHeaders(request, authentication, signature);
                addRequestBodyAndContentType(request, body);
                return request;
            }
        };
    }

    public static HttpRequestBuilder delete() {
        return new HttpRequestBuilder() {
            @Override
            public HttpUriRequest buildRequest(String uri, String authentication, String signature, String body) {
                HttpDelete request = new HttpDelete(uri);
                addAuthenticationHeaders(request, authentication, signature);
                return request;
            }
        };
    }

    public static HttpRequestBuilder options() {
        return new HttpRequestBuilder() {
            @Override
            public HttpUriRequest buildRequest(String uri, String authentication, String signature, String body) {
                HttpOptions request = new HttpOptions(uri);
                addAuthenticationHeaders(request, authentication, signature);
                return request;
            }
        };
    }

    private static void addAuthenticationHeaders(HttpUriRequest request, String authentication, String signature) {
        request.addHeader("Authentication", authentication);
        request.addHeader("Signature", signature);
    }

    private static void addRequestBodyAndContentType(HttpEntityEnclosingRequest request, String body) throws UnsupportedEncodingException
    {
        request.addHeader("Content-Type", CONTENT_TYPE);
        request.setEntity(new StringEntity(body));
    }
}
