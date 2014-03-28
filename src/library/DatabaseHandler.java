package library;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + Constants.TABLE_CONTACTS + "("
			+ Constants.KEY_CONTACT_ID + " INTEGER PRIMARY KEY," + Constants.KEY_NAME + " TEXT NOT NULL,"
			+ Constants.KEY_SURNAME + " TEXT," + Constants.KEY_TELEPHONE + " TEXT," + Constants.KEY_EMAIL
			+ " TEXT UNIQUE," + Constants.KEY_THUMBNAIL_IMAGE_PATH + " TEXT," + Constants.KEY_ORIGINAL_IMAGE_PATH
			+ " TEXT)";

	public DatabaseHandler(Context context) {
		super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
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
		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CONTACTS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing contact details in database
	 * */
	public void addContact(String name, String surname, String telephone, String email, String thumbnailPath,
			String originalImagePath) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Constants.KEY_NAME, name); // Name
		values.put(Constants.KEY_SURNAME, surname); // Surname
		values.put(Constants.KEY_TELEPHONE, telephone); // Telephone
		values.put(Constants.KEY_EMAIL, email); // Email
		values.put(Constants.KEY_THUMBNAIL_IMAGE_PATH, thumbnailPath);
		values.put(Constants.KEY_ORIGINAL_IMAGE_PATH, originalImagePath);

		Log.d("DatabaseHandler", "Contact Added to database, Name: " + name + " Surname: " + surname + " Telephone: "
				+ telephone + ", Thumbnail path: " + thumbnailPath + ", Original path: " + originalImagePath);

		// Inserting Row
		db.insert(Constants.TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getContactDetails() {
		HashMap<String, String> contacts = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + Constants.TABLE_CONTACTS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			contacts.put(Constants.KEY_CONTACT_ID, cursor.getString(0));
			contacts.put(Constants.KEY_NAME, cursor.getString(1));
			contacts.put(Constants.KEY_SURNAME, cursor.getString(2));
			contacts.put(Constants.KEY_TELEPHONE, cursor.getString(3));
			contacts.put(Constants.KEY_EMAIL, cursor.getString(4));
			contacts.put(Constants.KEY_THUMBNAIL_IMAGE_PATH, cursor.getString(5));
			contacts.put(Constants.KEY_ORIGINAL_IMAGE_PATH, cursor.getString(6));
		}
		cursor.close();
		db.close();
		// return user
		return contacts;
	}

	/**
	 * Getting the number of contacts existent
	 * */
	public int getContactsRowCount() {
		String countQuery = "SELECT  * FROM " + Constants.TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		// return row count
		return rowCount;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(Constants.TABLE_CONTACTS, null, null);
		db.close();
	}

}
