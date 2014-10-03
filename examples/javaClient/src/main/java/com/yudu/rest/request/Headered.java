package com.yudu.rest.request;

import org.apache.http.Header;
import org.apache.http.HttpMessage;

/**
 * @author rjm
 */
public abstract class Headered
{
    protected abstract HttpMessage getHttpMessage();

    public String getHeadersString() {
        Header[] headers = getHttpMessage().getAllHeaders();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            builder.append(header.getName())
                    .append(": ")
                    .append(header.getValue());

            if (i < headers.length - 1)
                builder.append('\n');
        }

        return builder.toString();
    }
}
