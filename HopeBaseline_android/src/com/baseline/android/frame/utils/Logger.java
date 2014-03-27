package com.baseline.android.frame.utils;

import android.util.Log;

import com.baseline.android.Common;

public class Logger {

	/**
	 * log dir
	 */
	public static final String LOG_DIR = "/mnt/sdcard/hopebase/log/";
	/**
	 * log tag
	 */
	public static final String TAG = "Logger";
	/**
	 * log path
	 */
	public static final String LOG_FILE_PATH = LOG_DIR + "mylog.log";
	/**
	 * file size limitation per log file
	 */
	public static final long MAXSIZE_PERFILE = 1048576;
	/**
	 * Priority constant for the println method; use Log.v.
	 */
	public static final int VERBOSE = 2;

	/**
	 * Priority constant for the println method; use Log.d.
	 */
	public static final int DEBUG = 3;

	/**
	 * Priority constant for the println method; use Log.i.
	 */
	public static final int INFO = 4;

	/**
	 * Priority constant for the println method; use Log.w.
	 */
	public static final int WARN = 5;

	/**
	 * Priority constant for the println method; use Log.e.
	 */
	public static final int ERROR = 6;
	/**
	 * current log level
	 */
	private static int currentLevel = VERBOSE;

	/**
	 * Low-level logging call.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param level
	 *            level
	 * @return The number of bytes written.
	 */
	public static int println(int level, String tag, String msg) {
		int result = 0;
//		if (isLoggable(level)) {
//			result = android.util.Log.println(level, tag, msg);
//		} else {
//			return result;
//		}
//		if (!MemoryStatus.externalMemoryAvailable()) {
//			android.util.Log.w(TAG, "SD is not available.");
//			return result;
//		}
//		if (!LogCache.getInstance().isStarted()
//				|| LogCache.getInstance().isLogThreadNull()) {
//			startService();
//		}
//		if (isLoggable(level)) {
//			LogCache.getInstance().write(levelString(level), tag, msg);
//		} else if (!MemoryStatus.externalMemoryAvailable()) {
//			android.util.Log.w(TAG, "SD Card is unavailable.");
//		}
		return result;
	}
	
	private static String levelString(int level)
    {
        switch (level)
        {
            case Logger.VERBOSE:
                return "V";
            case Logger.DEBUG:
                return "D";
            case Logger.INFO:
                return "I";
            case Logger.WARN:
                return "W";
            case Logger.ERROR:
                return "E";
            default:
                return "D";
        }
    }

	public static boolean isLoggable(int level) {
		return level >= currentLevel;
	}

	public static void v(String TAG, String msg) {
		if (Common.OPENLOG) {
			Log.v(TAG, msg);
		}else{
			println(VERBOSE, TAG, msg);
		}
	}

	public static void i(String TAG, String msg) {
		if (Common.OPENLOG) {
			Log.i(TAG, msg);
		}else{
			println(INFO, TAG, msg);
		}
	}

	public static void w(String TAG, String msg) {
		if (Common.OPENLOG) {
			Log.w(TAG, msg);
		}else{
			println(WARN, TAG, msg);
		}
	}

	public static void e(String TAG, String msg) {
		if (Common.OPENLOG) {
			Log.e(TAG, msg);
		}else{
			println(ERROR, TAG, msg);
		}
	}

	public static void e(String TAG, String msg, Exception e) {
		if (Common.OPENLOG) {
			Log.e(TAG, msg);
			e.printStackTrace();
		}else{
			println(ERROR, TAG, msg + '\n' + e.getStackTrace().toString());
		}
	}

	public static void d(String TAG, String msg) {
		if (Common.OPENLOG) {
			Log.d(TAG, msg);
		}
	}

}
