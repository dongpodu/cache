package me.will.cache.caffeine;

import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class Test {

    Cache<String, String> manualCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    LoadingCache<String, String> loadingCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build(k -> {
                System.out.println("loading " + k);
                return k;
            });

    AsyncLoadingCache<String, String> asyncLoadingCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .buildAsync(new AsyncCacheLoader<>() {
                @Override
                public @NonNull CompletableFuture<String> asyncLoad(@NonNull String key, @NonNull Executor executor) {
                    return CompletableFuture.completedFuture(key);
                }
            });

    /**
     * 如果key获取不到，则从回源函数中加载
     *
     * @param keys
     */
    Map<String, String> testGetAll(List<String> keys) {
        return manualCache.getAll(keys, (ks) -> {
            Map<String, String> result = new HashMap<>();
            ks.forEach(r -> result.put(r, r));
            return result;
        });
    }

    /**
     * 如果key获取不到，则从回源函数中加载
     *
     * @param key
     */
    String testGet(String key) {
        return manualCache.get(key, (k) -> k);
    }

    String loadingCache(String key) {
        return loadingCache.get(key);
    }

    CompletableFuture<String> asyncLoadingCache(String key) {
        return asyncLoadingCache.get(key);
    }

    public static void main(String[] args) {
        Test test = new Test();
//        test.testGetAll(List.of("1"));
//        test.testGetAll(List.of("2"));
//        test.testGetAll(List.of("1"));

//        test.loadingCache("1");
//        test.loadingCache("1");
//        test.loadingCache("2");

        test.asyncLoadingCache("1");
        test.asyncLoadingCache("2");
    }
}
