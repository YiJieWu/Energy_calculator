package thehabitslab.com.codebase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Helper for the energy database.
 * Steps to reproduce can be found in the tutorial
 * <a href="https://developer.android.com/training/basics/data-storage/databases.html">here</a>.
 * <p/>
 * In this file, the students should write the queries to carry out necessary operations. Currently,
 * they are not required to write queries to create the database and the contract is given to them
 * in order to maintain consistency among solutions.
 * <p/>
 * Created by William on 12/29/2016
 */
public class EnergyDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    /* ********************************* DATABASE STRUCTURE *********************************** */
    // DB metadata
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "energy.db";

    // SQL instructions for creation and deletion of the table
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EnergyEntry.TABLE_NAME + " (" +
                    EnergyEntry.COLUMN_NAME_ENERGY + " DOUBLE, " +
                    EnergyEntry.COLUMN_NAME_TIME + " TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EnergyEntry.TABLE_NAME;

    /**
     * Maintains the instance for the singleton model
     */
    private static EnergyDBHelper instance = null;

    /**
     * Returns the current instance of the DBHelper
     *
     * @param context of the app
     * @return the helper
     */
    public static EnergyDBHelper getInstance(Context context) {
        if (instance == null) instance = new EnergyDBHelper(context);
        return instance;
    }

    /**
     * Inner class for table contents
     */
    public class EnergyEntry implements BaseColumns {
        public static final String TABLE_NAME = "entries";
        public static final String COLUMN_NAME_ENERGY = "energy";
        public static final String COLUMN_NAME_TIME = "date";
    }

    private EnergyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * Called when the database version increases
     *
     * @param db         database
     * @param oldVersion previous version number
     * @param newVersion current version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    /**
     * Called when the database version decreases
     *
     * @param db         database
     * @param oldVersion previous version number
     * @param newVersion current version number
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /* *********************************** INTERRACTION METHODS ****************************** */

    /**
     * Adds an energy value to the current database
     *
     * @param energy  class instance representing the energy
     * @param context of the application
     */
    public static void enterEnergy(EnergyReading energy, Context context) {
        // TODO: write the data in the EnergyReading into the database

        //get the database instance
        SQLiteDatabase db=getInstance(context).getWritableDatabase();
        //create a new value to be inserted
        ContentValues values=new ContentValues();
        //store the actual value in the corresponding columns
        values.put(EnergyEntry.COLUMN_NAME_ENERGY,energy.getEnergy());
        values.put(EnergyEntry.COLUMN_NAME_TIME,energy.getDate());
        //insert the data into db
        db.insert(EnergyEntry.TABLE_NAME,null,values);
    }

    /**
     * Queries the database for the last 60 entries by datetime. Method taken from tutorial
     * <a href="https://developer.android.com/training/basics/data-storage/databases.html#DbHelper>here</a>.
     *
     * @param context of the application
     * @return a @Cursor containing the data
     */
    public static Cursor getLatest60Entries(Context context) {
        // TODO: query for the most recent 60 entries and return the cursor

        //get the database instance
        SQLiteDatabase db=getInstance(context).getWritableDatabase();
        String[] projection = {
                EnergyEntry.COLUMN_NAME_ENERGY,
                EnergyEntry.COLUMN_NAME_TIME
        };


        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                EnergyEntry.COLUMN_NAME_TIME + " DESC";

        String limit = "60";

        Cursor cursor = db.query(
                EnergyEntry.TABLE_NAME,                     // The table to query
                projection,                                 // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                        // don't filter by row groups
                sortOrder,                                  //sort order
                limit                                       // The sort order
        );

        return cursor;
    }

    /**
     * Queries for the first 60 entries by datetime. For info about the query() method, see
     * <a href="https://developer.android.com/training/basics/data-storage/databases.html#DbHelper">this</a>
     * link.
     *
     * @param context of the application
     * @return a Cursor containing the data
     */
    public static Cursor getFirst60Entries(Context context) {
        // TODO: query for the oldest 60 entries and return the cursor

        //get the database instance
        SQLiteDatabase db=getInstance(context).getWritableDatabase();
        String[] projection = {
                EnergyEntry.COLUMN_NAME_ENERGY,
                EnergyEntry.COLUMN_NAME_TIME
        };

        String limit = "60";

        Cursor cursor = db.query(
                EnergyEntry.TABLE_NAME,                     // The table to query
                projection,                                 // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                        // don't filter by row groups
                null,                                 //sort order
                limit                                       // The sort order
        );

        return cursor;
    }

    /**
     * Deletes the n oldest entries from the sqlite database. This should be used after successfully
     * backing up the entries to the server
     *
     * @param context of the application
     * @param n       number of entries to delete
     */
    public static void deleteNEntries(Context context, int n) {
        // TODO: delete the n oldest entries
        SQLiteDatabase db=getInstance(context).getWritableDatabase();
        String query ="delete from " + EnergyEntry.TABLE_NAME + " where "+EnergyEntry.COLUMN_NAME_TIME+
                " in (select "+ EnergyEntry.COLUMN_NAME_TIME +" from " + EnergyEntry.TABLE_NAME+
                " order by "+EnergyEntry.COLUMN_NAME_TIME +" LIMIT "+n+")";

        db.execSQL(query);
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}

