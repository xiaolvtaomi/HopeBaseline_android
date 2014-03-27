package com.baseline.android.frame.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import com.baseline.android.frame.utils.UriUtil.LocalDirType;
import com.hopebaseline.android.R;

/**
 * This helper class download images from the Internet and binds those with the provided ImageView.
 *
 * <p>It requires the INTERNET permission, which should be added to your application's manifest
 * file.</p>
 *
 * A local cache of downloaded images is maintained internally to improve performance.
 */
public class ImageDownloader {
    private static final String TAG = "ImageDownloader";

    private Context context ;
    public ImageDownloader(Context context){
    	this.context = context;
    }
    
    public enum DefaultImageType{
    	/**
    	 * 如果获取不到对应的图片，默认是显示头像的图片
    	 */
    	FACEICON("faceicon"),
    	/**
    	 * 如果获取不到对应的图片，默认是显示一个大的图片
    	 */
    	BIGIMAGE("bigimage");
    	
    	private String value;
    	private DefaultImageType(String value) {
    		this.value = value;
		}
    	public String getValue() {
			return value;
		}
    }
    
    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     *
     * @param url The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void download(String url, ImageView imageView, DefaultImageType defaultType) {
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(url);

        if (bitmap == null) {
            forceDownload(url, imageView, defaultType);
        } else {
            cancelPotentialDownload(url, imageView);
            imageView.setImageBitmap(bitmap);
        }
    }
    
    /**
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
     */
    private void forceDownload(String url, ImageView imageView , DefaultImageType defaultType) {
        // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (url == null) {
        	Logger.v(TAG, "url of pic incorrect==null");
            imageView.setImageDrawable(null);
            return;
        }
        
        // 如果图片路径不是正确的
        if(!url.toLowerCase().endsWith(".jpeg") && !url.toLowerCase().endsWith(".jpg") && !url.toLowerCase().endsWith(".png")){
        	Logger.v(TAG, "url of pic incorrect="+url);
        	Bitmap bmp = null;
        	if(defaultType.compareTo(DefaultImageType.FACEICON) == 0){
        		bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_contact_icon);
        	}else if(defaultType.compareTo(DefaultImageType.BIGIMAGE) == 0){
        		bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.post_list_thumb_loading);
        	}
        	sHardBitmapCache.put(url, bmp);
        	imageView.setImageBitmap(bmp);
        	return;
        }

        if (cancelPotentialDownload(url, imageView)) {
                	BitmapDownloaderTask task = null;
                	//  需要修改保存非头像的位置
	                	Bitmap bmp = null;
	                	if(defaultType.compareTo(DefaultImageType.FACEICON) == 0){
	                		task= new BitmapDownloaderTask(imageView, 
	                				UriUtil.getLocalStorageDir(null,LocalDirType.FACE),
	                				defaultType);
	                		bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_contact_icon);
	                	}else if(defaultType.compareTo(DefaultImageType.BIGIMAGE) == 0){
	                		bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.post_list_failed);
	                		task= new BitmapDownloaderTask(imageView, 
	                				UriUtil.getLocalStorageDir(null,LocalDirType.IMAGE),
	                				defaultType);
	                	}
                    DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task,context.getResources() ,bmp);
                    imageView.setImageDrawable(downloadedDrawable);
                    imageView.setMinimumHeight(156);
                    task.execute(url);
        }else{
        	Logger.v(TAG, "正在下载同一个资源，停止当前请求");
        }
    }
    

    /**
     * Returns true if the current download has been canceled or if there was no download in
     * progress on this image view.
     * Returns false if the download in progress deals with the same url. The download is not
     * stopped in that case.
     */
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }else{
            }
        }
        return null;
    }

    Bitmap downloadBitmap(String url, String path, DefaultImageType defaulttype) {
        final int IO_BUFFER_SIZE = 4 * 1024;
        
        //读取本地的图片
        String[] temp = url.split("/");
        String picname = temp[temp.length-1];
        Logger.v(TAG, "downloadBitmap() url="+url);
        File file = new File(path+picname);
        if(file.exists()){
        	Bitmap bmp = ImageUtils.getFittestBitmap(path+picname, 160);
        	sHardBitmapCache.put(url, bmp);
        	return bmp;
        }

        HttpClient client = null;
        HttpGet getRequest =null;
        try {
        	// AndroidHttpClient is not allowed to be used from the main thread
        	client = AndroidHttpClient.newInstance("Android");
        	getRequest = new HttpGet(url);
        	
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Logger.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                // 服务器上没有此图片，就默认返回头像图片
                Bitmap bmp = null;
                if(defaulttype.compareTo(DefaultImageType.FACEICON) == 0){
                	bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_contact_icon);
                }else if(defaulttype.compareTo(DefaultImageType.BIGIMAGE) == 0){
                	bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.post_list_failed);
                }else{
                	Logger.e(TAG, "格式不对");
                	bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_contact_icon);
                }
                sHardBitmapCache.put(url, bmp);
                return bmp;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    // 大图在这里会OOM
//                    Bitmap bmp = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
                    
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    bis.mark(0);  
                    BitmapFactory.Options options = new Options();
                    options.inJustDecodeBounds = true; 
                    BitmapFactory.decodeStream(bis, null, options); 
                    Logger.v(TAG, "width="+options.outWidth+"; height="+options.outHeight);  
                    int scale = 1;
                    int minWorH = 160 ;
        			while (true) {  
        				 if (options.outWidth / 2 >= minWorH && options.outHeight / 2 >= minWorH){
        					 options.outWidth /= 2; 
        					 options.outHeight /= 2; 
        					 scale++;  
        				 }else{
        					 break;
        				 }
        			}
        			options.inSampleSize = scale; 
        			options.inJustDecodeBounds = false; 
        			bis.reset();  
        			Bitmap bmp = BitmapFactory.decodeStream(bis, null, options);
                    inputStream.close();
                    bis.close();
        			
                    ImageUtils.storeInSD(bmp, path, picname);
                    return bmp;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            if(getRequest != null){
            	getRequest.abort();
            }
            Logger.e(TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
        	if(getRequest != null){
            	getRequest.abort();
            }
            Logger.e(TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
        	if(getRequest != null){
            	getRequest.abort();
            }
            Logger.e(TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            if (client !=null && (client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private String path;
        private DefaultImageType defaultType;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView ,String savePath, DefaultImageType mdefaultType) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            path = savePath;
            defaultType = mdefaultType;
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            return downloadBitmap(url,path, defaultType);
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            addBitmapToCache(url, bitmap);

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                // Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
                if ((this == bitmapDownloaderTask) ) {
                	if(bitmap != null){
                		imageView.setImageBitmap(bitmap);
                	}
                }
            }
        }
    }


    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     *
     * <p>Contains a reference to the actual download task, so that a download task can be stopped
     * if a new binding is required, and makes sure that only the last started download process can
     * bind its result, independently of the download finish order.</p>
     */
     static class DownloadedDrawable extends BitmapDrawable{
    	private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

    	public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Resources res, Bitmap bmp) {
    		super(res ,bmp);
            bitmapDownloaderTaskReference =
                new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

    
    /*
     * Cache-related fields and methods.
     * 
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */
    
    private static final int HARD_CACHE_CAPACITY = 10;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache =
            new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
                if (size() > HARD_CACHE_CAPACITY) {
                    // Entries push-out of hard reference cache are transferred to soft reference cache
                    sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                    return true;
                } else
                    return false;
            }
        };

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
        new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

    private final Handler purgeHandler = new Handler();

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    /**
     * Adds this bitmap to the cache.
     * @param bitmap The newly downloaded bitmap.
     */
    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private Bitmap getBitmapFromCache(String url) {
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
//                Logger.v(TAG, "getBitmap From hardBitmapCache");
                return bitmap;
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
//            	Logger.v(TAG, "getBitmap From SoftReference");
                return bitmap;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }
        
//        Logger.v(TAG, "there is no bmp in cache");
        return null;
    }
 
    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
    public void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }
}
