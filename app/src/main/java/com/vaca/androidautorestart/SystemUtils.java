package com.vaca.androidautorestart;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class SystemUtils {
	/**
	 * 获取设备的制造商
	 *
	 * @return 设备制造商
	 */
	public static String getDeviceManufacture() {
		return android.os.Build.MANUFACTURER;
	}

	/**
	 * 获取设备名称
	 *
	 * @return 设备名称
	 */
	public static String getDeviceName() {
		return android.os.Build.MODEL;
	}

	/**
	 * 获取系统版本号
	 *
	 * @return 系统版本号
	 */
	public static String getSystemVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取应用的版本号
	 *
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取应用的version code
	 *
	 * @param context
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 判断当前有没有网络连接
	 *
	 * @param context
	 * @return
	 */
	public static boolean getNetworkState(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		return networkinfo != null && networkinfo.isAvailable();
	}

	/**
	 * SD卡是否挂载
	 *
	 * @return
	 */
	public static boolean mountedSdCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
}
