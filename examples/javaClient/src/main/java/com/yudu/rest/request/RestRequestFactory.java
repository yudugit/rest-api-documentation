package com.yudu.rest.request;

import com.yudu.rest.model.RequestDetails;
import com.yudu.rest.request.util.HmacCalculator;
import com.yudu.rest.request.util.QueryStringBuilder;
import com.yudu.rest.request.util.SignableStringBuilder;

/**
 * @author rjm
 */
public class RestRequestFactory {

    private RequestDetails details;

    public RestRequestFactory(RequestDetails details) {
        this.details = details;
    }

    public RestRequest createRequest() {
        String stringToSign = new SignableStringBuilder(details).stringToSign(); // build the string to sign
        String signature = HmacCalculator.calculateHmac(stringToSign, details.getSharedSecret()); // sign the string

        return new RestRequest(requestURI(), details.getApiKey(), stringToSign, signature, details.getRequestBody(), requestBuilder());
    }

    private String requestURI() {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(details.getServiceHost()) // start with the API host (e.g. https://api.yudu.com)
                .append(details.getServiceBasePath()) // add the service base path (e.g. /Yudu/services/2.0)
                .append(details.getResourcePath()) // add the resource path (e.g. /editions)
                .append('?') // start the query string
                // add the query string - this one doesn't need to be sorted but still needs a timestamp
                .append(new QueryStringBuilder(details.getQueryString()).queryStringWithTimeStamp());

        return uriBuilder.toString();
    }

    private HttpRequestBuilder requestBuilder() {
        switch (details.getHttpMethod()) {
            case GET: return HttpRequestBuilder.get();
            case PUT: return HttpRequestBuilder.put();
            case POST: return HttpRequestBuilder.post();
            case DELETE: return HttpRequestBuilder.delete();
            default: return HttpRequestBuilder.options();
        }
    }
}
