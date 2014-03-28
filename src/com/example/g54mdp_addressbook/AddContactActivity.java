package com.example.g54mdp_addressbook;

import java.io.File;
import java.io.FileOutputStream;

import library.Constants;
import library.DatabaseHandler;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
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

	private String originalImagePath = null, thumbnailImagePath = null;

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
				startActivityForResult(imagePicker, Constants.CHOOSE_PIC_REQUEST_CODE);
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

				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
				db.addContact(name, surname, telephone, email, thumbnailImagePath, originalImagePath);
				Toast contactAddedToast = Toast.makeText(getApplicationContext(), "Contact Added", Toast.LENGTH_LONG);
				contactAddedToast.show();
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.CHOOSE_PIC_REQUEST_CODE && resultCode == RESULT_OK) {
			Uri pictureUri = data.getData();
			Bitmap contactImageThumbnail = getImageThumbNail(pictureUri);
			contactImageView = (ImageView) findViewById(R.id.contactImageView);
			contactImageView.setImageBitmap(contactImageThumbnail);

			String root = Environment.getExternalStorageDirectory().toString();
			File fileDir = new File(root + "/contact_thumbnails");
			fileDir.mkdirs();

			telephoneET = (EditText) findViewById(R.id.TelephoneNumberET);
			String telephone = telephoneET.getText().toString();

			String thumbnailName = "thumbnail_" + telephone + ".png";

			thumbnailImagePath = fileDir.getPath() + "/" + thumbnailName;
			File file = new File(fileDir, thumbnailImagePath);
			if (file.exists())
				file.delete();
			try {
				FileOutputStream fout = new FileOutputStream(file);
				contactImageThumbnail.compress(Bitmap.CompressFormat.PNG, 100, fout);
				fout.flush();
				fout.close();

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
		opts.inSampleSize = originalSize / Constants.THUMBNAIL_SIZE;
		return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(originalImagePath, opts), 120, 120);
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
