package com.yudu.rest.ui;

import com.yudu.rest.request.RestRequest;
import com.yudu.rest.request.RestRequestFactory;
import com.yudu.rest.request.RestResponse;

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

        RestResponse response = request.send();

        ui.updateResponseCode(response.getCode());
        ui.updateResponseHeaders(response.getHeadersString());
        ui.updateResponseBody(response.getResponseBody());
    }
}
