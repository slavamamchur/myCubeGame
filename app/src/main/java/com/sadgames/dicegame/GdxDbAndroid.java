package com.sadgames.dicegame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sadgames.sysutils.common.GdxDbInterface;
import com.sadgames.sysutils.platforms.android.AndroidSQLiteDBHelper;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.sadgames.sysutils.common.SysUtilsConsts.DB_NAME;

public class GdxDbAndroid implements GdxDbInterface {

    private Context context;

    public GdxDbAndroid(Context context) {
        this.context = context.getApplicationContext();

        initDataBase();
    }

    private void initDataBase() {
        try {
            AndroidSQLiteDBHelper dbHelper = new AndroidSQLiteDBHelper(context, DB_NAME, null, AndroidSQLiteDBHelper.DB_VERSION);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.close();
            dbHelper.close();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create DataBase.");
        }

        try {
            DriverManager.registerDriver((Driver) Class.forName("org.sqldroid.SQLDroidDriver").newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Failed to register SQLDroidDriver");
        }
    }

    @Override
    public Connection getJDBCConnection() {
        String jdbcUrl = "jdbc:sqldroid:" + context.getDatabasePath(DB_NAME).getPath();
        try {
            return DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
