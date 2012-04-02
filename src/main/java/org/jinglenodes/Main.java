/*
 * Copyright (C) 2011 - Jingle Nodes - Yuilop - Neppo
 *
 *   This file is part of Switji (http://jinglenodes.org)
 *
 *   Switji is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   Switji is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MjSip; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   Author(s):
 *   Benhur Langoni (bhlangonijr@gmail.com)
 *   Thiago Camargo (barata7@gmail.com)
 */

/**
 *
 */
package org.jinglenodes;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.jinglenodes.component.SIPGatewayApplication;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Properties;

/**
 * Main class
 *
 * @author bhlangonijr
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    static SIPGatewayApplication sipGatewayApplication;
    static String appDir;
    final static String defaultSprinfFile = "/conf/sipgateway.xml";

    /**
     * @param args command (start, stop)
     */
    public static void main(String[] args) {

        appDir = System.getProperty("sipgateway.home");
        if (appDir == null) appDir = System.getProperty("user.home") + "/sipgateway/";

        if (args.length > 0) {
            final String opt = args[0].toLowerCase();
            if ("start".equals(opt)) {
                start(defaultSprinfFile);
                return;
            } else if ("stop".equals(opt)) {
                stop();
                return;
            }
        }

        System.err.println("Usage: Main start/stop");
        System.err.println("\nOptions:");
        System.err.println("	start		- Starts SIP Gateway using the configuration files");
        System.err.println("	stop		- Stop the Application Engine");
    }

    /*
      * Starts sip gateway using the configuration files
      */
    public static void start(final String springFile) {
        try {
            long init = System.currentTimeMillis();
            log.info("Using home directory: " + appDir);
            Properties p = System.getProperties();
            p.setProperty("sipgateway.home", appDir);
            PropertyConfigurator.configure(appDir + "/conf/log4j.xml");
            DOMConfigurator.configureAndWatch(appDir + "/conf/log4j.xml");
            ApplicationContext appContext =
                    new FileSystemXmlApplicationContext("file:" + appDir + springFile);
            BeanFactory factory = (BeanFactory) appContext;
            sipGatewayApplication = (SIPGatewayApplication) factory.getBean("sip");
            double time = (double) (System.currentTimeMillis() - init) / 1000.0;
            addShutdownHook();
            log.info("Started Sip Gateway in " + time + " seconds");
            Thread.currentThread().wait();
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Shutdown hook intercepted. Shutting down SIP Gateway...");
                Main.stop();
                log.info("Stopped!");
            }
        });
    }

    /*
      * Kills application engine
      */
    private static void stop() {
        if (sipGatewayApplication != null) {
            sipGatewayApplication.destroy();
        }
    }

    public static SIPGatewayApplication getSipGatewayApplication() {
        return sipGatewayApplication;
    }

    public static String getAppDir() {
        return appDir;
    }

    public static void setAppDir(String appDir) {
        Main.appDir = appDir;
    }

}
