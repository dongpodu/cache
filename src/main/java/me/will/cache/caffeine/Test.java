package me.will.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Test {

    Cache<String, Object> manualCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    LoadingCache<String, String> loadingCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build(k -> k);

    /**
     * 如果key获取不到，则从回源函数中加载
     *
     * @param keys
     */
    public void testGetAll(List<String> keys) {
        manualCache.getAll(keys, (ks) -> {
            try {
                if (Thread.currentThread().getName().equals("thread 1")) {
                    Thread.sleep(30000);
                }
                System.out.println(Thread.currentThread().getName());
                System.out.println(manualCache.asMap());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Map<String, Object> result = new HashMap<>();
            ks.forEach(r -> result.put(r, r));
            return result;
        });
    }

    public void testGet(String key) {
        manualCache.get(key, (k) -> {
            System.out.println(Thread.currentThread().getName());
            System.out.println(manualCache.asMap());
            return k;
        });
    }

    void loadingCache(String key) {

    }

    public static void main(String[] args) {
        Test test = new Test();

        Thread thread = new Thread(() -> test.testGetAll(List.of("1")));
        thread.setName("thread 1");
        thread.start();

//        CompletableFuture.runAsync(() ->{
//            test.testGetAll(List.of(String.valueOf(3), String.valueOf(4)));
//        });

        Thread thread2 = new Thread(() -> test.testGet("1"));
        thread2.setName("thread 2");
        thread2.start();
    }
}
