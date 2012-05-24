package org.jinglenodes.session.persistence;

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

        if (self.containsKey(port) == false)
            self.put(port, new JedisConnection(server, port));
        return self.get(port);

    }
}