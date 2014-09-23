/*
 * 
 */

package com.bw.bd.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Definitions of the messages columns and helper functions
 * 
 * @author yujin
 */
public final class BdsMessage {
	public static final String AUTH = "com.bw.bd.send";

	// short message
	public static final String AUTHORITY_PLUS_SHORTMESSAGE = "content://" + AUTH + "/shortMessages/";

	/**
	 * The content:// style URL for this provider
	 */
	public static final Uri CONTENT_URI_BASE = Uri.parse("content://" + AUTH);

	private interface ShortMessageColumns extends BaseColumns {

		/**
		 * FOLDER: the type of short message, 1: received from others; 2: sent
		 * to others
		 */
		public static final String FOLDER = "folder";

		/**
		 * Time for the short message sent/received
		 */
		public static final String DATE = "date";

		/**
		 * The short message's content
		 */
		public static final String CONTENT = "content";

		/**
		 * The from user ID of the received short message
		 */
		public static final String FROM_ID = "fromId";
		public static final String FROM_NAME = "send_name";
		/**
		 * The receiver user ID for the sent short message
		 */
		public static final String TO_ID = "toId";
		public static final String TO_NAME = "receive_name";
		/**
		 * whether the message is readed
		 */
		public static final String IS_READ = "isRead";
	}

	public static class ShortMessage implements ShortMessageColumns {
		public static final int FOLDER_INBOX = 1;
		public static final int FOLDER_SENT = 2;

		private static final Uri CONTENT_URI = Uri.parse(AUTHORITY_PLUS_SHORTMESSAGE);

		/**
		 * Mime type for this type of data
		 */
		public static final String CONTENT_TYPE = "vnd." + AUTH + ".dir/shortMessages";

		/**
		 * Mime type for the type of a specified data
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd." + AUTH + ".item/shortMessages";

		/**
		 * Returns the URI to access all accounts.
		 */
		public static final Uri getUri() {
			return ShortMessage.CONTENT_URI;
		}

		/**
		 * @param cr
		 * @return messages in inbox
		 */
		public static final Cursor getMessagesInInbox(ContentResolver cr) {
			return cr.query(ShortMessage.CONTENT_URI, null, ShortMessage.FOLDER + "=" + ShortMessage.FOLDER_INBOX,
					null, null);
		}

		/**
		 * @param cr
		 * @return sent messages
		 */
		public static final Cursor getSentMessages(ContentResolver cr) {
			return cr.query(ShortMessage.CONTENT_URI, null, ShortMessage.FOLDER + "=" + ShortMessage.FOLDER_SENT, null,
					null);
		}

		/**
		 * Updates values of a message
		 */
		public static final int updateMessage(ContentResolver cr, Uri uri, ContentValues values) {
			return cr.update(uri, values, null, null);
		}

		/**
		 * Removes one account and all related data.
		 */
		public static final int deleteAccount(ContentResolver cr, long id) {
			return cr.delete(ShortMessage.getUri(), ShortMessage._ID + "=" + id, null);
		}
	}
}
