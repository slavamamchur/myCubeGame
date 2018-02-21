package com.sadgames.gl3dengine.manager;

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.GLEngineConsts.TEXTURE_CACHE_SIZE;

public class TextureCacheManager extends AbstractEntityCacheManager<AbstractTexture> {

    private static final Object lockObject = new Object();
    private static TextureCacheManager instance = null;

    private TextureCacheManager(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper, TEXTURE_CACHE_SIZE);
    }

    public static TextureCacheManager getInstance(SysUtilsWrapperInterface sysUtilsWrapper) {
        synchronized (lockObject) {
            return instance != null ? instance : new TextureCacheManager(sysUtilsWrapper);
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
        return BitmapTexture.createInstance(sysUtilsWrapper, key);
    }
}
