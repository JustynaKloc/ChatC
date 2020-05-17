package com.chatclient.gui;

import com.chatclient.service.ChatServiceClient;
import javafx.scene.Scene;
import org.apache.xmlrpc.XmlRpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.Vector;

@Component

public class Gui extends JFrame {

    @Autowired
    ChatServiceClient chatServiceClient;
    @Autowired
    XmlRpcClient xmlRpcClient;
    JTextArea chatArea;
    String userId;
    JFrame frame;
    public boolean XMLRPCMode = false;
    public Gui(){
        initUI();
    }

    private void initUI() {

        //Creating the Frame
        frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);


        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("Enter Text");
        JTextField inputMessageTextField = new JTextField(20); // accepts upto 10 characters
        JButton buttonSend = new JButton("Send");
        JButton buttonServiceType = new JButton("Hessian");
        panel.add(label); // Components Added using Flow Layout
        panel.add(inputMessageTextField);
        panel.add(buttonSend);
        panel.add(buttonServiceType);

        // Text Area at the Center
        chatArea = new JTextArea();

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, chatArea);
        frame.setVisible(true);


        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String input = inputMessageTextField.getText();
                if (!StringUtils.isEmpty(input)){
                    sendMessage(input);
                    inputMessageTextField.setText("");
                }
            }
        });

        buttonServiceType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (XMLRPCMode) {
                    XMLRPCMode = false;
                    buttonServiceType.setText("Hessian");
                }
                else {
                    XMLRPCMode = true;
                    buttonServiceType.setText("XMLRPC");
                }
            }
        });
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Gui.class)
                .headless(false).run(args);

        EventQueue.invokeLater(() -> {
            Gui ex = ctx.getBean(Gui.class);
            ex.setVisible(true);
        });
    }

    public String displayIdInput(){
        String result = (String) JOptionPane.showInputDialog(this,"Enter your name",
                "Enter your name",
                JOptionPane.PLAIN_MESSAGE,
                null,null,
        "");
        userId = result;
        frame.setTitle(userId);
        return result;
    }

    public void displayMessage(String message) {
        chatArea.append(message);
    }

    public void sendMessage (String message){
        if (!XMLRPCMode){
        chatServiceClient.writeMessage(message, userId);
        }
        else {
            Vector params = new Vector();
            params.addElement(message);
            params.addElement(userId);
            try {
                xmlRpcClient.execute("XMLRPCServer.writeMessage", params);
            } catch (Exception e){
                System.err.println("Exception thrown while using XMLRPC write message method " + e);
            }
        }

    }
}