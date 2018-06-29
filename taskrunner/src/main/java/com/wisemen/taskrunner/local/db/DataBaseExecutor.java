package com.wisemen.taskrunner.local.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.wisemen.taskrunner.download.db.DataBaseDao;

import java.util.List;


public class DataBaseExecutor<T> {
    private DataBaseExecutor() {}

    /** 执行一条数据更新 */
    public static void exec(SQLiteOpenHelper helper, String sql) {
        DefaultDataBaseDao dao = new DefaultDataBaseDao(helper);
        dao.exec(sql);
    }

    /** 执行一条数据更新 */
    public static void exec(SQLiteOpenHelper helper, String sql, Object[] bindArgs) {
        DefaultDataBaseDao dao = new DefaultDataBaseDao(helper);
        dao.exec(sql, bindArgs);
    }

    /** 批量执行多条数据更新 */
    public static void execBatch(SQLiteOpenHelper helper, List<String> sqlList) {
        DefaultDataBaseDao dao = new DefaultDataBaseDao(helper);
        dao.execBatch(sqlList);
    }

    /** 批量执行多条数据更新 */
    public static void execBatch(SQLiteOpenHelper helper, String sql, List<String[]> bindArgsList) {
        DefaultDataBaseDao dao = new DefaultDataBaseDao(helper);
        dao.execBatch(sql, bindArgsList);
    }

    /** 根据条件删除数据库中的数据 */
    public static int delete(SQLiteOpenHelper helper, String whereClause, String[] whereArgs) {
        DefaultDataBaseDao dao = new DefaultDataBaseDao(helper);
        return dao.delete(whereClause, whereArgs);
    }

    /** 删除所有数据 */
    public static int deleteAll(SQLiteOpenHelper helper) {
        DefaultDataBaseDao dao = new DefaultDataBaseDao(helper);
        return dao.deleteAll();
    }

}

class DefaultDataBaseDao<T> extends DataBaseDao<T> {
    public DefaultDataBaseDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public T parseCursorToBean(Cursor cursor) {
        return null;
    }

    @Override
    public ContentValues getContentValues(T t) {
        return null;
    }
}