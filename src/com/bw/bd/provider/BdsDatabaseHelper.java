/**
 * 
 */

package com.bw.bd.provider;

import com.bw.bd.provider.BdsMessage.ShortMessage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The helper class for creating/updating db and tables.
 * 
 * @author yujin
 */
public class BdsDatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "BdsDatabaseHelper";

	private static final String DATABASE_NAME = "beidou_message.db";

	private static final int DATABASE_VERSION = 1;

	private static BdsDatabaseHelper mInstance = null;
	private Context mContext;

	private BdsDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	/**
	 * Return a singleton helper for the Bds database.
	 */
	public static synchronized BdsDatabaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new BdsDatabaseHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "----onCreate---");
		createTables(db);
	}

	/**
	 * Create message tables.
	 * 
	 * @param db
	 *            The database
	 */

	private void createTables(SQLiteDatabase db) {
		Log.i(TAG, "---createTables---");
		Log.i(TAG, "\n\nSQL: " + "CREATE TABLE " + BeiDouProvider.TABLE_SHORT_MESSAGE + " (" + ShortMessage._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ShortMessage.FOLDER + " INTEGER,"
				+ ShortMessage.IS_READ + " INTEGER,"
				+ ShortMessage.DATE + " TEXT,"
				+ ShortMessage.FROM_ID + " TEXT,"
				+ ShortMessage.FROM_NAME + " TEXT,"
				+ ShortMessage.TO_ID + " TEXT,"
				+ ShortMessage.TO_NAME + " TEXT,"
				+ ShortMessage.CONTENT + " TEXT);\n\n");
		db.execSQL("CREATE TABLE " + BeiDouProvider.TABLE_SHORT_MESSAGE + " (" + ShortMessage._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ShortMessage.FOLDER + " INTEGER,"
				+ ShortMessage.IS_READ + " INTEGER,"
				+ ShortMessage.DATE + " TEXT,"
				+ ShortMessage.FROM_ID + " TEXT,"
				+ ShortMessage.FROM_NAME + " TEXT,"
				+ ShortMessage.TO_ID + " TEXT,"
				+ ShortMessage.TO_NAME + " TEXT,"
				+ ShortMessage.CONTENT + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
