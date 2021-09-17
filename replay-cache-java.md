### Replay Cache - Java Sample

```java
package com.example.bankserver;

import java.nio.ByteBuffer;

import java.util.concurrent.ConcurrentHashMap;

import java.util.logging.Logger;

/**
 * Singleton thread maintaining a reply cache.
 * 
 * Replays are only checked within the time limits for
 * authorizations, because if an authorization has expired, it
 * will be immediately rejected anyway, and not go into the cache.
 * 
 */
public enum ReplayCache {

    // Single-element enums are according to authoritative Java gurus the optimal
    // way creating thread-safe singletons.
    INSTANCE;

    static final long CYCLE_TIME = 120000;

    Logger logger = Logger.getLogger(ReplayCache.class.getCanonicalName());

    final ConcurrentHashMap<ByteBuffer, Long> cache = new ConcurrentHashMap<>();

    ReplayCache() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(CYCLE_TIME);
                        long now = System.currentTimeMillis();
                        cache.forEach((cacheableSadObject, expirationTime) -> {
                            if (expirationTime < now) {
                                // This authorization is already consumed but is now too 
                                // old to qualify, so we can safely remove it from the cache
                                // (in order to keep it as small and up-to-date as possible).
                                cache.remove(cacheableSadObject);
                                logger.info("Removed authorization token: " + 
                                            cacheableSadObject.hashCode());
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

### Use

```java
    if (ReplayCache.INSTANCE.add(ByteBuffer.wrap(sadByteArray), expirationTime)) {
        // Deal with the replay
    }
```
