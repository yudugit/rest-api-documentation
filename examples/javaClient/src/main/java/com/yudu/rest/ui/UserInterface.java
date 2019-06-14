package com.yudu.rest.ui;

import com.yudu.rest.exception.RequestBuilderExceptionHandler;
import com.yudu.rest.model.HttpMethod;
import com.yudu.rest.model.RequestDetails;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * @author rjm
 */
public class UserInterface {
    private JFrame frame;

    private JLabel apiKeyLabel;
    private JTextField apiKeyTextField;
    private JLabel sharedSecretLabel;
    private JTextField sharedSecretTextField;
    private JLabel serviceHostLabel;
    private JTextField serviceHostTextField;
    private JLabel serviceBasePathLabel;
    private JTextField serviceBasePathTextField;
    private JLabel resourcePathLabel;
    private JTextField resourcePathTextField;
    private JLabel queryStringLabel;
    private JTextField queryStringTextField;
    private JLabel requestBodyLabel;
    private JTextArea requestBodyTextArea;
    private JScrollPane requestBodyScrollPane;
    private JLabel httpMethodLabel;
    private JComboBox httpMethodComboBox;
    private JButton requestButton;

    private JLabel requestURILabel;
    private JTextArea requestURITextArea;
    private JLabel stringToSignLabel;
    private JTextArea stringToSignTextArea;
    private JScrollPane stringToSignScrollPane;
    private JLabel signatureLabel;
    private JTextArea signatureTextArea;
    private JLabel requestHeadersLabel;
    private JTextArea requestHeadersTextArea;
    private JLabel responseCodeLabel;
    private JTextArea responseCodeTextArea;
    private JLabel responseHeadersLabel;
    private JTextArea responseHeadersTextArea;
    private JLabel responseTimeLabel;
    private JTextArea responseTimeTextArea;
    private JLabel responseBodyLabel;
    private JTextArea responseBodyTextArea;
    private JScrollPane responseBodyScrollPane;

    private GroupLayout layout;

    public RequestDetails getRequestDetails() {
        return new RequestDetails(
                apiKeyTextField.getText(),
                sharedSecretTextField.getText(),
                serviceHostTextField.getText(),
                serviceBasePathTextField.getText(),
                resourcePathTextField.getText(),
                queryStringTextField.getText(),
                requestBodyTextArea.getText(),
                (HttpMethod) httpMethodComboBox.getSelectedItem()
        );
    }

    public void updateRequestURI(String responseBody) {
        requestURITextArea.setText(responseBody);
    }

    public void updateStringToSign(String responseBody) {
        stringToSignTextArea.setText(responseBody);
    }

    public void updateSignature(String responseBody) {
        signatureTextArea.setText(responseBody);
    }


    public void updateRequestHeaders(String responseBody) {
        requestHeadersTextArea.setText(responseBody);
    }

    public void updateResponseCode(String responseBody) {
        responseCodeTextArea.setText(responseBody);
    }

    public void updateResponseHeaders(String responseBody) {
        responseHeadersTextArea.setText(responseBody);
    }

    public void updateResponseTime(String responseBody) {
        responseTimeTextArea.setText(responseBody);
    }

    public void updateResponseBody(String responseBody) {
        responseBodyTextArea.setText(responseBody);
    }

    public void createUI() {
        createFrame();

        createApiKeyField();
        createSharedSecretField();
        createServiceHostField();
        createServiceBasePathField();
        createResourcePathField();
        createQueryStringField();
        createHttpMethodDropdown();
        createRequestBodyField();
        createRequestButton();

        createRequestURIField();
        createStringToSignField();
        createSignatureField();
        createRequestHeadersField();
        createResponseCodeField();
        createResponseHeadersField();
        createResponseTimeField();
        createResponseBodyField();

        setLayout();
    }

    public void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new RequestBuilderExceptionHandler(frame));
    }

    public void addRequestHandler(ActionListener listener) {
        requestButton.addActionListener(listener);
    }

    public void showUI() {
        frame.pack();
        frame.setVisible(true);
    }

    private void createFrame()
    {
        frame = new JFrame("REST Request Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createApiKeyField() {
        apiKeyLabel = new JLabel("API Key");
        apiKeyTextField = new JTextField();
    }

    private void createSharedSecretField() {
        sharedSecretLabel = new JLabel("Shared Secret");
        sharedSecretTextField = new JTextField();
    }

    private void createServiceHostField() {
        serviceHostLabel = new JLabel("Service Host");
        serviceHostTextField = new JTextField();
        serviceHostTextField.setText("https://api.yudu.com");
    }

    private void createServiceBasePathField() {
        serviceBasePathLabel = new JLabel("Service Base Path");
        serviceBasePathTextField = new JTextField();
        serviceBasePathTextField.setText("/Yudu/services/2.0");
    }

    private void createResourcePathField() {
        resourcePathLabel = new JLabel("Resource Path");
        resourcePathTextField = new JTextField();
        resourcePathTextField.setText("/");
    }

    private void createQueryStringField() {
        queryStringLabel = new JLabel("Query String");
        queryStringTextField = new JTextField();
    }

    private void createHttpMethodDropdown() {
        httpMethodLabel = new JLabel("HTTP Method");
        sharedSecretTextField = new JTextField();
        httpMethodComboBox = new JComboBox(HttpMethod.values());
    }

    private void createRequestBodyField() {
        requestBodyLabel = new JLabel("Request Body");
        requestBodyTextArea = new JTextArea(5, 60);
        requestBodyScrollPane = new JScrollPane(requestBodyTextArea);
    }

    private void createRequestButton()
    {
        requestButton = new JButton("Send Request");
    }

    private void createRequestURIField()
    {
        requestURILabel = new JLabel("Request URI");
        requestURITextArea = new JTextArea(1, 60);
        requestURITextArea.setEditable(false);
    }

    private void createStringToSignField()
    {
        stringToSignLabel = new JLabel("String To Sign");
        stringToSignTextArea = new JTextArea(1, 60);
        stringToSignScrollPane = new JScrollPane(stringToSignTextArea);
        stringToSignTextArea.setEditable(false);
    }

    private void createSignatureField()
    {
        signatureLabel = new JLabel("Signature");
        signatureTextArea = new JTextArea(1, 60);
        signatureTextArea.setEditable(false);
    }

    private void createRequestHeadersField()
    {
        requestHeadersLabel = new JLabel("Request Headers");
        requestHeadersTextArea = new JTextArea(3, 60);
        requestHeadersTextArea.setEditable(false);
    }

    private void createResponseCodeField()
    {
        responseCodeLabel = new JLabel("Response Code");
        responseCodeTextArea = new JTextArea(1, 60);
        responseCodeTextArea.setEditable(false);
    }

    private void createResponseHeadersField()
    {
        responseHeadersLabel = new JLabel("Response Headers");
        responseHeadersTextArea = new JTextArea(5, 60);
        responseHeadersTextArea.setEditable(false);
    }

    private void createResponseTimeField()
    {
        responseTimeLabel = new JLabel("Response Time");
        responseTimeTextArea = new JTextArea(1, 60);
        responseTimeTextArea.setEditable(false);
    }

    private void createResponseBodyField()
    {
        responseBodyLabel = new JLabel("Response Body");
        responseBodyTextArea = new JTextArea(15, 60);
        responseBodyScrollPane = new JScrollPane(responseBodyTextArea);
        responseBodyTextArea.setEditable(false);
    }

    private void setLayout()
    {
        layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(sequence(apiKeyLabel, apiKeyTextField))
                .addGroup(sequence(sharedSecretLabel, sharedSecretTextField))
                .addGroup(sequence(serviceHostLabel, serviceHostTextField))
                .addGroup(sequence(serviceBasePathLabel, serviceBasePathTextField))
                .addGroup(sequence(resourcePathLabel, resourcePathTextField))
                .addGroup(sequence(queryStringLabel, queryStringTextField))
                .addGroup(sequence(httpMethodLabel, httpMethodComboBox))
                .addGroup(sequence(requestBodyLabel, requestBodyScrollPane))
                .addComponent(requestButton)
                .addGroup(sequence(requestURILabel, requestURITextArea))
                .addGroup(sequence(stringToSignLabel, stringToSignScrollPane))
                .addGroup(sequence(signatureLabel, signatureTextArea))
                .addGroup(sequence(requestHeadersLabel, requestHeadersTextArea))
                .addGroup(sequence(responseCodeLabel, responseCodeTextArea))
                .addGroup(sequence(responseHeadersLabel, responseHeadersTextArea))
                .addGroup(sequence(responseTimeLabel, responseTimeTextArea))
                .addGroup(sequence(responseBodyLabel, responseBodyScrollPane)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(parallelBaseline(apiKeyLabel, apiKeyTextField))
                .addGroup(parallelBaseline(sharedSecretLabel, sharedSecretTextField))
                .addGroup(parallelBaseline(serviceHostLabel, serviceHostTextField))
                .addGroup(parallelBaseline(serviceBasePathLabel, serviceBasePathTextField))
                .addGroup(parallelBaseline(resourcePathLabel, resourcePathTextField))
                .addGroup(parallelBaseline(queryStringLabel, queryStringTextField))
                .addGroup(parallelBaseline(httpMethodLabel, httpMethodComboBox))
                .addGroup(parallelBaseline(requestBodyLabel, requestBodyScrollPane))
                .addComponent(requestButton)
                .addGroup(parallelBaseline(requestURILabel, requestURITextArea))
                .addGroup(parallelBaseline(stringToSignLabel, stringToSignScrollPane))
                .addGroup(parallelBaseline(signatureLabel, signatureTextArea))
                .addGroup(parallelBaseline(requestHeadersLabel, requestHeadersTextArea))
                .addGroup(parallelBaseline(responseCodeLabel, responseCodeTextArea))
                .addGroup(parallelBaseline(responseHeadersLabel, responseHeadersTextArea))
                .addGroup(parallelBaseline(responseTimeLabel, responseTimeTextArea))
                .addGroup(parallelBaseline(responseBodyLabel, responseBodyScrollPane)));
    }

    private GroupLayout.Group sequence(JComponent... components) {
        GroupLayout.Group group = layout.createSequentialGroup();
        for (JComponent component : components) {
            group.addComponent(component);
        }
        return group;
    }

    private GroupLayout.Group parallelBaseline(JComponent... components) {
        GroupLayout.Group group = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        for (JComponent component : components) {
            group.addComponent(component);
        }
        return group;
    }
}
