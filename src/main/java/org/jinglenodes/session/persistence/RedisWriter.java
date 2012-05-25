package org.jinglenodes.session.persistence;

import org.apache.log4j.Logger;
import org.jinglenodes.session.CallSession;
import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 11:54 PM
 */
public class RedisWriter implements PersistenceWriter {

    final private static Logger log = Logger.getLogger(RedisWriter.class);
    final private String ENCODE = "UTF-8";
    private JedisConnection jedisConnection;
    private String redisHost = "localhost";
    private int redisPort = 6379;

    @Override
    public void write(String id, byte[] data) {
        try {
            JedisConnection connection = JedisConnection.getInstance(redisHost, redisPort);
            Jedis jedis = connection.getResource();

            if (jedis == null) return;

            try {
                log.debug("Persisting CallSession");
                jedis.setex(id.getBytes(ENCODE), 3600, data);
            } catch (UnsupportedEncodingException e) {
                log.error("Unsupported Encoding on CallSession ID", e);
            } finally {
                connection.returnResource(jedis);
            }
        } catch (Exception e) {
            log.error("Could not Write: " + id, e);
        }

    }

    @Override
    public void delete(String id) {
        try {
            JedisConnection connection = JedisConnection.getInstance(redisHost, redisPort);
            Jedis jedis = connection.getResource();

            if (jedis == null) return;

            try {
                log.debug("Deleting Persistent CallSession");
                jedis.del(id.getBytes(ENCODE));
            } catch (UnsupportedEncodingException e) {
                log.error("Unsupported Encoding on CallSession ID", e);
            } finally {
                connection.returnResource(jedis);
            }
        } catch (Exception e) {
            log.error("Could not Delete: " + id, e);
        }

    }

    public List<byte[]> loadData() {

        try {
            JedisConnection connection = JedisConnection.getInstance(redisHost, redisPort);
            Jedis jedis = connection.getResource();

            if (jedis == null) return null;

            final Set<String> keys = jedis.keys("*");
            final List<byte[]> data = new ArrayList<byte[]>(keys.size());

            log.debug("Loading Persistent CallSession...");
            for (final String key : keys) {
                try {
                    log.debug("Loaded Key: " + key);
                    final byte[] b = jedis.get(key.getBytes(ENCODE));
                    data.add(b);
                } catch (UnsupportedEncodingException e) {
                    log.error("Unsupported Encoding on CallSession ID", e);
                } finally {
                    connection.returnResource(jedis);
                }
            }

            return data;
        } catch (Exception e) {
            log.error("Could not Load Data", e);
        }

        return null;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }
}
