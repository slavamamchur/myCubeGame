package com.sadgames.gl3dengine.manager;

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapTexture;

import static com.sadgames.gl3dengine.GLEngineConsts.TEXTURE_CACHE_SIZE;

public class TextureCacheManager extends AbstractEntityCacheManager<AbstractTexture> {

    private static final Object lockObject = new Object();
    private static TextureCacheManager instance = null;

    private TextureCacheManager() {
        super(TEXTURE_CACHE_SIZE);
    }

    public static TextureCacheManager getInstance() {
        synchronized (lockObject) {
            instance = instance != null ? instance : new TextureCacheManager();
            return instance;
        }
    }

    public static TextureCacheManager getNewInstance() {
        synchronized (lockObject) {
            instance = new TextureCacheManager();
            return instance;
        }
    }

    @Override
    public long getItemSize(AbstractTexture item) {
        return item.getTextureSize();
    }

    @Override
    protected void releaseItem(AbstractTexture item) {
        item.deleteTexture();
    }

    @Override
    protected AbstractTexture createItem(String key) {
        return BitmapTexture.createInstance(key);
    }
}
