package org.zywx.wbpalmstar.plugin.uexhtm.util;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wisemen.taskwalker.TaskWalker;

/**
 * 下载数据库的辅助类
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "/data/data/org.zywx.wbpalmstar.widgetone.uex11594548/databases/hhb.db";
    public static final int DB_VERSION = 4;


    public DbHelper() {
        super(TaskWalker.getContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}