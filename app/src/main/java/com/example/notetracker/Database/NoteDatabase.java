package com.example.notetracker.Database;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.notetracker.Model.Note;

import java.util.ArrayList;


public class NoteDatabase extends SQLiteOpenHelper {


    private	static final String	DB_NAME = "note";
    private	static final String NOTES_TABLE = "notes";
    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private	static final int VERSION =	5;
    public NoteDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String	CREATE_NOTES_TABLE = "CREATE TABLE " + NOTES_TABLE + "(" + ID + " INTEGER PRIMARY KEY," + TITLE + " TEXT," + DESCRIPTION + " INTEGER" + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
        onCreate(db);
    }

    public ArrayList<Note> noteList(){
        String sql = "select * from " + NOTES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Note> storeNotes = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                int id = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String description = cursor.getString(2);
                storeNotes.add(new Note(id, title, description));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return storeNotes;
    }

    public void noteAdd(Note notes){
        ContentValues values = new ContentValues();
        values.put(TITLE, notes.getTitle());
        values.put(DESCRIPTION, notes.getDescription());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(NOTES_TABLE, null, values);
    }

    public void noteUpdate(Note notes) throws Exception {
        ContentValues values = new ContentValues();
        String passKey = "asd@Q#RS!%G5yi^UE%WT$$";
        values.put(TITLE, Encryption.encTitle(notes.getTitle(), passKey));
        values.put(DESCRIPTION, Encryption.encDesc(notes.getDescription(), passKey));
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(NOTES_TABLE, values, ID	+ "	= ?", new String[] { String.valueOf(notes.getId())});
    }


    public void noteDelete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTES_TABLE, ID	+ "	= ?", new String[] { String.valueOf(id)});
    }
}
