package com.sadgames.dicegame.desktop;

import com.badlogic.gdx.files.FileHandle;
import com.sadgames.gl3dengine.glrender.GdxExt;
import com.sadgames.sysutils.common.GdxDbInterface;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.sadgames.sysutils.common.SysUtilsConsts.DB_NAME;

/**
 * Created by Slava Mamchur on 02.11.2018.
 */
public class DesktopGdxDbWrapper implements GdxDbInterface {

    private String name;

    public DesktopGdxDbWrapper() {
        name = DB_NAME + ".sq3";
        initDataBase();
    }

    private void initDataBase() {
        FileHandle handle = GdxExt.files.external(name);

        try {
            if (!handle.exists())
                GdxExt.files.internal("db/" + name).copyTo(handle);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create DataBase.");
        }

        try {
            DriverManager.registerDriver((Driver) Class.forName("org.sqlite.JDBC").newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Failed to register SQLDroidDriver");
        }

    }

    @Override
    public Connection getJDBCConnection() {
        String jdbcUrl = "jdbc:sqlite:" + GdxExt.files.external(name).file().getAbsolutePath();
        try {
            return DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
