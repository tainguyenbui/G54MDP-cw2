package com.example.g54mdp_addressbook;

import java.io.File;
import java.io.FileOutputStream;

import library.ContactsContract;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

		contactImageView = (ImageView) findViewById(R.id.contactImageView);
		contactImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent imagePicker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				imagePicker.addCategory(Intent.CATEGORY_OPENABLE);
				imagePicker.setType("image/*");
				startActivityForResult(imagePicker, ContactsContract.CHOOSE_PIC_REQUEST_CODE);
			}
		});

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

	private boolean validContactDetails(String name, String telephone, String email) {
		// Check not empty field in Name
		if (name.length() > 1 && telephone.length() > 2 && email.contains("@") && email.length() > 5) {
			return true;
		}
		else {
			Toast.makeText(getApplicationContext(), "Invalid input, please check", Toast.LENGTH_LONG).show();
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ContactsContract.CHOOSE_PIC_REQUEST_CODE && resultCode == RESULT_OK) {
			Uri pictureUri = data.getData();
			Bitmap contactImageThumbnail = getImageThumbNail(pictureUri);
			contactImageView = (ImageView) findViewById(R.id.contactImageView);
			contactImageView.setImageBitmap(contactImageThumbnail);

			telephoneET = (EditText) findViewById(R.id.TelephoneNumberET);
			String telephone = telephoneET.getText().toString();

			File fileDir = new File(ContactsContract.THUMBNAIL_PATH);
			if (!fileDir.exists())
				fileDir.mkdirs();

			String path = fileDir.getAbsolutePath();

			String thumbnailName = "thumbnail_Contact_" + System.currentTimeMillis() + ".png";

			thumbnailImagePath = path + "/" + thumbnailName;
			File file = new File(path, thumbnailName);

			try {
				FileOutputStream fout = new FileOutputStream(file);
				contactImageThumbnail.compress(Bitmap.CompressFormat.PNG, 100, fout);
				fout.flush();
				fout.close();

				Log.d("AddContactActivity", "Image saved");
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			Log.d("AddContactActivity", "onActivityResult, picture uri: " + pictureUri.toString());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Bitmap getImageThumbNail(Uri pictureUri) {
		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = true;

		originalImagePath = getImagePath(pictureUri);

		BitmapFactory.decodeFile(originalImagePath, bounds);

		if (bounds.outWidth == -1 || bounds.outHeight == -1)
			return null;

		int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight : bounds.outWidth;

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = originalSize / ContactsContract.THUMBNAIL_SIZE;
		return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(originalImagePath, opts), 140, 140);
	}

	private String getImagePath(Uri pictureUri) {
		Cursor cursor = getContentResolver().query(pictureUri, null, null, null, null);
		cursor.moveToFirst();
		String document_id = cursor.getString(0);
		document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
		cursor.close();

		cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
				MediaStore.Images.Media._ID + " = ? ", new String[] { document_id }, null);
		cursor.moveToFirst();
		String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
		cursor.close();

		return path;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_contact, menu);
		return true;
	}

}
