package com.sadgames.gl3dengine.manager;

import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.HashMap;

import static com.sadgames.gl3dengine.GLEngineConsts.NOT_ENOUGH_SPACE_IN_CACHE_ERROR_MESSAGE;

public abstract class AbstractEntityCacheManager<T> {

    private class CacheItem {
       private T entity;
       private String key;
       private int usageCounter = 0;

        public CacheItem(T entity, String key) {
            this.entity = entity;
            this.key = key;
        }

        public T getEntity() {
            usageCounter++;
            return entity;
        }

        public int getUsageCounter() {
            return usageCounter;
        }
        public String getKey() {
            return key;
        }
    }

    private long cacheSize;
    private long actualCacheSize = 0;
    private HashMap<String, CacheItem> items = new HashMap<>();
    protected SysUtilsWrapperInterface sysUtilsWrapper;

    protected AbstractEntityCacheManager(SysUtilsWrapperInterface sysUtilsWrapper, long cacheSize) {
        this.cacheSize = cacheSize;
        this.sysUtilsWrapper = sysUtilsWrapper;
    }

    protected abstract long getItemSize(T item);
    protected abstract void releaseItem(T item);
    protected abstract T createItem(String key);

    public T getItem(String key) {
        if (!items.containsKey(key)) {
            T newItem = createItem(key);
            long itemSize = getItemSize(newItem);

            freeNecessarySpace(itemSize);
            items.put(key, new CacheItem(newItem, key));
            actualCacheSize += itemSize;
        }

        return items.get(key).getEntity();
    }

    public void clearCache() {
        for(CacheItem item : items.values())
            deleteItem(item);
    }

    private boolean isEnoughSpace(long size) {
        if (size > cacheSize)
            throw new IllegalArgumentException(NOT_ENOUGH_SPACE_IN_CACHE_ERROR_MESSAGE);

        return (cacheSize - actualCacheSize) >= size;
    }

    private CacheItem get2KillCandidate() {
        int minUsageCounter = 0;
        CacheItem mortal = null;

        for(CacheItem item : items.values())
            if (mortal == null || item.getUsageCounter() < minUsageCounter) {
                mortal = item;
                minUsageCounter = item.getUsageCounter();
            }

        return mortal;
    }

    private void deleteItem(CacheItem item) {
        items.remove(item.getKey());
        actualCacheSize -= getItemSize(item.getEntity());
        releaseItem(item.getEntity());
    }

    private void freeNecessarySpace(long size) {
        if (isEnoughSpace(size))
            return;

        deleteItem(get2KillCandidate());
        freeNecessarySpace(size);
    }

}
