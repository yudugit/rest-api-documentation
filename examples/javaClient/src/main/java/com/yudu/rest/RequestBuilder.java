package com.yudu.rest;

import com.yudu.rest.ui.RequestActionHandler;
import com.yudu.rest.ui.UserInterface;

/**
 * @author rjm
 */
public class RequestBuilder
{
    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                UserInterface ui = new UserInterface();
                ui.createUI();
                ui.setUncaughtExceptionHandler();

                RequestActionHandler requestHandler = new RequestActionHandler(ui);
                ui.addRequestHandler(requestHandler);

                ui.showUI();
            }
        });
    }
}
