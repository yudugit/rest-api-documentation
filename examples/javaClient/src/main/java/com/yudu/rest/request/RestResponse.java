package com.yudu.rest.request;

import com.yudu.rest.exception.RequestBuilderException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author rjm
 */
public class RestResponse extends Headered
{

    private HttpResponse response;
    private String responseBody;

    public RestResponse(HttpResponse response) {
        this.response = response;
        this.responseBody = extractResponseBody();
    }

    private String extractResponseBody()
    {
        HttpEntity entity = response.getEntity();

        try {
            if (entity != null)
            {
                InputStream instream = entity.getContent();
                try
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line = reader.readLine();
                    while (line != null)
                    {
                        stringBuilder.append(line);
                        stringBuilder.append("\n");
                        line = reader.readLine();
                    }
                    return stringBuilder.toString();
                } finally {
                    // Closing the input stream will trigger connection release
                    instream.close();
                }
            }
        } catch (IOException e) {
            throw new RequestBuilderException("Caught IO Exception while reading response body.", e);
        }

        return "";
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getCode()
    {
        return response.getStatusLine().toString();
    }

    @Override
    protected HttpMessage getHttpMessage()
    {
        return response;
    }
}
