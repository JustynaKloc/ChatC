package com.chatclient;

import com.chatclient.gui.Gui;
import com.chatclient.service.ChatServiceClient;
import org.apache.xmlrpc.XmlRpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Client {

    @Bean
    public HessianProxyFactoryBean hessianInvoker() {
        HessianProxyFactoryBean invoker = new HessianProxyFactoryBean();
        invoker.setServiceUrl("http://localhost:8080/chat");
        invoker.setServiceInterface(ChatServiceClient.class);
        return invoker;
    }

    @Bean
    public XmlRpcClient XmlRpcClient (){
        try {
            XmlRpcClient xmlRpcClient = new XmlRpcClient("http://localhost/RPC2");
            return xmlRpcClient;
        } catch (Exception exception) {
            System.err.println("JavaClient: " + exception);
        }
        return null;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Client.class).
                headless(false).run(args);
        ChatServiceClient service = ctx.getBean(ChatServiceClient.class);

        XmlRpcClient test = ctx.getBean(XmlRpcClient.class);

        Gui gui = ctx.getBean(Gui.class);
        String id;
        id = gui.displayIdInput();

        try {
            while (true){
                if (!StringUtils.isEmpty(id) && service.isIdFree(id) && !gui.XMLRPCMode){
                    service.createNewUser(id);
                    break;
                }
                if (!StringUtils.isEmpty(id) && service.isIdFree(id) && gui.XMLRPCMode) {
                    Vector params = new Vector();
                    params.addElement(id);
                    test.execute("XMLRPCServer.createNewUser", params);
                    break;
                }
                else System.exit(0);

            }
            while (true) {
                if (!gui.XMLRPCMode) {
                    List<String> pendingMessages = service.getMessages(id);
                    if (!pendingMessages.isEmpty()) {
                        for (String message : pendingMessages) {
                            gui.displayMessage(message + '\n');
                        }
                    }
                }

                if (gui.XMLRPCMode) {
                    Vector params = new Vector();
                    params.addElement(id);

                    Object result = test.execute("XMLRPCServer.getMessagesXMLRPC", params);
                    if (!((Vector) result).isEmpty()) {
                        for (String message : (Vector<String>) result) {
                            gui.displayMessage(message + '\n');
                        }
                    }
                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
