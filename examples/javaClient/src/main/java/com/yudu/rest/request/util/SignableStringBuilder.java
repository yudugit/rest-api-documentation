package com.yudu.rest.request.util;

import com.yudu.rest.model.RequestDetails;

/**
 * @author rjm
 */
public class SignableStringBuilder
{
    private RequestDetails details;

    public SignableStringBuilder(RequestDetails details) {
        this.details = details;
    }

    /**
     * @return The string to sign, based on the request details.
     */
    public String stringToSign() {
        StringBuilder signatureBuilder = new StringBuilder();

        signatureBuilder.append(details.getHttpMethod().toString()) // The request method in upper case (e.g. GET)
                .append(details.getServiceBasePath()) // The service base path (e.g. /Yudu/services/2.0)
                .append(details.getResourcePath()) // The resource path (e.g. /editions)
                .append(canonicalQueryString()) // The query string with a timestamp parameter and sorted alphabetically
                .append(details.getRequestBody()); // The request body (if there is one) including whitespace.

        return signatureBuilder.toString();
    }

    private String canonicalQueryString() {
        QueryStringBuilder queryStringBuilder = new QueryStringBuilder(details.getQueryString());
        return queryStringBuilder.canonicalQueryString();
    }
}
