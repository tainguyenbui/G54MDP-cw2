package com.example.g54mdp_addressbook;

import library.ContactsContract;
import library.DatabaseHandler;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {

	private DatabaseHandler dbHandler = null;

	private static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(ContactsContract.AUTHORITY, ContactsContract.TABLE_CONTACTS, ContactsContract.CONTACTS);
		uriMatcher.addURI(ContactsContract.AUTHORITY, ContactsContract.TABLE_CONTACTS + "/#",
				ContactsContract.CONTACTS_ID);
	}

	@Override
	public boolean onCreate() {
		this.dbHandler = new DatabaseHandler(this.getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		String contentType;
		if (uri.getLastPathSegment() == null) {
			contentType = ContactsContract.CONTENT_TYPE_MULTIPLE;
		}
		else {
			contentType = ContactsContract.CONTENT_TYPE_SINGLE;
		}
		return contentType;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d("MyContentProvider", "insert");
		SQLiteDatabase db = dbHandler.getWritableDatabase();
		long rowID = db.insert(ContactsContract.TABLE_CONTACTS, null, values);
		db.close();
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(uri, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		else {
			throw new SQLException("Failed to insert contact into " + uri);
		}

	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d("MyContentProvider", "query");

		SQLiteDatabase db = dbHandler.getWritableDatabase();
		Cursor cursor = db.query(ContactsContract.TABLE_CONTACTS, projection, selection, selectionArgs, null, null,
				sortOrder, null);
		cursor.moveToFirst();
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Log.d("MyContentProvider", "update");
		int res = 0;
		if (uriMatcher.match(uri) == 1) {
			SQLiteDatabase db = dbHandler.getWritableDatabase();
			res = db.update(ContactsContract.TABLE_CONTACTS, values, selection, selectionArgs);
			Log.d("MyContentProvider", "Contact Updated");
			db.close();
		}
		else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return res;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d("MyContentProvider", "delete");
		int res = 0;
		if (uriMatcher.match(uri) == 1) {
			SQLiteDatabase db = dbHandler.getWritableDatabase();
			res = db.delete(ContactsContract.TABLE_CONTACTS, selection, selectionArgs);
			db.close();
		}
		else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return res;
	}

}
