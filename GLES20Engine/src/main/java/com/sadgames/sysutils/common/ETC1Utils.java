package com.sadgames.sysutils.common;

import java.nio.ByteBuffer;

public class ETC1Utils {

    /**
     * A utility class encapsulating a compressed ETC1 texture.
     */
    public static class ETC1Texture {
        public ETC1Texture(int width, int height, ByteBuffer data) {
            mWidth = width;
            mHeight = height;
            mData = data;
        }

        /**
         * Get the width of the texture in data.
         * @return the width of the texture in data.
         */
        public int getWidth() { return mWidth; }

        /**
         * Get the height of the texture in data.
         * @return the width of the texture in data.
         */
        public int getHeight() { return mHeight; }

        /**
         * Get the compressed data of the texture.
         * @return the texture data.
         */
        public ByteBuffer getData() { return mData; }

        private int mWidth;
        private int mHeight;
        private ByteBuffer mData;
    }


}
