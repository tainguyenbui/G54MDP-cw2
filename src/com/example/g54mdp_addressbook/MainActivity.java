package com.example.g54mdp_addressbook;

import library.ContactsContract;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		updateContactList();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Log.d("MainActivity", "List Item clicked, ID: " + id);
				Intent viewContactDetailsIntent = new Intent(getApplicationContext(), ContactDetailsActivity.class);
				viewContactDetailsIntent.putExtra(ContactsContract.KEY_CONTACT_ID, id);
				startActivity(viewContactDetailsIntent);
			}
		});
	}

	@Override
	protected void onStart() {
		// Update contact list in case a contact has been deleted
		updateContactList();
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void addContact(View view) {
		Intent addContactIntent = new Intent(getApplicationContext(), AddContactActivity.class);
		startActivityForResult(addContactIntent, ContactsContract.SUCCESSFUL_ADD_CONTACT_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == ContactsContract.SUCCESSFUL_ADD_CONTACT_REQUEST) {
			updateContactList();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateContactList() {
		Cursor cursor = getContentResolver().query(ContactsContract.CONTACTS_URI, ContactsContract.TABLE_COLUMNS, null,
				null, ContactsContract.LISTVIEW_ORDER);

		if (cursor != null) {
			String[] columns = { ContactsContract.KEY_NAME, ContactsContract.KEY_SURNAME,
					ContactsContract.KEY_THUMBNAIL_IMAGE_PATH };
			int[] toViews = { R.id.name, R.id.surname, R.id.contact_icon };

			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.listview_items, cursor, columns,
					toViews, 0);

			listView = (ListView) findViewById(R.id.contacts_list);
			listView.setAdapter(adapter);
		}
	}
}
