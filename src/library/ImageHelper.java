package library;

import java.io.File;
import java.io.FileOutputStream;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * 
 * @author Tai Nguyen Bui (psytn2)
 * 
 *         This class takes in charge of creating a thumbnail and storing it in the external storage of the device
 * 
 */
public class ImageHelper {
	private String originalImagePath = ContactsContract.DEFAULT_ICON_PATH,
			thumbnailImagePath = ContactsContract.DEFAULT_ICON_PATH;

	private ContentResolver contentResolver;

	public ImageHelper(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	/**
	 * Create thumbnail of an image from the picture gallery of the device
	 * 
	 * @param pictureUri Original picture URI
	 * @return
	 */
	public ContactImageData createThumbnail(Uri pictureUri) {
		this.originalImagePath = getImagePath(pictureUri);
		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(originalImagePath, bounds);

		if (bounds.outWidth == -1 || bounds.outHeight == -1)
			return null;

		int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight : bounds.outWidth;

		// Set the bitmap size
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = originalSize / ContactsContract.THUMBNAIL_SIZE;

		// Create thumbnail bitmap
		Bitmap contactThumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(originalImagePath, opts),
				140, 140);

		// Save bitmap in a file
		saveFile(contactThumbnail);
		return new ContactImageData(contactThumbnail, thumbnailImagePath, originalImagePath);

	}

	/**
	 * Save the bitmap given as parameter on the mobile external storage as a .png file
	 * 
	 * @param contactThumbnail
	 */
	private void saveFile(Bitmap contactThumbnail) {
		File fileDir = new File(ContactsContract.THUMBNAIL_PATH);
		if (!fileDir.exists())
			fileDir.mkdirs();

		String path = fileDir.getAbsolutePath();

		// Set the name of the according to the current time of the system, to avoid name duplications
		String thumbnailName = "thumbnail_Contact_" + System.currentTimeMillis() + ".png";

		this.thumbnailImagePath = path + "/" + thumbnailName;
		File file = new File(path, thumbnailName);

		try {
			FileOutputStream fout = new FileOutputStream(file);
			contactThumbnail.compress(Bitmap.CompressFormat.PNG, 100, fout);
			fout.flush();
			fout.close();

			Log.d("AddContactActivity", "Image saved");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the path of an image URI
	 * 
	 * @param pictureUri
	 * @return the path of the URI given
	 */
	public String getImagePath(Uri pictureUri) {
		Cursor cursor = contentResolver.query(pictureUri, null, null, null, null);
		cursor.moveToFirst();
		String document_id = cursor.getString(0);
		document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
		cursor.close();

		cursor = contentResolver.query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
				MediaStore.Images.Media._ID + " = ? ", new String[] { document_id }, null);
		cursor.moveToFirst();
		String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
		cursor.close();

		return path;
	}

}
