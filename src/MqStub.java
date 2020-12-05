import com.ibm.mq.jms.*;
import com.ibm.msg.client.wmq.compat.base.internal.MQPutMessageOptions;

import javax.jms.*;

public class MqStub {
    public static void main(String[] args) {
        try {
            MQQueueConnection mqConn;
            MQQueueConnectionFactory mqCF;
            final MQQueueSession mqSession;
            MQQueue mqIn;
            MQQueue mqOut;
            MQQueueReceiver mqReceiver;
            MQQueueSender mqSender;

            mqCF = new MQQueueConnectionFactory();
            mqCF.setHostName("localhost");

            mqCF.setPort(1377);

            mqCF.setQueueManager("ADMIN");
            mqCF.setChannel("SYSTEM.DEF.SVRCONN");

            mqConn = (MQQueueConnection) mqCF.createConnection();
            mqSession = (MQQueueSession) mqConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            mqIn = (MQQueue) mqSession.createQueue("MQ.IN");
            mqReceiver = (MQQueueReceiver) mqSession.createReceiver(mqIn);

            mqOut = (MQQueue) mqSession.createQueue("MQ.OUT");
            mqSender = (MQQueueSender) mqSession.createSender(mqOut);

            MessageListener listener = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        System.out.println("Got Message!!!");
                        try {
                            String tMsg = ((TextMessage) message).getText();
                            System.out.println(tMsg);
                            mqSender.send(message);
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            mqReceiver.setMessageListener(listener);
            mqConn.start();
            System.out.println("Stub Started.");

        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
