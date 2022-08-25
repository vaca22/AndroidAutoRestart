package com.vaca.androidautorestart;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashExceptionHandler implements Thread.UncaughtExceptionHandler {


	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	/**
	 * 默认存放闪退信息的文件夹名称
	 */
	private static final String DEFAULT_CRASH_FOLDER_NAME = "Log";
	/**
	 * appSD卡默认目录
	 */
	private static final String DEFAULT_APP_FOLDER_NAME = "DefaultCrash";


	private Context mApplicationContext;
	/**
	 * app内log在SD卡上的主目录
	 */
	private String mAppMainLogFolder;
	/**
	 * 保存闪退日志的文件目录
	 */
	private String mCrashInfoFolder;

	/**
	 * app在SD卡上的主目录
	 */
	private File mAppMainFolderDir;
	/**
	 * 保存闪退日志的文件目录
	 */
	private File mCrashInfoFolderDir;

	/**
	 * 向远程服务器发送错误信息
	 */
	private CrashExceptionRemoteReport mCrashExceptionRemoteReport;

	/**
	 * @param context
	 * @param appMainFolderName   app程序主目录名，配置后位于SD卡一级目录下
	 * @param crashInfoFolderName 闪退日志保存目录名，配置后位于 appMainFolderName 配置的一级目录下
	 */
	public CrashExceptionHandler(Context context, String appMainFolderName, String crashInfoFolderName) {
		this.mApplicationContext = context.getApplicationContext();

//		File root = context.getExternalFilesDir(null);
		if (!TextUtils.isEmpty(appMainFolderName)) {
			this.mAppMainLogFolder = appMainFolderName;
		} else {
			this.mAppMainLogFolder = DEFAULT_APP_FOLDER_NAME;
		}

		if (!TextUtils.isEmpty(crashInfoFolderName)) {
			this.mCrashInfoFolder = crashInfoFolderName;
		} else {
			this.mCrashInfoFolder = DEFAULT_CRASH_FOLDER_NAME;
		}

	}

	/**
	 * google策略，当app崩溃后，会自动根据栈里面的activity的顺序，重启栈顶activity。这样重启方式会带来持续的问题
	 *
	 * 所以重启前 创建启动页Activity， 让APP重走正常的启动流程
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		handleException(ex);
		Log.e("this","异常："+thread.toString());
		Log.e("this","异常："+ex.getMessage());
		try {
			Thread.sleep(1000);

			// 清除其他Activity,打开启动页，让后续的重启能正常使用
			Intent intent = new Intent(mApplicationContext, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			mApplicationContext.startActivity(intent);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//杀死进程
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);

	}

	/**
	 * 配置远程传回log到服务器的设置
	 *
	 * @param crashExceptionRemoteReport
	 */
	public void configRemoteReport(CrashExceptionRemoteReport crashExceptionRemoteReport) {
		this.mCrashExceptionRemoteReport = crashExceptionRemoteReport;
	}

	/**
	 * 处理异常
	 *
	 * @param ex
	 */
	private void handleException(Throwable ex) {
		if (ex == null) {
			return;
		} else {
			saveCrashInfoToFile(ex);
			sendCrashInfoToServer(ex);

			//使用Toast来显示异常信息
			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					try {
						//Toast.makeText(mApplicationContext, R.string.app_crash_log, Toast.LENGTH_LONG).show();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					Looper.loop();
				}
			}.start();
		}
	}


	/**
	 * 保存闪退信息到本地文件中
	 *
	 * @param ex
	 */
	private void saveCrashInfoToFile(Throwable ex) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {


				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // 存到Documents目录
					String crashLogFileName = DATE_FORMAT.format(new Date());//当先的时间格式化
					Uri fileUri = FileManagerUtils.Companion.getLogFile(mApplicationContext, mAppMainLogFolder, mCrashInfoFolder, crashLogFileName);
					ContentResolver contentResolver = mApplicationContext.getContentResolver();

					String rwMode = "rw";
					ParcelFileDescriptor pfd =
							contentResolver.openFileDescriptor(fileUri, rwMode);
					FileWriter fileWriter = new FileWriter(pfd.getFileDescriptor());
					fileWriter.write("------------Crash Environment Info------------" + "\n");
					fileWriter.write("------------Manufacture: " + SystemUtils.getDeviceManufacture() + "------------" + "\n");
					fileWriter.write("------------DeviceName: " + SystemUtils.getDeviceName() + "------------" + "\n");
					fileWriter.write("------------SystemVersion: " + SystemUtils.getSystemVersion() + "------------" + "\n");
					fileWriter.write("------------AppVersion: " + SystemUtils.getAppVersion(mApplicationContext) + "------------" + "\n");
					fileWriter.write("------------Crash Environment Info------------" + "\n");
					fileWriter.write("\n");

					PrintWriter pw = new PrintWriter(fileWriter, true);
					ex.printStackTrace(pw);//写入奔溃的日志信息
					pw.close();
					fileWriter.close();
				} else { // 存到DATA目录对应的包名下
					File root = mApplicationContext.getExternalFilesDir(null);
					if (!TextUtils.isEmpty(mAppMainLogFolder)) {
						this.mAppMainFolderDir = new File(root, mAppMainLogFolder);
					} else {
						this.mAppMainFolderDir = new File(root, DEFAULT_APP_FOLDER_NAME);
					}
					if (!mAppMainFolderDir.exists()) {//app目录不存在则先创建目录
						mAppMainFolderDir.mkdirs();
					}
					if (!TextUtils.isEmpty(mCrashInfoFolder)) {
						this.mCrashInfoFolderDir = new File(mAppMainFolderDir, mCrashInfoFolder);
					} else {
						this.mCrashInfoFolderDir = new File(mAppMainFolderDir, DEFAULT_CRASH_FOLDER_NAME);
					}
					if (!mCrashInfoFolderDir.exists()) {//闪退日志目录不存在则先创建闪退日志目录
						mCrashInfoFolderDir.mkdirs();
					}
					String timeStampString = DATE_FORMAT.format(new Date());//当先的时间格式化
					String crashLogFileLogName = timeStampString + ".log";
					File crashLogFile = new File(mCrashInfoFolderDir, crashLogFileLogName);
					crashLogFile.createNewFile();

					//记录闪退环境的信息
					RandomAccessFile randomAccessFile = new RandomAccessFile(crashLogFile, "rw");
					randomAccessFile.writeChars("------------Crash Environment Info------------" + "\n");
					randomAccessFile.writeChars("------------Manufacture: " + SystemUtils.getDeviceManufacture() + "------------" + "\n");
					randomAccessFile.writeChars("------------DeviceName: " + SystemUtils.getDeviceName() + "------------" + "\n");
					randomAccessFile.writeChars("------------SystemVersion: " + SystemUtils.getSystemVersion() + "------------" + "\n");
					randomAccessFile.writeChars("------------AppVersion: " + SystemUtils.getAppVersion(mApplicationContext) + "------------" + "\n");
					randomAccessFile.writeChars("------------Crash Environment Info------------" + "\n");
					randomAccessFile.writeChars("\n");
					randomAccessFile.close();

					PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(crashLogFile.getAbsolutePath(), true)), true);
					ex.printStackTrace(pw);//写入奔溃的日志信息
					pw.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}



	/**
	 * 发送发送闪退信息到远程服务器
	 *
	 * @param ex
	 */
	private void sendCrashInfoToServer(Throwable ex) {
		if (mCrashExceptionRemoteReport != null) {
			mCrashExceptionRemoteReport.onCrash(ex);
		}
	}

	/**
	 * 闪退日志远程奔溃接口，主要考虑不同app下，把log回传给服务器的方式不一样，所以此处留一个对外开放的接口
	 */
	public interface CrashExceptionRemoteReport {
		/**
		 * 当闪退发生时，回调此接口函数
		 *
		 * @param ex
		 */
		void onCrash(Throwable ex);
	}
}
