### Replay Cache - Java Sample

```java
package com.example.bankserver;

import java.nio.ByteBuffer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Replay cache support.
 * 
 * Replays are only checked within the time limits for authorizations, because 
 * if a received authorization has already expired, it should be rejected,
 * rather than being cached.
 * 
 */
public enum ReplayCache {

    // According to multiple Java information resources, the "enum" type represents
    // a viable option for creating singletons in multi-threaded applications.
    INSTANCE;

    private static final long CYCLE_TIME = 120000;

    private final ConcurrentHashMap<ByteBuffer, Long> cache = new ConcurrentHashMap<>();

    private ReplayCache() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(CYCLE_TIME);
                        long now = System.currentTimeMillis();
                        cache.forEach((cacheableSadObject, expirationTime) -> {
                            if (expirationTime < now) {
                                // The authorization has apparently expired so we can safely
                                // remove it from the replay cache in order to keep the cache
                                // as small and up-to-date as possible.
                                cache.remove(cacheableSadObject);
                            }
                        });
                    } catch (InterruptedException e) {
                        new RuntimeException("Unexpected interrupt", e);
                    }
                }
            }
           
        }).start();
    }
    
    /**
     * Add validated SAD object to the replay cache.
     *
     * Note: the <code>expirationTime</code> stays the same for replayed SAD objects,
     * making rewrites benign.
     * 
     * @param cacheableSadObject The SAD object packaged to suit HashMap
     * @param expirationTime For the SAD object
     * @return <code>true</code> if replay, else <code>false</code>
     */
    public boolean add(ByteBuffer cacheableSadObject, long expirationTime) {
        return cache.put(cacheableSadObject, expirationTime) != null;
    }
}
```

### Usage

```java
    // Assumptions:
    // The SAD object has been fully validated.
    // The SAD binary is available in: byte[] sadByteArray;
    // The SAD timeStamp is available in: long timeStamp;
    
    long now = System.currentTimeMillis();
    long expirationTime = timeStamp + AUTHORIZATION_MAX_AGE;
    if (expirationTime < now) {
        // Expired
    }

    if (timeStamp - AUTHORIZATION_MAX_FUTURE > now) {
        // Out of sync
    }

    if (ReplayCache.INSTANCE.add(ByteBuffer.wrap(sadByteArray), expirationTime)) {
        // Replay
    }
```
