package org.xmpp;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.jinglenodes.Main;
import org.jinglenodes.component.SIPGatewayApplication;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 3/20/12
 * Time: 3:32 PM
 */
public class TestSIPGateway extends TestCase {

    private static final Logger log = Logger.getLogger(TestSIPGateway.class);

    public void testStart() {

        Main.setAppDir(System.getProperty("user.dir") + "/target/test-classes/");
        Main.start("sipgatewaytest.xml");

        final JingleProcessor jingleProcessor = Main.getSipGatewayApplication().getJingleProcessor();

        jingleProcessor.processIQ(TestGatewayFlow.fakeJingleInitiate("initiator@abc.com", "responder@abc.com", "sip.abc.com"));

    }


}
