package com.sadgames.gl3dengine.manager;

import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.HashMap;

import static com.sadgames.gl3dengine.GLEngineConsts.NOT_ENOUGH_SPACE_IN_CACHE_ERROR_MESSAGE;

public abstract class AbstractEntityCacheManager<T> {

    private class CacheItem {
        private T entity;
        private String key;
        private int usageCounter = 0;
        private boolean immortal;

        @SuppressWarnings("all")
        public CacheItem(T entity, String key, boolean immortal) {
            this.entity = entity;
            this.key = key;
            this.immortal = immortal;
        }

        public T getEntity() {
            usageCounter++;
            return entity;
        }

        @SuppressWarnings("all") public int getUsageCounter() {
            return usageCounter;
        }
        public String getKey() {
            return key;
        }
        @SuppressWarnings("all") public boolean isImmortal() {
            return immortal;
        }

        public void setImmortal(boolean immortal) {
            this.immortal = immortal;
        }
    }

    private long cacheSize;
    private long actualCacheSize = 0;
    private HashMap<String, CacheItem> items = new HashMap<>();
    protected SysUtilsWrapperInterface sysUtilsWrapper;

    @SuppressWarnings("all")
    protected AbstractEntityCacheManager(SysUtilsWrapperInterface sysUtilsWrapper, long cacheSize) {
        this.cacheSize = cacheSize;
        this.sysUtilsWrapper = sysUtilsWrapper;
    }

    public abstract long getItemSize(T item);
    protected abstract void releaseItem(T item);
    protected abstract T createItem(String key);

    public T getItem(String key) {
        if (!items.containsKey(key)) {
            T newItem = createItem(key);
            putItem(newItem, key, getItemSize(newItem), false);
        }

        return items.get(key).getEntity();
    }

    /** for non-typical cubeMap textures */
    public void putItem(T item, String key, long itemSize) {
        putItem(item, key, itemSize, true);
    }

    @SuppressWarnings("all")
    protected void putItem(T item, String key, long itemSize, boolean isImmortal) {
        if (!items.containsKey(key)) {
            freeNecessarySpace(itemSize);
            items.put(key, new CacheItem(item, key, isImmortal));
            actualCacheSize += itemSize;
        }
        else
            items.get(key).setImmortal(isImmortal);
    }

    /** for load new level with keeping some objects if needed*/
    public void setWeakCacheMode() {
        for(CacheItem item : items.values())
            item.setImmortal(false);
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
            if ((mortal == null || item.getUsageCounter() < minUsageCounter) && !item.isImmortal()) {
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
