package com.example.misha.funmatchgame;
/**
 * Created by Misha on 8/2/2016.
 */
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOutOfMemoryException;
import android.database.sqlite.SQLiteTableLockedException;

public class DBHelper extends SQLiteOpenHelper {
//
    public static final String DATABASE_NAME = "FunMatchGame.db";
    public static final String SCORE_TABLE_NAME = "scores";
    public static final String SCORE_COLUMN_ID = "id";
    public static final String SCORE_COLUMN_NAME = "name";
    public static final String SCORE_COLUMN_LEVEL_SELECTED = "level";
    public static final String SCORE_COLUMN_END_TIME = "winTime";
    public static final String SCORE_COLUMN_DATE = "date";
    public static final String SCORE_COLUMN_ATTEMPTS = "attempts";
    public static final int SCORE_BOARD_MAX_LIST_SIZE = 10;


    public DBHelper(Context context) throws SQLiteDatabaseCorruptException,SQLiteCantOpenDatabaseException{
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table scores " +
                        "(id integer primary key, name text,level text,winTime long, date text,attempts text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertScore  (String name, String level, Long winTime, String date,Integer attempts) throws SQLiteTableLockedException,SQLiteOutOfMemoryException,SQLiteFullException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("level", level);
        contentValues.put("winTime", winTime);
        contentValues.put("date", date);
        contentValues.put("attempts", attempts);
        long test =  db.insert("scores", null, contentValues);
        return true;
    }

    public Cursor getDataByID(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from scores where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SCORE_TABLE_NAME);
        return numRows;
    }

    public boolean updateScore (Integer id, String name, String level, String winTime, String date,String attempts)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("level", level);
        contentValues.put("winTime", winTime);
        contentValues.put("date", date);
        contentValues.put("attempts", attempts);
        db.update("scores", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteScore (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("scores",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllScores()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from scores", null );
        res.moveToFirst();
        String [] test = res.getColumnNames();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(SCORE_COLUMN_NAME))
                    +":"+res.getString(res.getColumnIndex(SCORE_COLUMN_END_TIME))
                    +":"+res.getString(res.getColumnIndex(SCORE_COLUMN_LEVEL_SELECTED)));
            res.moveToNext();
        }
        return array_list;
    }


    public ArrayList<String> getTop10ScoresByLevel(String level) throws SQLiteDatabaseCorruptException,SQLiteBindOrColumnIndexOutOfRangeException {
        ArrayList<String> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql="select * from "+SCORE_TABLE_NAME
                                    +" where " +SCORE_COLUMN_LEVEL_SELECTED+" like '"+level+"' order by "+SCORE_COLUMN_END_TIME
                                    +" limit "+SCORE_BOARD_MAX_LIST_SIZE;

        Cursor res =  db.rawQuery( sql, null );
        /*Cursor res =  db.rawQuery( "select top 10 * from scores where (select * from scores where level="+level+") order by "
                                                        +SCORE_COLUMN_END_TIME, null );*/
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(SCORE_COLUMN_NAME))
                    +":"+res.getString(res.getColumnIndex(SCORE_COLUMN_END_TIME))
                    +":"+res.getString(res.getColumnIndex(SCORE_COLUMN_LEVEL_SELECTED)));
            res.moveToNext();
        }
        return array_list;
    }
}