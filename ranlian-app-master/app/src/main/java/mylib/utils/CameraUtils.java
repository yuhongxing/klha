package mylib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;

import mylib.app.AndroidApp;

public class CameraUtils {
	public static final String sImageID = "imageID";

	public static final String sImageName = "imageName";

	public static final String sImagePath = "imagePath";

	public static final String sIsThumb = "sIsThumb";

	public static final String sImageSize = "imageSize";

	public static String getImageById(String id, Activity act) {
		Cursor mCursor = null;
		String ret = null;
		try {
			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			String[] projection = { MediaStore.Images.Media.DATA, };
			String selection = MediaStore.Images.Media._ID + "=" + id;

			mCursor = act.getContentResolver().query(uri, projection, selection, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
				if (mCursor.isLast()) {
					return null;
				}
				ret = mCursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != mCursor) {
				try {
					mCursor.close();
				} catch (Exception e) {
				}
			}
		}
		return ret;
	}

	public static String getThumbById(String id, Activity act) {
		Cursor mCursor = null;
		String ret = null;
		try {
			Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
			String[] projection = { MediaStore.Images.Media.DATA, };
			String selection = MediaStore.Images.Thumbnails.IMAGE_ID + "=" + id;

			mCursor = act.getContentResolver().query(uri, projection, selection, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
				if (mCursor.isLast()) {
					return null;
				}
				ret = mCursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != mCursor) {
				try {
					mCursor.close();
				} catch (Exception e) {
				}
			}
		}
		return ret;
	}

	public static List<Map<String, String>> getAllImages(Activity act) {
		List<Map<String, String>> ret = null;
		Cursor mCursor = null;
		try {

			List<Map<String, String>> imgList = getImageList(act);
			if (null == imgList || 0 == imgList.size()) {
				return null;
			}
			ret = new LinkedList<Map<String, String>>();
			for (int i = 0; i < imgList.size(); i++) {
				Map<String, String> item = imgList.get(i);
				String path = item.get(sImagePath);
				if (!ImageUtils.isImage(path)) {
					continue;
				}
				String id = item.get(sImageID);
				String path2 = getThumbById(id, act);
				HashMap<String, String> data = new HashMap<String, String>();
				data.put(sImageID, id);
				if (!ImageUtils.isImage(path2)) {
					data.put(sImagePath, path);
				} else {
					data.put(sImagePath, path2);
					data.put(sIsThumb, "");
				}
				ret.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != mCursor) {
				try {
					mCursor.close();
				} catch (Exception e) {
				}
			}
		}
		return ret;
	}

	public static List<Map<String, String>> getThumbList(Activity act) {
		List<Map<String, String>> imageList = null;
		Cursor mCursor = null;
		try {
			HashMap<String, String> imageMap;

			Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
			String[] projection = { MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA, };
			mCursor = act.getContentResolver().query(uri, projection, null, null, null);

			if (mCursor != null) {
				mCursor.moveToFirst();
				imageList = new ArrayList<Map<String, String>>();
				while (true) {
					if (mCursor.isLast()) {
						break;
					}
					imageMap = new HashMap<String, String>();
					String id = mCursor.getString(0);
					imageMap.put(sImageID, id);
					String path = mCursor.getString(1);
					imageMap.put(sImagePath, path);
					imageList.add(imageMap);

					mCursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != mCursor) {
				try {
					mCursor.close();
				} catch (Exception e) {
				}
			}
		}
		return imageList;
	}

	public static List<Map<String, String>> getImageList(Activity act) {
		List<Map<String, String>> imageList = null;
		Cursor mCursor = null;
		try {

			HashMap<String, String> imageMap;
			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, };
			String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
			String[] selectionArg = { "image/jpeg", };
			mCursor = act.getContentResolver().query(uri, projection, selection, selectionArg,
					MediaStore.Images.Media.DISPLAY_NAME);
			if (mCursor != null) {
				mCursor.moveToFirst();
				imageList = new ArrayList<Map<String, String>>();
				while (true) {
					if (mCursor.isLast()) {
						break;
					}
					imageMap = new HashMap<String, String>();
					imageMap.put(sImageID, mCursor.getString(0));
					imageMap.put(sImagePath, mCursor.getString(1));
					imageList.add(imageMap);
					mCursor.moveToNext();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != mCursor) {
				try {
					mCursor.close();
				} catch (Exception e) {
				}
			}
		}
		return imageList;
	}

	public static final int FLAG_CAMERA = 1;

	public static final int FLAG_GALLERY = (FLAG_CAMERA << 1);

	public static final String tmpPicFile;
	static {
		String pkg = "/sdcard/" + AndroidApp.sInst.getPackageName();
		tmpPicFile = pkg + "_tmp_pic.jpg";
	}

	public static void doCamera(Activity act, String picFile) {
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = new File(picFile == null ? tmpPicFile : picFile);
		if (f.exists()) {
			f.delete();
		}
		i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		try {
			Log.i("xing","doCamera");
//			act.startActivityForResult(i, FLAG_CAMERA);
			// startActivity(Intent.createChooser(intent, "标题"));`
			act.startActivityForResult(Intent.createChooser(i,"标题"),FLAG_CAMERA);
		} catch (Exception e) {
			Log.i("xing","Exception==>"+e.toString());
//			Utils.toast(R.string.tip_no_app);
		}
	}

	public static void doGallery(Activity act) {
		Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		i.setType("image/*");
		try {
			act.startActivityForResult(i, FLAG_GALLERY);
		} catch (Exception e) {
			//Utils.toast(R.string.tip_no_app);
		}
	}

	@SuppressWarnings("deprecation")
	public static String getGalleryFile(Uri uri, Activity act) {
		String file = null;
		String[] projection = { MediaStore.Images.ImageColumns.DATA };
		Cursor c = act.managedQuery(uri, projection, null, null, null);
		try {
			c.moveToFirst();
			file = c.getString(0);
			if (Build.VERSION.SDK_INT < 14) {
				c.close();
			} else {
			}
		} catch (Exception e) {
			file = null;
		}
		return file;
	}

	public static InputStream getGalleryData(Uri uri, Activity act) {
		String f = getGalleryFile(uri, act);
		if (f == null)
			return null;
		try {
			return new FileInputStream(new File(f));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static String getPicutre(Intent data, int requestCode, int resultCode, Activity act, String picFile) {
		if (resultCode != Activity.RESULT_OK) {
			return null;
		}
		if (requestCode == FLAG_GALLERY) {
			if (data == null)
				return null;
			Uri uri = data.getData();
			String path = getGalleryFile(uri, act);
			return path;
		} else if (requestCode == FLAG_CAMERA) {
			String path = picFile == null ? tmpPicFile : picFile;
			File f = new File(path);
			Uri uri = null;
			if (!f.exists() || !f.isFile()) {
				if (data == null)
					return null;
				uri = data.getData();
				if (null == uri)
					return null;
				f = new File(path);
				Object o = data.getExtras().get("data");
				if (o == null || !(o instanceof Bitmap)) {
					return null;
				} else {
					Bitmap bmp = (Bitmap) o;
					try {
						f = new File(path);
						FileOutputStream out = new FileOutputStream(f);
						bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.close();
					} catch (Exception e) {
						return null;
					}
				}
			}
			return path;
		}
		return null;
	}

	public static void initPhotoError() {
		// android 7.0系统解决拍照的问题
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		builder.detectFileUriExposure();
	}

}
