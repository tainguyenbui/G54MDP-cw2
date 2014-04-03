package com.example.g54mdp_addressbook;

import library.ContactImageData;
import library.ContactsContract;
import library.ImageHelper;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 
 * @author Tai Nguyen Bui (psytn2)
 * 
 *         Activity opened when a contact wants to be added
 * 
 */
public class AddContactActivity extends Activity {

	private ImageView contactImageView;

	private EditText nameET = null, surnameET = null, telephoneET = null, emailET = null;

	private String originalImagePath = ContactsContract.DEFAULT_ICON_PATH,
			thumbnailImagePath = ContactsContract.DEFAULT_ICON_PATH;

	private Button btnAddContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);

		// Set listener on the contact image, in order to be able to select an image
		contactImageView = (ImageView) findViewById(R.id.contactImageView);
		contactImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent imagePickerIntent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(imagePickerIntent, ContactsContract.CHOOSE_PIC_REQUEST_CODE);
			}
		});

		// Add contact button listener
		btnAddContact = (Button) findViewById(R.id.btnAddContact);
		btnAddContact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				nameET = (EditText) findViewById(R.id.contactNameET);
				String name = nameET.getText().toString();

				surnameET = (EditText) findViewById(R.id.contactSurnameET);
				String surname = surnameET.getText().toString();

				telephoneET = (EditText) findViewById(R.id.TelephoneNumberET);
				String telephone = telephoneET.getText().toString();

				emailET = (EditText) findViewById(R.id.emailET);
				String email = emailET.getText().toString();

				if (validContactDetails(name, telephone, email)) {
					ContentValues values = new ContentValues();
					values.put(ContactsContract.KEY_NAME, name); // Name
					values.put(ContactsContract.KEY_SURNAME, surname); // Surname
					values.put(ContactsContract.KEY_TELEPHONE, telephone); // Telephone
					values.put(ContactsContract.KEY_EMAIL, email); // Email
					values.put(ContactsContract.KEY_THUMBNAIL_IMAGE_PATH, thumbnailImagePath);
					values.put(ContactsContract.KEY_ORIGINAL_IMAGE_PATH, originalImagePath);

					Uri uri = getContentResolver().insert(ContactsContract.CONTACTS_URI, values);

					Toast contactAddedToast = Toast.makeText(getApplicationContext(), "Contact Added",
							Toast.LENGTH_LONG);
					contactAddedToast.show();

					Intent _result = new Intent();
					_result.setData(uri);
					setResult(Activity.RESULT_OK, _result);
					finish();
				}
			}

		});
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * Verify that the data inserted is valid
	 * 
	 * @param name of contact
	 * @param telephone of contact
	 * @param email of contact
	 * @return
	 */
	private boolean validContactDetails(String name, String telephone, String email) {
		// Check not empty field in Name
		if (name.length() > 1 && telephone.length() > 2 && email.contains("@") && !email.endsWith("@")
				&& email.length() > 5) {
			return true;
		}
		else {
			Toast.makeText(getApplicationContext(), "Invalid input, please check", Toast.LENGTH_LONG).show();
		}
		return false;
	}

	/**
	 * Set the image selected through the image picker on the ImageView of the contact
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ContactsContract.CHOOSE_PIC_REQUEST_CODE && resultCode == RESULT_OK) {
			Uri pictureUri = data.getData();

			ImageHelper imageHelper = new ImageHelper(getContentResolver());
			ContactImageData contactImageData = imageHelper.createThumbnail(pictureUri);

			this.originalImagePath = contactImageData.getOriginalImagePath();
			this.thumbnailImagePath = contactImageData.getThumbnailImagePath();

			contactImageView = (ImageView) findViewById(R.id.contactImageView);
			contactImageView.setImageBitmap(contactImageData.getThumbnailImage());

			Log.d("AddContactActivity", "onActivityResult, picture uri: " + pictureUri.toString());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_contact, menu);
		return true;
	}

}
