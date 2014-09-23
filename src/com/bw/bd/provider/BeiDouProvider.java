/*
 */

package com.bw.bd.provider;

import java.util.ArrayList;

import com.bw.bd.activity.MessageEntity;
import com.bw.bd.provider.BdsMessage;
import com.bw.bd.provider.BdsMessage.ShortMessage;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
/**
 * 
 * @author yujin
 *
 */
public class BeiDouProvider extends ContentProvider {
	private static final String TAG = "BdsProvider";
	private static final boolean DBG = true;

	// Table names
	public static final String TABLE_SHORT_MESSAGE = "shortMessages";

	// URI IDs
	private static final int URI_MESSAGE = 0;

	private SQLiteOpenHelper mOpenHelper;

	private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sURLMatcher.addURI(BdsMessage.AUTH, "shortMessages", URI_MESSAGE);
	}

	@Override
	public final boolean onCreate() {
		Log.i(TAG, "----onCreate---");
		mOpenHelper = BdsDatabaseHelper.getInstance(getContext());
		return mOpenHelper != null;
	}

	@Override
	public final String getType(Uri uri) {
		final int match = sURLMatcher.match(uri);
		switch (match) {
		case URI_MESSAGE:
			return ShortMessage.CONTENT_TYPE;

			// case URI_ACCOUNTS_AID:
			// return ShortMessage.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI");
		}
	}

	private void notifyChange(Uri uri) {
		ContentResolver cr = getContext().getContentResolver();
		cr.notifyChange(uri, null);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// List<String> segs = uri.getPathSegments();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final int match = sURLMatcher.match(uri);

		switch (match) {
		case URI_MESSAGE:
			qb.setTables(TABLE_SHORT_MESSAGE);
			//qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
			break;

		// case URI_ACCOUNTS_AID:
		// qb.setTables(TABLE_ACCOUNT);
		// qb.appendWhere(Account._ID + "=" + segs.get(1));
		// break;

		default:
			throw new IllegalStateException("Unrecognized URI:" + uri);
		}

		String orderBy = null;
		if (!TextUtils.isEmpty(sortOrder)) {
			orderBy = sortOrder;
		} else if (qb.getTables().equals(TABLE_SHORT_MESSAGE)) {
			orderBy = ShortMessage.DATE + " DESC";
		}
		Cursor ret = qb.query(db, projection, null, null, null, null, null);
		ret.setNotificationUri(getContext().getContentResolver(), uri);
		return ret;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		int match = sURLMatcher.match(uri);
		if (DBG) {
			Log.i(TAG, "insert uri=" + uri + ", match=" + match);
		}

		switch (match) {
		case URI_MESSAGE:
			return insertShortMessage(uri, initialValues);

		default:
			throw new IllegalArgumentException("Invalid request: " + uri);
		}
	}

	private Uri insertShortMessage(Uri uri, ContentValues initialValues) {
		uri = simpleInsert(TABLE_SHORT_MESSAGE, uri, initialValues);
		notifyChange(uri);
		return uri;
	}

	private Uri simpleInsert(String table, Uri uri, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(table, null, initialValues);
		if (rowId <= 0) {
			if (DBG)
				Log.e(TAG, "MailProvider.insert: failed! " + initialValues);
			return null;
		}

		Uri newUri = ContentUris.withAppendedId(uri, rowId);
		notifyChange(newUri);
		return newUri;
	}

	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		// List<String> segs = uri.getPathSegments();
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int count = 0;
		final int match = sURLMatcher.match(uri);
		switch (match) {
		case URI_MESSAGE:
			count = db.delete(TABLE_SHORT_MESSAGE, whereClause, whereArgs);
			break;

		default:
			throw new UnsupportedOperationException("DELETE not supported for " + uri);
		}

		if (count > 0) {
			notifyChange(uri);
		}

		return count;
	}

	@Override
	public synchronized ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		if (DBG) {
			Log.i(TAG, "applyBatch");
		}
		final int numOperations = operations.size();
		final ContentProviderResult[] results = new ContentProviderResult[numOperations];
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (int i = 0; i < numOperations; i++) {
				results[i] = operations.get(i).apply(this, results, i);
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public final int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		// List<String> segs = uri.getPathSegments();
		final int match = sURLMatcher.match(uri);

		int count = 0;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (match) {
		case URI_MESSAGE:
			count = db.update(TABLE_SHORT_MESSAGE, values, where, null);
			break;

		default:
			throw new UnsupportedOperationException("UPDATE not supported for " + uri);
		}

		if (count > 0) {
			notifyChange(uri);
		}
		return count;
	}
	
	 private void revertSeq() {
		  String sql = "update sqlite_sequence set seq=0 where name='"+TABLE_SHORT_MESSAGE+"'";
		  SQLiteDatabase db =  mOpenHelper.getWritableDatabase();
		  db.execSQL(sql);
	}
}
