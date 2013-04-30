package org.jinglenodes.util;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.apache.log4j.Logger;
import org.zoolu.tools.NamingThreadFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Expirable concurrent hashmap with time to live and max entries
 * based on google {@see ConcurrentLinkedHashMap}
 *
 * @author bhlangonijr
 * Date: 4/29/13
 * Time: 3:09 PM
 */
public class ConcurrentExpirableHashMap  <K, V>  extends java.util.AbstractMap<K,V>
        implements java.util.concurrent.ConcurrentMap<K,V>, java.io.Serializable {
    static final Logger log = Logger.getLogger(ConcurrentExpirableHashMap.class);

    private final ConcurrentLinkedHashMap<K, V> map;
    private final ConcurrentLinkedHashMap<K, Long> expireMap;
    private final ScheduledExecutorService scheduledService;
    private final ExecutorService service;
    private long ttl = DEFAULT_TTL;
    private long purgeCounterLimit = DEFAULT_PURGE_LIMIT;
    private long purgeDelay = DEFAULT_PURGE_DELAY;

    private static final int DEFAULT_MAX_ENTRIES = 5000;
    private static final long DEFAULT_TTL = 1000 * 60 * 60;
    private static final long DEFAULT_PURGE_DELAY = 1000 * 60 * 10;
    private static final long DEFAULT_PURGE_LIMIT = 2000;

    private final AtomicInteger counter = new AtomicInteger(0);

    private Future purgeTask = null;

    public ConcurrentExpirableHashMap() {
        this(DEFAULT_MAX_ENTRIES, DEFAULT_TTL, DEFAULT_PURGE_DELAY);
    }

    public ConcurrentExpirableHashMap(int maxEntries, long timeToLive) {
        this(maxEntries, timeToLive, DEFAULT_PURGE_DELAY);
    }

    public ConcurrentExpirableHashMap(int maxEntries, long timeToLive, long purgeDelay) {
        map = new ConcurrentLinkedHashMap.Builder<K, V>()
                .maximumWeightedCapacity(maxEntries)
                .build();
        expireMap = new ConcurrentLinkedHashMap.Builder<K, Long>()
                .maximumWeightedCapacity(maxEntries)
                .build();
        setTtl(timeToLive);
        service = Executors.newSingleThreadExecutor(new NamingThreadFactory("ConcurrenExpirableHashMap.Task"));
        scheduledService = Executors.newSingleThreadScheduledExecutor(
                new NamingThreadFactory("ConcurrenExpirableHashMap.Scheduled.Task"));
        setPurgeDelay(purgeDelay);

        enableScheduledPurge();

    }

    public void enableScheduledPurge() {
        if (purgeTask != null) {
            log.warn("There is already an active scheduled purge task");
            return;
        }
        purgeTask = scheduledService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    cleanUpExpiredWithNoResult().get(); //delay scheduling of next execution if current
                    // is taking too long
                } catch (Exception e) {
                    log.error("Error cleaning up expired entries: ",e);
                }
            }
        }, getPurgeDelay(), getPurgeDelay(), TimeUnit.MILLISECONDS);
    }

    public void disableScheduledPurge() {
        if (purgeTask == null) {
            log.warn("There is not active scheduled purge task running");
            return;
        }
        purgeTask.cancel(true);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public boolean remove(Object key, Object value) {
        final boolean r = map.remove(key, value);
        if (r) {
            expireMap.remove(key);
        }
        return  r;
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return map.replace(key, value);
    }


    public int capacity() {
        return map.capacity();
    }

    public void setCapacity(int capacity) {
        expireMap.setCapacity(capacity);
        map.setCapacity(capacity);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int size() {
        return map.size();
    }

    public int weightedSize() {
        return map.weightedSize();
    }

    @Override
    public void clear() {
        expireMap.clear();
        map.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    public V put(K key, V value) {
        if  (getPurgeCounterLimit() > 0 &&
               counter.incrementAndGet() > getPurgeCounterLimit() &&
               counter.getAndAdd(0) > getPurgeCounterLimit()) {
            cleanUpExpiredWithNoResult();
        }
        expireMap.put(key, System.currentTimeMillis());
        return map.put(key, value);
    }

    public V putIfAbsent(K key, V value) {
        if (getPurgeCounterLimit() > 0 &&
               counter.incrementAndGet() > getPurgeCounterLimit() &&
               counter.getAndAdd(0) > getPurgeCounterLimit()) {
            cleanUpExpiredWithNoResult();
        }
        expireMap.putIfAbsent(key, System.currentTimeMillis());
        return map.putIfAbsent(key, value);
    }

    @Override
    public V remove(Object key) {
        expireMap.remove(key);
        return map.remove(key);
    }

    /**
     * Force purge of all expired entries for this map
     *
     * @param timeout
     * @return
     */
    public List<V> cleanUpExpired(final long timeout) {

        List<V> l = new ArrayList<V>();

        for (Entry<K, Long> entry: expireMap.entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() > timeout) {
                l.add(map.get(entry.getKey()));
                expireMap.remove(entry.getKey());
                map.remove(entry.getKey());
            }
        }

        return l;
    }

    /**
     * Force purge of all expired entries for this map
     * using default time to live
     * @return
     */
    public List<V> cleanUpExpired() {
        return cleanUpExpired(getTtl());
    }

    private Future cleanUpExpiredWithNoResult() {

        return service.submit(new Runnable() {
            @Override
            public void run() {
                cleanUpExpired(getTtl());
            }
        });

    }

    public long getTtl() {
        return ttl;
    }

    /**
     * Time to live for expiring the entries
     * @param ttl
     */
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getPurgeCounterLimit() {
        return purgeCounterLimit;
    }

    /**
     * Define a threshold limit (on put & putIfAbsent count) for checking for
     * expired entries
     * @param purgeCounterLimit
     */
    public void setPurgeCounterLimit(long purgeCounterLimit) {
        this.purgeCounterLimit = purgeCounterLimit;
    }

    public long getPurgeDelay() {
        return purgeDelay;
    }

    /**
     * Delay for running the task for checking expired entries
     * accordingly with the defined time to live
     * @param purgeDelay
     */
    public void setPurgeDelay(long purgeDelay) {
        this.purgeDelay = purgeDelay;
    }
}
