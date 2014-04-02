package library;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + ContactsContract.TABLE_CONTACTS + "("
			+ ContactsContract.KEY_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + ContactsContract.KEY_NAME + " TEXT NOT NULL,"
			+ ContactsContract.KEY_SURNAME + " TEXT," + ContactsContract.KEY_TELEPHONE + " TEXT," + ContactsContract.KEY_EMAIL
			+ " TEXT UNIQUE," + ContactsContract.KEY_THUMBNAIL_IMAGE_PATH + " TEXT," + ContactsContract.KEY_ORIGINAL_IMAGE_PATH
			+ " TEXT)";

	public DatabaseHandler(Context context) {
		super(context, ContactsContract.DATABASE_NAME, null, ContactsContract.DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + ContactsContract.TABLE_CONTACTS);

		// Create tables again
		onCreate(db);
	}

}
