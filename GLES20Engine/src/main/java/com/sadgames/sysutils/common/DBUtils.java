package com.sadgames.sysutils.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.sadgames.sysutils.common.SysUtilsConsts.DB_NAME;
import static com.sadgames.sysutils.common.SysUtilsConsts.DB_TABLE_NAME;
import static com.sadgames.sysutils.common.SysUtilsConsts.MAP_ID_DB_FIELD;
import static com.sadgames.sysutils.common.SysUtilsConsts.MAP_UPDATED_DATE_DB_FIELD;

public class DBUtils {

    public static boolean isBitmapCached(SysUtilsWrapperInterface sysUtils, String id, Long updatedDate) {
        boolean result = false;
        Connection conn = null;

        try {
            conn = sysUtils.iGetDBConnection(DB_NAME);

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
        finally {
            if (conn != null) try {conn.close();} catch (SQLException ignored) {}
        }

        return result;
    }
}
