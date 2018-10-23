package com.sadgames.sysutils.common;

import com.badlogic.gdx.graphics.glutils.ETC1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ETC1Utils {

    /**
     * A utility class encapsulating a compressed ETC1 texture.
     */
    public static class ETC1Texture {

        private int mWidth;
        private int mHeight;
        private ByteBuffer mData;

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

        public static ETC1Texture createFromStream(InputStream input) throws IOException {
            int width;
            int height;
            byte[] ioBuffer = new byte[32768];
            {
                int headerSize = ETC1.PKM_HEADER_SIZE;
                if (input.read(ioBuffer, 0, headerSize) != headerSize) {
                    throw new IOException("Unable to read PKM file header.");
                }
                ByteBuffer headerBuffer = ByteBuffer.allocateDirect(headerSize)
                        .order(ByteOrder.nativeOrder());
                headerBuffer.put(ioBuffer, 0, headerSize).position(0);
            /*if (!ETC1.isValid(headerBuffer)) {
                throw new IOException("Not a PKM file.");
            }*/
                headerBuffer.position(40);
                width = headerBuffer.getShort();
                height = headerBuffer.getShort();
                headerBuffer.position(0);
                //width = ETC1.getWidth(headerBuffer);
                //height = ETC1.getHeight(headerBuffer);

            }
            int encodedSize = ETC1.getCompressedDataSize(width, height);
            ByteBuffer dataBuffer = ByteBuffer.allocateDirect(encodedSize).order(ByteOrder.nativeOrder());
            for (int i = 0; i < encodedSize; ) {
                int chunkSize = Math.min(ioBuffer.length, encodedSize - i);
                if (input.read(ioBuffer, 0, chunkSize) != chunkSize) {
                    throw new IOException("Unable to read PKM file data.");
                }
                dataBuffer.put(ioBuffer, 0, chunkSize);
                i += chunkSize;
            }
            dataBuffer.position(0);

            return new ETC1Texture(width, height, dataBuffer);
        }
    }

}
