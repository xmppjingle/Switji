package org.xmpp;

import junit.framework.TestCase;
import org.jinglenodes.session.persistence.PersistentCallSessionMapper;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 8:42 PM
 */
public class TestPersistentMapper extends TestCase{

    final private static PersistentCallSessionMapper sessionMapper = new PersistentCallSessionMapper();

    public void testMarshal(){
        String x="";
        long st = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {

        }
        System.out.println("Elapsed " + (System.currentTimeMillis()-st) + " for 100 marshal");

        st = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {

        }
        System.out.println("Elapsed " + (System.currentTimeMillis()-st) + " for 100 unmarshal");

    }

    public void testUnmarshal(){

    }

}
