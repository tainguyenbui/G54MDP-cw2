package library;

import android.graphics.Bitmap;

/**
 * 
 * @author Tai Nguyen Bui (psytn2)
 * 
 *         Object that is returned by the ImageHelper class when an image thumbnail is created
 * 
 */
public class ContactImageData {
	private Bitmap contactThumbnail;

	private String originalImagePath, thumbnailImagePath;

	/**
	 * Default constructor
	 * 
	 * @param contactThumbnail Bitmap of the thumbnail
	 * @param thumbnailImagePath Path of the thumbnail
	 * @param originalImagePath Path of the original image
	 */
	public ContactImageData(Bitmap contactThumbnail, String thumbnailImagePath, String originalImagePath) {
		this.contactThumbnail = contactThumbnail;
		this.originalImagePath = originalImagePath;
		this.thumbnailImagePath = thumbnailImagePath;
	}

	/**
	 * Get the original image path contained in the object
	 * 
	 * @return a String with the path of the original image
	 */
	public String getOriginalImagePath() {
		return this.originalImagePath;
	}

	/**
	 * Get the thumbnail image from the object
	 * 
	 * @return the thumbnail as a Bitmap
	 */
	public Bitmap getThumbnailImage() {
		return this.contactThumbnail;
	}

	/**
	 * Get the thumbnail image path contained in the object
	 * 
	 * @return a String with the path of the thumbnail
	 */
	public String getThumbnailImagePath() {
		return this.thumbnailImagePath;
	}

}
