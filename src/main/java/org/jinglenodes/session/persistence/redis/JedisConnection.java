package org.jinglenodes.session.persistence.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: pepe
 * Date: 27/10/11
 * Time: 9:30
 */
public class JedisConnection extends JedisPool {
    private static Map<Integer, JedisConnection> self = new ConcurrentHashMap<Integer, JedisConnection>();

    private JedisConnection(String server, final int port) {
        super(new JedisPoolConfig(), server, port);
    }

    public static JedisConnection getInstance(String server, final int port) {

        JedisConnection jc = self.get(port);
        if (jc == null) {
            jc = new JedisConnection(server, port);
            self.put(port, jc);
        }
        return jc;

    }
}