package com.yudu.rest.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author rjm
 */
public class RequestBuilderExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private Component component;

    public RequestBuilderExceptionHandler(Component component) {
        this.component = component;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        JOptionPane.showMessageDialog(component, ExceptionUtils.getStackTrace(e));
    }
}
