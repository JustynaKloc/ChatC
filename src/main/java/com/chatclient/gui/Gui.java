package com.chatclient.gui;

import com.chatclient.service.ChatServiceClient;
import org.apache.xmlrpc.XmlRpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    public Gui() throws IOException {
        initUI();
    }

    private void initUI() throws IOException {

        //Creating the Frame
        frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        BufferedImage myPicture = ImageIO.read(new File("/home/justyna/Desktop/ChatClient-master/chat.png"));
        JLabel picLabel = new JLabel(new ImageIcon(myPicture));

        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("");
        JTextField inputMessageTextField = new JTextField(35); // accepts upto 10 characters
        JButton buttonSend = new JButton(">");
        JButton buttonServiceType = new JButton("Hessian");
        panel.setForeground(Color.DARK_GRAY);
        panel.setBackground(Color.DARK_GRAY);
        panel.add(buttonServiceType);
        panel.add(label); // Components Added using Flow Layout
        panel.add(inputMessageTextField);
        inputMessageTextField.setBackground(Color.DARK_GRAY);
        inputMessageTextField.setForeground(Color.WHITE);
        panel.add(buttonSend);

        label.setBackground(Color.DARK_GRAY);
        buttonSend.setBackground(Color.PINK);
        buttonServiceType.setBackground(Color.PINK);



        // Text Area at the Center
        chatArea = new JTextArea();
        chatArea.setBackground(Color.DARK_GRAY);
        chatArea.setDisabledTextColor(Color.WHITE);
        chatArea.setCaretColor(Color.WHITE);
        chatArea.setForeground(Color.WHITE);
        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, chatArea);
        frame.setVisible(true);
        frame.setBackground(Color.DARK_GRAY);


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
        frame.setBackground(Color.DARK_GRAY);
        frame.setForeground(Color.DARK_GRAY);
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