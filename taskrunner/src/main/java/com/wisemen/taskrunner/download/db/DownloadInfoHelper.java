package com.wisemen.taskrunner.download.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wisemen.taskrunner.download.DownloadInfo;
import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.utils.Logger;

/**
 * 下载数据库的辅助类
 */
public class DownloadInfoHelper extends SQLiteOpenHelper {

//    private static final String DB_CACHE_NAME = Environment.getExternalStorageDirectory() + File.separator + "download" + File.separator + "taskrunner.db";
    private static final String DB_CACHE_NAME = "taskrunner.db";
    public static final int DB_CACHE_VERSION = 4;
    public static final String TABLE_NAME = "download_table";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
            DownloadInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DownloadInfo.TASK_KEY + " VARCHAR, " +
            DownloadInfo.URL + " VARCHAR, " +
            DownloadInfo.TARGET_FOLDER + " VARCHAR, " +
            DownloadInfo.TARGET_PATH + " VARCHAR, " +
            DownloadInfo.FILE_NAME + " VARCHAR, " +
            DownloadInfo.PROGRESS + " REAL, " +
            DownloadInfo.TOTAL_LENGTH + " INTEGER, " +
            DownloadInfo.DOWNLOAD_LENGTH + " INTEGER, " +
            DownloadInfo.NETWORK_SPEED + " INTEGER, " +
            DownloadInfo.STATE + " INTEGER, " +
            DownloadInfo.DOWNLOAD_REQUEST + " BLOB, " +
            DownloadInfo.DATA + " BLOB)";
    private static final String SQL_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX cache_unique_index ON "
            + TABLE_NAME + "(\"" + DownloadInfo.TASK_KEY + "\")";
    private static final String SQL_DELETE_TABLE = "DROP TABLE " + TABLE_NAME;
    private static final String SQL_DELETE_UNIQUE_INDEX = "DROP INDEX cache_unique_index";

    public DownloadInfoHelper() {
        super(TaskWalker.getContext(), DB_CACHE_NAME, null, DB_CACHE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(SQL_CREATE_TABLE);
            db.execSQL(SQL_CREATE_UNIQUE_INDEX);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            db.beginTransaction();
            try {
                if (newVersion > 3) {
                    db.execSQL(SQL_DELETE_UNIQUE_INDEX);
                    db.execSQL(SQL_DELETE_TABLE);
                }
                db.execSQL(SQL_CREATE_TABLE);
                db.execSQL(SQL_CREATE_UNIQUE_INDEX);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Logger.e(e);
            } finally {
                db.endTransaction();
            }
        }
    }
}