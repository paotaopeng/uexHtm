package com.wisemen.taskrunner.download.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.wisemen.taskwalker.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class DataBaseDao<T> {

    private SQLiteOpenHelper helper;

    public DataBaseDao(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    protected final SQLiteDatabase openReader() {
        return helper.getReadableDatabase();
    }

    protected final SQLiteDatabase openWriter() {
        return helper.getWritableDatabase();
    }

    protected final void closeDatabase(SQLiteDatabase database, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) cursor.close();
        if (database != null && database.isOpen()) database.close();
    }

    protected abstract String getTableName();

    public int count() {
        return countColumn("id");
    }

    /** 返回一列的总记录数量 */
    public int countColumn(String columnName) {
        String sql = "SELECT COUNT(?) FROM " + getTableName();
        SQLiteDatabase database = openReader();
        Cursor cursor = null;
        try {
            database.beginTransaction();
            cursor = database.rawQuery(sql, new String[]{columnName});
            int count = 0;
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            database.setTransactionSuccessful();
            return count;
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            database.endTransaction();
            closeDatabase(database, cursor);
        }
        return 0;
    }

    /** 删除所有数据 */
    public int deleteAll() {
        return delete(null, null);
    }

    /** 根据条件删除数据库中的数据 */
    public int delete(String whereClause, String[] whereArgs) {
        SQLiteDatabase database = openWriter();
        try {
            database.beginTransaction();
            int result = database.delete(getTableName(), whereClause, whereArgs);
            database.setTransactionSuccessful();
            return result;
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            database.endTransaction();
            closeDatabase(database, null);
        }
        return 0;
    }

    /** 查询并返回所有对象的集合 */
    public List<T> getAll() {
        return get(null, null);
    }

    /** 按条件查询对象并返回集合 */
    public List<T> get(String selection, String[] selectionArgs) {
        return get(null, selection, selectionArgs, null, null, null, null);
    }

    /** 按条件查询对象并返回集合 */
    public List<T> get(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        SQLiteDatabase database = openReader();
        List<T> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            database.beginTransaction();
            cursor = database.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            while (!cursor.isClosed() && cursor.moveToNext()) {
                list.add(parseCursorToBean(cursor));
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            database.endTransaction();
            closeDatabase(database, cursor);
        }
        return list;
    }

    /** 创建或替换一条记录 */
    public long replace(T t) {
        SQLiteDatabase database = openWriter();
        try {
            database.beginTransaction();
            long id = database.replace(getTableName(), null, getContentValues(t));
            database.setTransactionSuccessful();
            return id;
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            database.endTransaction();
            closeDatabase(database, null);
        }
        return 0;
    }

    /** 创建一条记录 */
    public long create(T t) {
        SQLiteDatabase database = openWriter();
        try {
            database.beginTransaction();
            long id = database.insert(getTableName(), null, getContentValues(t));
            database.setTransactionSuccessful();
            return id;
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            database.endTransaction();
            closeDatabase(database, null);
        }
        return 0;
    }

    /** 更新一条记录 */
    public int update(T t, String whereClause, String[] whereArgs) {
        SQLiteDatabase database = openWriter();
        try {
            database.beginTransaction();
            int count = database.update(getTableName(), getContentValues(t), whereClause, whereArgs);
            database.setTransactionSuccessful();
            return count;
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            database.endTransaction();
            closeDatabase(database, null);
        }
        return 0;
    }

    /* 根据SQL查询数据*/
    public List<T> query(String sql, String[] selectionArgs) {
        SQLiteDatabase database = openReader();
        List<T> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            database.beginTransaction();
            cursor = database.rawQuery(sql, selectionArgs);
            while (!cursor.isClosed() && cursor.moveToNext()) {
                list.add(parseCursorToBean(cursor));
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            database.endTransaction();
            closeDatabase(database, cursor);
        }
        return list;
    }

    /** 执行一条数据更新 */
    public void exec(String sql) {
        SQLiteDatabase database = openWriter();
        try {
            database.beginTransaction();
            database.execSQL(sql);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            database.endTransaction();
            closeDatabase(database, null);
        }
    }

    /** 执行一条数据更新 */
    public void exec(String sql, Object[] bindArgs) {
        SQLiteDatabase database = openWriter();
        try {
            database.beginTransaction();
            database.execSQL(sql, bindArgs);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            database.endTransaction();
            closeDatabase(database, null);
        }
    }

    /** 批量执行多条数据更新 */
    public void execBatch(List<String> sqlList) {
        if(sqlList != null && sqlList.size() > 0) {
            SQLiteDatabase database = openWriter();
            try {
                database.beginTransaction();
                for(String sql : sqlList) {
                    database.execSQL(sql);
                }
                database.setTransactionSuccessful();
            } catch (Exception e) {
                Logger.e(e);
            } finally {
                database.endTransaction();
                closeDatabase(database, null);
            }
       }
    }

    /** 批量执行多条数据更新 */
    public void execBatch(String sql, List<String[]> bindArgsList) {
        if(bindArgsList != null && bindArgsList.size() > 0) {
            SQLiteDatabase database = openWriter();
            SQLiteStatement stat = database.compileStatement(sql);
            try {
                database.beginTransaction();
                for(String[] bindArgs : bindArgsList) {
                    stat.bindAllArgsAsStrings(bindArgs);
                    stat.execute();
                }
                database.setTransactionSuccessful();
            } catch (Exception e) {
                Logger.e(e);
            } finally {
                database.endTransaction();
                closeDatabase(database, null);
            }
        }
    }

    /** 将Cursor解析成对应的JavaBean */
    public abstract T parseCursorToBean(Cursor cursor);

    /** 需要替换的列 */
    public abstract ContentValues getContentValues(T t);

}