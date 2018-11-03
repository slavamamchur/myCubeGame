package com.sadgames.sysutils.common;

import com.sadgames.gl3dengine.glrender.GdxExt;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static com.sadgames.sysutils.common.SysUtilsConsts.BYTES_IN_MB;
import static com.sadgames.sysutils.common.SysUtilsConsts.CHUNK_NUMBER_DB_FIELD;
import static com.sadgames.sysutils.common.SysUtilsConsts.DB_TABLE_NAME;
import static com.sadgames.sysutils.common.SysUtilsConsts.MAP_ID_DB_FIELD;
import static com.sadgames.sysutils.common.SysUtilsConsts.MAP_IMAGE_DB_FIELD;
import static com.sadgames.sysutils.common.SysUtilsConsts.MAP_UPDATED_DATE_DB_FIELD;

public class DBUtils {

    //TODO: pass table_name, field_name as params, rename bitmap to array

    private static void saveChunk2DB(Connection conn,
                                     String map_id,
                                     int chunkNumber,
                                     Long updatedDate,
                                     byte[] chunkData) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (" +
                                                               MAP_ID_DB_FIELD + ", " +
                                                               CHUNK_NUMBER_DB_FIELD + ", " +
                                                               MAP_UPDATED_DATE_DB_FIELD + ", " +
                                                               MAP_IMAGE_DB_FIELD +
                                                               ") VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, map_id);
            stmt.setInt(2, chunkNumber);
            stmt.setString(3, String.valueOf(updatedDate));
            stmt.setBinaryStream(4, new ByteArrayInputStream(chunkData), chunkData.length);
            stmt.executeUpdate();
        }
    }

    public static void saveBitmap2DB(byte[] bitmapArray,
                                     String map_id,
                                     Long updatedDate) throws SQLException {
        try (Connection conn = GdxExt.dataBase.getJDBCConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "delete from " + DB_TABLE_NAME + " where " + MAP_ID_DB_FIELD + " = \"" + map_id + "\"")) {
                stmt.executeUpdate();
            }

            int chunkCount = bitmapArray.length / (BYTES_IN_MB * 2);
            final int lastChunkSize = bitmapArray.length % (BYTES_IN_MB * 2);
            chunkCount = lastChunkSize > 0 ? chunkCount + 1 : chunkCount;

            for (int i = 0; i < chunkCount; i++) {
                int chunkSize = (i == (chunkCount - 1)) && (lastChunkSize > 0) ? lastChunkSize : (BYTES_IN_MB * 2);
                byte[] chunkData = Arrays.copyOfRange(bitmapArray, i * chunkSize, (i + 1) * chunkSize);

                saveChunk2DB(conn, map_id, i, updatedDate, chunkData);
            }
        }
    }

    public static boolean isBitmapCached(String id, Long updatedDate) {
        boolean result = false;

        try (Connection conn = GdxExt.dataBase.getJDBCConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select count(" + MAP_ID_DB_FIELD + ") as CNT" +
                                " from " + DB_TABLE_NAME +
                                " where " + MAP_ID_DB_FIELD + " = ?" +
                                " and " + MAP_UPDATED_DATE_DB_FIELD + " = ?")) {
                    stmt.setString(1, id);
                    stmt.setString(2, String.valueOf(updatedDate));

                    try (ResultSet rs = stmt.executeQuery()) {
                        rs.next();
                        result = rs.getInt(1) > 0;
                    }
                }
        }
        catch (SQLException ignored) {}

        return result;
    }

    public static byte[] loadBitmapFromDB(String textureResName, boolean isRelief) throws SQLException {
        byte[] bitmapArray = null;

        GdxExt.restAPI.iDownloadBitmapIfNotCached(textureResName, isRelief);

        try (Connection conn = GdxExt.dataBase.getJDBCConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("select " + MAP_IMAGE_DB_FIELD +
                            " from " + DB_TABLE_NAME +
                            " where " + MAP_ID_DB_FIELD + " = ?" +
                            " order by " + CHUNK_NUMBER_DB_FIELD)) {
                stmt.setString(1, (isRelief ? "rel_" : "") + textureResName);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs != null && rs.first()) {
                        int dataPtr = 0;
                        int chunkCount = 1;
                        while (rs.next()) chunkCount++;
                        int imageSize = chunkCount * (BYTES_IN_MB * 2);

                        rs.last();
                        Blob blob = rs.getBlob(1);
                        byte[] lastChunk = blob.getBytes(0, (int) blob.length());
                        imageSize = lastChunk.length < (BYTES_IN_MB * 2) ? imageSize - (BYTES_IN_MB * 2) + lastChunk.length : imageSize;
                        bitmapArray = new byte[imageSize];
                        rs.first();

                        do {
                            blob = rs.getBlob(1);
                            byte[] chunkData = blob.getBytes(0, (int) blob.length());
                            System.arraycopy(chunkData, 0, bitmapArray, dataPtr, chunkData.length);

                            dataPtr += chunkData.length;
                        } while (rs.next());
                    }
                }
            }
        }

        return bitmapArray;
    }

}
