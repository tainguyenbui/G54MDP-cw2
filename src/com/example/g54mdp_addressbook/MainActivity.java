package com.example.g54mdp_addressbook;

import library.ContactsContract;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
 * @author Tai Nguyen Bui (psytn2)
 * 
 *         Main activity of the application, it displays the list of contacts and allows the user to add a new one or
 *         access to more details about a specific contact
 * 
 */
public class MainActivity extends Activity {

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Update contacts listview
		updateContactList();

		// Set listener for the listview when an item is clicked
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
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		finish();
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		// Update contacts listview in case a contact has been deleted
		updateContactList();
		super.onStart();
	}

	/**
	 * Function called when Add Contact button is pressed, it starts an Activity to fill information about the contact
	 * 
	 * @param view
	 */
	public void addContact(View view) {
		Intent addContactIntent = new Intent(getApplicationContext(), AddContactActivity.class);
		startActivityForResult(addContactIntent, ContactsContract.SUCCESSFUL_ADD_CONTACT_REQUEST);
	}

	/**
	 * Function called when contact has been added
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == ContactsContract.SUCCESSFUL_ADD_CONTACT_REQUEST) {
			updateContactList();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Update Listview with the latest contacts information from the database
	 */
	private void updateContactList() {
		Cursor cursor = getContentResolver().query(ContactsContract.CONTACTS_URI, ContactsContract.TABLE_COLUMNS, null,
				null, ContactsContract.LISTVIEW_ORDER);

		if (cursor != null) {
			String[] columns = { ContactsContract.KEY_NAME, ContactsContract.KEY_SURNAME,
					ContactsContract.KEY_THUMBNAIL_IMAGE_PATH };
			int[] toViews = { R.id.name, R.id.surname, R.id.contact_icon };

			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.listview_items, cursor, columns,
					toViews, 0);

			// Set adapter to listView
			listView = (ListView) findViewById(R.id.contacts_list);
			listView.setAdapter(adapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_contact_main:
			addContact(this.getCurrentFocus());
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
