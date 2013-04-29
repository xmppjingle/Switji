package org.jinglenodes.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: bhlangonijr
 * Date: 4/29/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConcurrentExpirableHashMapTest extends TestCase {

    ExecutorService service = Executors.newFixedThreadPool(4);
    ConcurrentExpirableHashMap<String, String> map = new ConcurrentExpirableHashMap<String, String>(100000, 2000, 2000);


    public void testExpiration() throws InterruptedException {

        map.put("my.name","ben");

        Thread.sleep(1000);

        assertTrue(map.containsKey("my.name"));
        assertEquals(map.get("my.name"), "ben");

        Thread.sleep(3000);

        assertFalse(map.containsKey("my.name"));


    }

    public void testConcurrency() throws ExecutionException, InterruptedException {

        List<Future> futures = new ArrayList<Future>();

        map.setTtl(5000);

        final long init = System.currentTimeMillis();

        for (int i = 1; i <= 10; i++) {
            final int cnt = i;
            futures.add(service.submit(new Callable<Long>() {
                @Override
                public Long call() {
                    final long t = System.currentTimeMillis();
                    for (int j = 1; j <= 6000; j++) {
                        map.put(cnt + "-" + j, "string=" + cnt + "-" + j);
                    }
                    return System.currentTimeMillis() - t;
                }
            }));
        }

        int c = 0;

        while (c < 10) {
            for (Future f: futures) {
                try {
                    System.out.println("Task time="+f.get(100,TimeUnit.MILLISECONDS));
                    c++;
                } catch (TimeoutException e) {}
            }
        }

        assertTrue(map.size()==10*6000);

        map.setTtl(100);

        Thread.sleep(2000);

        assertTrue(map.size()==0);

        System.out.println("Total time = "+(System.currentTimeMillis()-init));

    }

}
