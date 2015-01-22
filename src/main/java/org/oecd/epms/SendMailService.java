package org.oecd.epms;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.platform.Verticle;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMailService extends Verticle {

    @Override
    public void start() {
        EventBus eb = vertx.eventBus();
        Handler<Message<JsonObject>> inHandler = new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> message) {
                Logger logger = container.logger();
                logger.info("SendMailService here, someone requested resource: " + message.body());

                String to = message.body().getString("to");
                String subject = message.body().getString("subject");
                String body = message.body().getString("body");

                // Validate mail

                JsonObject status = sendEmail(to, subject, body);
                vertx.eventBus().send("epms.email.out", status);
            }
        };
        eb.registerHandler("epms.email.in", inHandler);

        HttpServer httpServer = vertx.createHttpServer();
        SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);

        JsonObject config = new JsonObject().putString("prefix", "/bridge");
        JsonArray noPermitted = new JsonArray();
        noPermitted.add(new JsonObject());
        sockJSServer.bridge(config, noPermitted, noPermitted);

        httpServer.listen(8888);
    }

    private JsonObject sendEmail(String to, String subject, String body) {
        String from = "web@gmail.com";
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);

        try{
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(body);

            // Send message
            Transport.send(message);

            return createStatusObject("success", "Sent message successfully", null);
        } catch (Exception ex) {
            return createStatusObject("error", ex.getMessage(), ex.getStackTrace().toString());
        }
    }

    private JsonObject createStatusObject(String status, String message, String stackTrace) {
        JsonObject statusJson = new JsonObject();
        statusJson.putString("status", status);
        statusJson.putString("message", message);
        statusJson.putString("stackTrace", stackTrace);
        return statusJson;
    }
}
