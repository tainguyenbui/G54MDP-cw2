package library;

import android.net.Uri;

public class Constants {
	public static final int CHOOSE_PIC_REQUEST_CODE = 3;

	public static final int THUMBNAIL_SIZE = 120;

	public static final String KEY_CONTACT_ID = "contact_id";

	public static final String KEY_NAME = "name";

	public static final String KEY_SURNAME = "surname";

	public static final String KEY_TELEPHONE = "telephone";

	public static final String KEY_EMAIL = "email";

	public static final String KEY_THUMBNAIL_IMAGE_PATH = "thumbnail_image_path";

	public static final String KEY_ORIGINAL_IMAGE_PATH = "original_image_path";

	// Database Version
	public static final int DATABASE_VERSION = 1;

	// Database Name
	public static final String DATABASE_NAME = "AddressBook-MDP";

	// Contacts table name
	public static final String TABLE_CONTACTS = "contacts";

	// Content provider
	public static final String AUTHORITY = "com.example.g54mdp_addressbook.MyContentProvider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_CONTACTS);
	
	public static final int CONTACTS = 1;
	public static final int CONTACTS_ID = 2;
}
