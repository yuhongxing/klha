package mylib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.os.Environment;
import android.text.TextUtils;
import mylib.app.AndroidApp;
import mylib.app.MyLog;

public class FileUtils {

	//// dirs
	private static String mHomeDir;

	public static enum DirType {
		home, pic, file, tmp, log, download, snd,
	}

	public static String getPicDirName() {
		return DirType.pic.name();
	}

	public static String getDir(DirType type) {
		if (null == mHomeDir)
			return "/"; // FIXME: return null ?
		if (type == DirType.home) {
			return mHomeDir;
		} else if (type == DirType.pic) {
			return mHomeDir + getPicDirName() + File.separatorChar;
		}
		return mHomeDir + type.name() + File.separatorChar;
	}

	public static void checkDir() {
		// PackageInfo pi = null;
		// PackageManager pm = AndroidApp.sInst.getPackageManager();
		File f = null;
		try {
			// pi = pm.getPackageInfo(AndroidApp.sInst.getPackageName(), 0);

			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String home = null;
				// home = pm.getApplicationLabel(pi.applicationInfo).toString();
				// if (home == null) {
				home = AndroidApp.sInst.getPackageName();
				// }

				mHomeDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + home
						+ File.separatorChar;
				f = new File(mHomeDir);
				if (!f.isDirectory()) {
					f.delete();
					f.mkdirs();
				}
			} else {
				mHomeDir = null; // getFilesDir().getAbsolutePath();
				return;
			}
		} catch (Exception e) {
			mHomeDir = null;
			return;
		}

		String[] dirs = new String[] { getPicDirName(), DirType.file.name(), DirType.log.name(), DirType.tmp.name(),
				DirType.snd.name(), DirType.download.name() };
		boolean[] nomeida = new boolean[] { false, true, true, true, true, true };
		for (int i = 0; i < dirs.length; i++) {
			String dir = mHomeDir + dirs[i] + File.separatorChar;
			f = new File(dir);
			if (!f.isDirectory()) {
				f.delete();
				f.mkdirs();
			}
			f = new File(dir + ".nomedia");
			if (nomeida[i]) {
				if (!f.exists()) {
					f.mkdirs();
				}
			} else {
				if (f.exists()) {
					f.delete();
				}
			}
		}
	}

	/// ! dirs
	public static String getURLFilePath(String dir, String url) {
		String homedir = getDir(DirType.home);
		if (homedir == null) {
			return null;
		}
		return homedir + File.separatorChar + dir + File.separatorChar + getURLFile(url);
	}

	public static String getURLFile(String url) {
		// return getURLFile(url, 3);
		return Utils.md5(url);
	}

	public static String getURLFile(String url, int level) {
		String homedir = getDir(DirType.home);
		if (homedir == null || level <= 0) {
			return null;
		}
		int idx = url.length();
		do {
			idx = url.lastIndexOf('/', idx - 1);
			if (idx < 0)
				break;
		} while (--level > 0);

		return url.substring(idx + 1).replace('/', '-');
	}

	private final static String sObjectsDir = "objects";

	public static void removeObjcect(String file) {
		if (file == null)
			return;
		String path = AndroidApp.sInst.getDir(sObjectsDir, 4/* Context.MODE_MULTI_PROCESS */).getAbsolutePath() + "/"
				+ file;
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}
	}

	public static boolean saveObject(String fileName, Object o) {
		if (o == null || TextUtils.isEmpty(fileName)) {
			return false;
		}
		Object obj = getObject(fileName, o.getClass());
		if (obj != null && obj.equals(o)) {
			return true;
		}
		AndroidApp app = AndroidApp.sInst;
		File file = null;
		if (fileName.charAt(0) != File.separatorChar) { // local file
			String dir = app.getDir(sObjectsDir, 4/* Context.MODE_MULTI_PROCESS */).getAbsolutePath();
			file = new File(dir + File.separatorChar + fileName);
			if (file.exists()) {
				file.delete();
				file = new File(dir + File.separatorChar + fileName);
			}
		} else {
			file = new File(fileName);
		}
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(o);
			return true;
		} catch (Throwable e) {
			if (MyLog.DEBUG) {
				MyLog.LOGE(e);
			}
		} finally {
			Utils.close(out);
		}
		return false;
	}

	public static Object getObject(String fileName, Class<?> claz) {
		if (TextUtils.isEmpty(fileName)) {
			return null;
		}
		AndroidApp app = AndroidApp.sInst;
		File file = null;
		if (fileName.charAt(0) != File.separatorChar) {
			String dir = app.getDir(sObjectsDir, 4/* Context.MODE_MULTI_PROCESS */).getAbsolutePath();
			file = new File(dir + File.separatorChar + fileName);
		} else {
			file = new File(fileName);
		}
		if (file == null || !file.isFile() || !file.exists()) {
			return null;
		}

		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			Object obj = in.readObject();
			if (obj != null && !claz.isInstance(obj)) {
				return null;
			}
			return obj;
		} catch (Throwable e) {
			// if (e instanceof InvalidClassException) {
			file.delete(); // delete anyway !
			// }
			//MyLog.LOGE(e);
		} finally {
			Utils.close(in);
		}
		return null;
	}

}
