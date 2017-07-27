package com.yudu.rest.ui;

import com.yudu.rest.request.RestRequest;
import com.yudu.rest.request.RestRequestFactory;
import com.yudu.rest.request.RestResponse;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author rjm
 */
public class RequestActionHandler implements ActionListener
{
    private final UserInterface ui;

    public RequestActionHandler(UserInterface ui)
    {
        this.ui = ui;
    }

    public void actionPerformed(ActionEvent event)
    {
        RestRequestFactory factory = new RestRequestFactory(ui.getRequestDetails());
        RestRequest request = factory.createRequest();

        ui.updateRequestURI(request.getRequestURI());
        ui.updateStringToSign(request.getStringToSign());
        ui.updateSignature(request.getSignature());
        ui.updateRequestHeaders(request.getHeadersString());

        DateTime startTime = DateTime.now();
        RestResponse response = request.send();
        DateTime endTime = DateTime.now();

        ui.updateResponseCode(response.getCode());
        ui.updateResponseHeaders(response.getHeadersString());
        ui.updateResponseTime(String.format("%.2f seconds", (endTime.getMillis() - startTime.getMillis()) / 1000f));
        ui.updateResponseBody(response.getResponseBody());
    }
}
