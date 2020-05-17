package com.chatclient.impl;

import com.chatclient.service.ChatServiceClient;

import java.util.List;

public class ChatServiceClientImpl implements ChatServiceClient {

    @Override
    public String writeMessage(String message, String id) {
        message = System.console().readLine();
        System.out.println("-CLIENT-");
        System.out.println("Message sent");
        System.out.println(message);
        return "Hello from client" + message;
    }

    @Override
    public List<String> getMessages(String id) {
        return null;
    }

    @Override
    public String []getMessagesXMLRPC(String id) {
        return null;
    }

    @Override
    public boolean isIdFree(String id) {
        return false;
    }

    @Override
    public boolean createNewUser(String id) {
        return true;
    }


}
