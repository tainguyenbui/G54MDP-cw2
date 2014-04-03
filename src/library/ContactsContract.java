package library;

import android.net.Uri;
import android.os.Environment;

public class ContactsContract {
	public static final int CHOOSE_PIC_REQUEST_CODE = 3;

	public static final int THUMBNAIL_SIZE = 120;

	public static final String KEY_CONTACT_ID = "_id";

	public static final String KEY_NAME = "name";

	public static final String KEY_SURNAME = "surname";

	public static final String KEY_TELEPHONE = "telephone";

	public static final String KEY_EMAIL = "email";

	public static final String KEY_THUMBNAIL_IMAGE_PATH = "thumbnail_image_path";

	public static final String KEY_ORIGINAL_IMAGE_PATH = "original_image_path";

	public static final int SUCCESSFUL_ADD_CONTACT_REQUEST = 2;

	// Database Version
	public static final int DATABASE_VERSION = 6;

	// Database Name
	public static final String DATABASE_NAME = "AddressBook-MDP";

	// Contacts table name
	public static final String TABLE_CONTACTS = "contacts";

	// Content provider
	public static final String AUTHORITY = "com.example.g54mdp_addressbook.MyContentProvider";

	public static final Uri CONTACTS_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_CONTACTS);

	public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/MyContentProvider.data.text";

	public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/MyContentProvider.data.text";

	public static final int CONTACTS = 1;

	public static final int CONTACTS_ID = 2;

	public static final String[] TABLE_COLUMNS = { ContactsContract.KEY_CONTACT_ID, ContactsContract.KEY_NAME,
			ContactsContract.KEY_SURNAME, ContactsContract.KEY_TELEPHONE, ContactsContract.KEY_EMAIL,
			ContactsContract.KEY_THUMBNAIL_IMAGE_PATH, ContactsContract.KEY_ORIGINAL_IMAGE_PATH };

	public static final String LISTVIEW_ORDER = ContactsContract.KEY_NAME + " COLLATE NOCASE ASC";

	public static final String SELECTION_BY_ID = ContactsContract.KEY_CONTACT_ID + " = ";

	public static String THUMBNAIL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/contact_thumbnails";

	public static String DEFAULT_ICON_PATH = THUMBNAIL_PATH + "/ic_contact_picture_2.png";
}
