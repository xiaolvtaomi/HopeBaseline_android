package com.baseline.android.frame.utils;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

import com.baseline.android.Common;

public final class UriUtil {
	private static final String TAG = "UriUtil";
    
	/**
     * 本地文件夹类型
     */
	public enum LocalDirType {
		/**
		 * 图片
		 */
		IMAGE("image"),
		/**
		 * 语音
		 */
		VOICE("voice"),
		/**
		 * 视频
		 */
		VIDEO("video"),
		/**
		 * 缩略图
		 */
		THUMB_NAIL("thumbnail"),
		/**
		 * 下载
		 */
		DOWNLOAD("download"),
		/**
		 * 头像
		 */
		FACE("face"),
		/**
         * VoIP相关的录音文件
         */
        VOIP_RECORD("voip/record"),
        /**
         * 其他临时需要处理的文件
         */
        TEMP("temp"),
        /**
         * 系统相册的目录
         */
        DCIM("DCIM/hitalk"),
        /**
         * 升级文件
         */
        UPGRADE("upgrade"),
        /**
         * 日志文件
         */
        LOG("log"),
        /**
         * 新浪微博
         */
        MBLOG_SINA("mblog/sina"),
        /**
         * im
         */
        IM("im"),
        /**
         * sns
         */
        SNS("sns"),
        /**
         * email
         */
        EMAIL("email");
		private String value;

		private LocalDirType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
    
	/**
     * 来源类型
     */
    public enum FromType
    {
        /**
         * 发送
         */
        SEND("send"),
        /**
         * 接收
         */
        RECEIVE("receive"),
        /**
         * 第三方
         */
        Third("3rd");
        
        private String value;
        
        private FromType(String value)
        {
            this.value = value;
        }
        
        public String getValue()
        {
            return value;
        }
    }
    
	public static String getLocalStorageDir(String userAccount,
            LocalDirType dirType){
		StringBuilder buffer = new StringBuilder();
        buffer.append(Environment.getExternalStorageDirectory().getPath());
        buffer.append("/");
        buffer.append(Common.APP_NAME);
        buffer.append("/");
        
        if (null != userAccount)
        {
            buffer.append(String.valueOf(userAccount));
            buffer.append("/");
        }
        if (null != dirType)
        {
            buffer.append(dirType.getValue());
            buffer.append("/");
        }
      //如果目录不存在，则创建新目录
        File storeDir = FileUtils.getFileByPath(buffer.toString());
        
        if (null == storeDir)
        {
            return null;
        }
        
        if (!storeDir.exists())
        {
            if (!storeDir.mkdirs())
            {
                return null;
            }
        }
        
        // 语音文件给个拒绝扫描
        if(dirType.compareTo(LocalDirType.VOICE) == 0){
	        File nomediaFile = new File(storeDir, ".nomedia");
	    	if(!nomediaFile.exists()){
	    		Logger.e(TAG,".nomedia not exist and create it");
	    		try {
					nomediaFile.createNewFile();
				} catch (IOException e) {
					Logger.e(TAG, ".nomedia not exist and create failed", e);
				}
	    	}
	    	if(nomediaFile != null){
	    		nomediaFile = null;
	    	}
        }
        // 语音文件给个拒绝扫描
        if(dirType.compareTo(LocalDirType.IMAGE) == 0){
	        File nomediaFile = new File(storeDir, ".nomedia");
	    	if(!nomediaFile.exists()){
	    		Logger.e(TAG,".nomedia not exist and create it");
	    		try {
					nomediaFile.createNewFile();
				} catch (IOException e) {
					Logger.e(TAG, ".nomedia not exist and create failed", e);
				}
	    	}
	    	if(nomediaFile != null){
	    		nomediaFile = null;
	    	}
        }
        
        return buffer.toString();
	}
	
	
}
