package com.example.g54mdp_addressbook;

import java.io.File;

import library.ContactImageData;
import library.ContactsContract;
import library.ImageHelper;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 
 * @author Tai Nguyen Bui (psytn2)
 * 
 *         Activity to display all the details of a specific contact. The details can be modified.
 * 
 */
public class ContactDetailsActivity extends Activity {

	private EditText nameLabel, surnameLabel, telephoneLabel, emailLabel;

	private ImageView contactImageView, callIcon, emailIcon;

	private Button editContactBtn, finishEditBtn, deleteContactBtn;

	private String email, thumbnailImagePath, originalImagePath;

	private Long telephone, _id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details);

		// Get the ID of the contact clicked in the listView
		Bundle extras = getIntent().getExtras();
		_id = extras.getLong(ContactsContract.KEY_CONTACT_ID);
		setContactData(_id);

		// Initialise listeners of the buttons
		setIconListeners();

		// Hide Keyboard when the application starts
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * Populate contact details by putting the details of the contact in the EditText fields
	 * 
	 * @param _id of the contact in the database
	 */
	private void setContactData(long _id) {

		// Get contact details through the content provider
		Cursor cursor = getContentResolver().query(ContactsContract.CONTACTS_URI, ContactsContract.TABLE_COLUMNS,
				ContactsContract.SELECTION_BY_ID + _id, null, ContactsContract.LISTVIEW_ORDER);
		Log.d("ContactDetailsActivity", "setContactData id of user: " + _id);
		if (cursor != null) {
			if (cursor.getCount() > 0) {

				// Set editText fields with the contact details retrieved
				nameLabel = (EditText) findViewById(R.id.nameLabel);
				nameLabel.setText(cursor.getString(1));

				surnameLabel = (EditText) findViewById(R.id.surnameLabel);
				surnameLabel.setText(cursor.getString(2));

				this.telephone = Long.parseLong(cursor.getString(3));
				telephoneLabel = (EditText) findViewById(R.id.telephoneLabel);
				telephoneLabel.setText(cursor.getString(3));

				this.email = cursor.getString(4);
				emailLabel = (EditText) findViewById(R.id.emailLabel);
				emailLabel.setText(email);

				this.thumbnailImagePath = cursor.getString(5);
				this.originalImagePath = cursor.getString(6);

				// Set the labels not editable
				setContactFieldsEditable(false);

				this.contactImageView = (ImageView) findViewById(R.id.contactImage);
				contactImageView.setImageURI(Uri.parse(cursor.getString(5)));
			}
		}
		cursor.close();

	}

	/**
	 * Set the icon listeners for Call, contact image and email
	 * 
	 * @param telephone
	 * @param email
	 */
	private void setIconListeners() {

		// Call icon listener
		callIcon = (ImageView) findViewById(R.id.call_icon);
		callIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callContact();
			}

		});

		// Email icon listener
		emailIcon = (ImageView) findViewById(R.id.email_icon);
		emailIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				emailContact();
			}
		});

		// Contact image listener
		contactImageView = (ImageView) findViewById(R.id.contactImage);
		contactImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent imageViewIntent = new Intent();
				imageViewIntent.setAction(Intent.ACTION_VIEW);
				File file = new File(originalImagePath);
				imageViewIntent.setDataAndType(Uri.fromFile(file), "image/*");
				startActivity(imageViewIntent);
			}
		});

	}

	/**
	 * Method to call the contact, used by the listeners of the icon and the activity menu
	 */
	private void callContact() {
		if (telephone != null) {
			Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telephone));
			startActivity(callIntent);
		}
		else {
			Toast.makeText(getApplicationContext(), "Incorrect telephone number", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Method to delete the contact, used by the listeners of the button and the activity menu
	 */
	private void emailContact() {
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
		startActivity(Intent.createChooser(emailIntent, "Send email..."));
	}

	/**
	 * Edit contact details, called when button "edit contact is pressed"
	 * 
	 * @param view
	 */
	public void editContact(View view) {
		editContactBtn = (Button) findViewById(R.id.editContactBtn);
		editContactBtn.setVisibility(View.GONE);

		deleteContactBtn = (Button) findViewById(R.id.deleteContactBtn);
		deleteContactBtn.setVisibility(View.VISIBLE);

		finishEditBtn = (Button) findViewById(R.id.finishEditBtn);
		finishEditBtn.setVisibility(View.VISIBLE);

		// Set contact labels editable
		setContactFieldsEditable(true);

		// Change the listener of the contact image in order to be able to be modified
		contactImageView = (ImageView) findViewById(R.id.contactImage);
		contactImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent imagePickerIntent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(imagePickerIntent, ContactsContract.CHOOSE_PIC_REQUEST_CODE);
			}
		});
	}

	/**
	 * Method called when a new image has been selected from the gallery
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == ContactsContract.CHOOSE_PIC_REQUEST_CODE && data != null) {
			Uri pictureUri = data.getData();

			ImageHelper imageHelper = new ImageHelper(getContentResolver());
			ContactImageData contactImageData = imageHelper.createThumbnail(pictureUri);

			String oldThumbnailPath = thumbnailImagePath;

			// update with the last image paths
			this.originalImagePath = contactImageData.getOriginalImagePath();
			this.thumbnailImagePath = contactImageData.getThumbnailImagePath();

			// Set the new contact image
			this.contactImageView = (ImageView) findViewById(R.id.contactImage);
			contactImageView.setImageBitmap(contactImageData.getThumbnailImage());

			// Delete old contact image to save space in memory
			if (!oldThumbnailPath.equals(thumbnailImagePath) && !thumbnailImagePath.contains("picture_2.png")) {
				File file = new File(oldThumbnailPath);
				file.delete();
			}

			Log.d("ContactDetailsActivity", "Update Image requestcode" + requestCode + " resultcode " + resultCode
					+ " " + pictureUri.toString());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Finish editing the contact
	 * 
	 * @param view
	 */
	public void finishContactEdit(View view) {

		// Update contact details
		if (updatedContactData()) {
			editContactBtn = (Button) findViewById(R.id.editContactBtn);
			editContactBtn.setVisibility(View.VISIBLE);

			deleteContactBtn = (Button) findViewById(R.id.deleteContactBtn);
			deleteContactBtn.setVisibility(View.GONE);

			finishEditBtn = (Button) findViewById(R.id.finishEditBtn);
			finishEditBtn.setVisibility(View.GONE);

			// Set the labels not editable
			setContactFieldsEditable(false);

			// Update listeners with new data
			setIconListeners();
		}
	}

	/**
	 * Update the details of the Contact in the database
	 */
	private boolean updatedContactData() {
		String name = nameLabel.getText().toString();
		Long telephone = Long.parseLong(telephoneLabel.getText().toString());
		String email = emailLabel.getText().toString();
		String surname = surnameLabel.getText().toString();

		// If the input is valid, updated the database with the new contact details
		if (validContactDetails(name, telephone, email)) {
			ContentValues values = new ContentValues();
			values.put(ContactsContract.KEY_NAME, name); // Name
			values.put(ContactsContract.KEY_SURNAME, surname); // Surname
			values.put(ContactsContract.KEY_TELEPHONE, telephone); // Telephone
			values.put(ContactsContract.KEY_EMAIL, email); // Email
			values.put(ContactsContract.KEY_THUMBNAIL_IMAGE_PATH, thumbnailImagePath); // Thumbnail
			values.put(ContactsContract.KEY_ORIGINAL_IMAGE_PATH, originalImagePath); // original image

			int update = getContentResolver().update(ContactsContract.CONTACTS_URI, values,
					ContactsContract.SELECTION_BY_ID + _id, null);

			// If update is successful show Toast
			if (update == 1) {
				Log.d("ContactDetailsActivity", update + " rows updated, id of the user: " + _id);
				Toast.makeText(getApplicationContext(), "Contact updated", Toast.LENGTH_LONG).show();

				// update icon listeners
				this.telephone = telephone;
				this.email = email;
				setIconListeners();
			}
			return true;
		}
		return false;
	}

	/**
	 * Delete contact from phonebook and database. Thumbnail of the contact is also removed to save space
	 * 
	 * @param view
	 */
	public void deleteContact(View view) {

		// Create dialog to verify that the user wants to delete de contact
		AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetailsActivity.this);
		builder.setMessage("Delete contact?").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						int delete = getContentResolver().delete(ContactsContract.CONTACTS_URI,
								ContactsContract.SELECTION_BY_ID + _id, null);

						// If the contact is deleted, delete the thumbnail of the contact and display a Toast
						if (delete == 1) {

							// Remove from external memory Thumbnail of deleted contact
							if (!thumbnailImagePath.contains("picture_2.png")) {
								File file = new File(thumbnailImagePath);
								if (file.delete()) {
									Log.d("ContactDetailsActivity", "DeleteContact, Thumbnail " + thumbnailImagePath
											+ " deleted");
								}
								else {
									Log.d("ContactDetailsActivity", "DeleteContact, Thumbnail " + thumbnailImagePath
											+ " could not be deleted");
								}
							}

							// Display information about sucessful deletion
							Log.d("ContactDetailsActivity", delete + " rows deleted, id of the user: " + _id);
							Toast.makeText(getApplicationContext(), "Contact deleted", Toast.LENGTH_LONG).show();
						}
						// Finish Contact Details activity
						finish();
					}

				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	/**
	 * Verify that the data inserted is valid
	 * 
	 * @param name of contact
	 * @param telephone of contact
	 * @param email of contact
	 * @return
	 */
	private boolean validContactDetails(String name, Long telephone, String email) {
		// Check not empty field in Name
		if (name.length() > 1 && telephone != null && email.contains("@") && email.length() > 5) {
			return true;
		}
		else {
			Toast.makeText(getApplicationContext(), "Invalid input, please check", Toast.LENGTH_LONG).show();
		}
		return false;
	}

	/**
	 * Set contact details labels editable or not editable depending on the boolean parameter received
	 * 
	 * @param b
	 */
	public void setContactFieldsEditable(boolean b) {
		nameLabel.setEnabled(b);
		surnameLabel.setEnabled(b);
		telephoneLabel.setEnabled(b);
		emailLabel.setEnabled(b);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}

	/**
	 * Set top menu items actions
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_call:
			callContact();
			break;
		case R.id.action_email:
			emailContact();
			break;
		case R.id.action_delete:
			deleteContact(getCurrentFocus());
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
