package com.baseline.android.frame.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.hopebaseline.android.R;

public class ImageUtils {
	public static final String TAG = "PhotoUtils";
	// 图片在SD卡中的缓存路径
	private static final String IMAGE_PATH = Environment
			.getExternalStorageDirectory().toString()
			+ File.separator
			+ "immomo" + File.separator + "Images" + File.separator;
	// 相册的RequestCode
	public static final int INTENT_REQUEST_CODE_ALBUM = 0;
	// 照相的RequestCode
	public static final int INTENT_REQUEST_CODE_CAMERA = 1;
	// 裁剪照片的RequestCode
	public static final int INTENT_REQUEST_CODE_CROP = 2;
	// 滤镜图片的RequestCode
	public static final int INTENT_REQUEST_CODE_FLITER = 3;

	/**
	 * 通过手机相册获取图片
	 * 
	 * @param activity
	 */
	public static void selectPhoto(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		activity.startActivityForResult(intent, INTENT_REQUEST_CODE_ALBUM);
	}

	/**
	 * 通过手机照相获取图片
	 * 
	 * @param activity
	 * @return 照相后图片的路径
	 */
	public static String takePicture(Activity activity) {
		FileUtils.createDirFile(IMAGE_PATH);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String path = IMAGE_PATH + UUID.randomUUID().toString() + "jpg";
		File file = FileUtils.createNewFile(path);
		if (file != null) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		}
		activity.startActivityForResult(intent, INTENT_REQUEST_CODE_CAMERA);
		return path;
	}


	/**
	 * 删除图片缓存目录
	 */
	public static void deleteImageFile() {
		File dir = new File(IMAGE_PATH);
		if (dir.exists()) {
			FileUtils.delFolder(IMAGE_PATH);
		}
	}

	/**
	 * 从文件中获取图片
	 * 
	 * @param path
	 *            图片的路径
	 * @return
	 */
	public static Bitmap getBitmapFromFile(String path) {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * 从Uri中获取图片
	 * 
	 * @param cr
	 *            ContentResolver对象
	 * @param uri
	 *            图片的Uri
	 * @return
	 */
	public static Bitmap getBitmapFromUri(ContentResolver cr, Uri uri) {
		try {
			return BitmapFactory.decodeStream(cr.openInputStream(uri));
		} catch (FileNotFoundException e) {

		}
		return null;
	}

	/**
	 * 根据宽度和长度进行缩放图片
	 * 
	 * @param path
	 *            图片的路径
	 * @param w
	 *            宽度
	 * @param h
	 *            长度
	 * @return
	 */
	public static Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * 获取图片的长度和宽度
	 * 
	 * @param bitmap
	 *            图片bitmap对象
	 * @return
	 */
	public static Bundle getBitmapWidthAndHeight(Bitmap bitmap) {
		Bundle bundle = null;
		if (bitmap != null) {
			bundle = new Bundle();
			bundle.putInt("width", bitmap.getWidth());
			bundle.putInt("height", bitmap.getHeight());
			return bundle;
		}
		return null;
	}

	/**
	 * 判断图片高度和宽度是否过大
	 * 
	 * @param bitmap
	 *            图片bitmap对象
	 * @return
	 */
	public static boolean bitmapIsLarge(Bitmap bitmap) {
		final int MAX_WIDTH = 60;
		final int MAX_HEIGHT = 60;
		Bundle bundle = getBitmapWidthAndHeight(bitmap);
		if (bundle != null) {
			int width = bundle.getInt("width");
			int height = bundle.getInt("height");
			if (width > MAX_WIDTH && height > MAX_HEIGHT) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据比例缩放图片
	 * 
	 * @param screenWidth
	 *            手机屏幕的宽度
	 * @param filePath
	 *            图片的路径
	 * @param ratio
	 *            缩放比例
	 * @return
	 */
	public static Bitmap CompressionPhoto(float screenWidth, String filePath,
			int ratio) {
		Bitmap bitmap = ImageUtils.getBitmapFromFile(filePath);
		Bitmap compressionBitmap = null;
		float scaleWidth = screenWidth / (bitmap.getWidth() * ratio);
		float scaleHeight = screenWidth / (bitmap.getHeight() * ratio);
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		try {
			compressionBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} catch (Exception e) {
			return bitmap;
		}
		return compressionBitmap;
	}

	/**
	 * 保存图片到SD卡
	 * 
	 * @param bitmap
	 *            图片的bitmap对象
	 * @return
	 */
	public static String savePhotoToSDCard(Bitmap bitmap) {
		if (!FileUtils.isSdcardExist()) {
			return null;
		}
		FileOutputStream fileOutputStream = null;
		FileUtils.createDirFile(IMAGE_PATH);

		String fileName = UUID.randomUUID().toString() + ".jpg";
		String newFilePath = IMAGE_PATH + fileName;
		File file = FileUtils.createNewFile(newFilePath);
		if (file == null) {
			return null;
		}
		try {
			fileOutputStream = new FileOutputStream(newFilePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
		} catch (FileNotFoundException e1) {
			return null;
		} finally {
			try {
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (IOException e) {
				return null;
			}
		}
		return newFilePath;
	}


	/**
	 * 滤镜效果--LOMO
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap lomoFilter(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int dst[] = new int[width * height];
		bitmap.getPixels(dst, 0, width, 0, 0, width, height);

		int ratio = width > height ? height * 32768 / width : width * 32768
				/ height;
		int cx = width >> 1;
		int cy = height >> 1;
		int max = cx * cx + cy * cy;
		int min = (int) (max * (1 - 0.8f));
		int diff = max - min;

		int ri, gi, bi;
		int dx, dy, distSq, v;

		int R, G, B;

		int value;
		int pos, pixColor;
		int newR, newG, newB;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y * width + x;
				pixColor = dst[pos];
				R = Color.red(pixColor);
				G = Color.green(pixColor);
				B = Color.blue(pixColor);

				value = R < 128 ? R : 256 - R;
				newR = (value * value * value) / 64 / 256;
				newR = (R < 128 ? newR : 255 - newR);

				value = G < 128 ? G : 256 - G;
				newG = (value * value) / 128;
				newG = (G < 128 ? newG : 255 - newG);

				newB = B / 2 + 0x25;

				// ==========边缘黑暗==============//
				dx = cx - x;
				dy = cy - y;
				if (width > height)
					dx = (dx * ratio) >> 15;
				else
					dy = (dy * ratio) >> 15;

				distSq = dx * dx + dy * dy;
				if (distSq > min) {
					v = ((max - distSq) << 8) / diff;
					v *= v;

					ri = (int) (newR * v) >> 16;
					gi = (int) (newG * v) >> 16;
					bi = (int) (newB * v) >> 16;

					newR = ri > 255 ? 255 : (ri < 0 ? 0 : ri);
					newG = gi > 255 ? 255 : (gi < 0 ? 0 : gi);
					newB = bi > 255 ? 255 : (bi < 0 ? 0 : bi);
				}
				// ==========边缘黑暗end==============//

				dst[pos] = Color.rgb(newR, newG, newB);
			}
		}

		Bitmap acrossFlushBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		acrossFlushBitmap.setPixels(dst, 0, width, 0, 0, width, height);
		return acrossFlushBitmap;
	}

	/**
	 * 根据文字获取图片
	 * 
	 * @param text
	 * @return
	 */
	public static Bitmap getIndustry(Context context, String text) {
		String color = "#ffefa600";
		if ("艺".equals(text)) {
			color = "#ffefa600";
		} else if ("学".equals(text)) {
			color = "#ffbe68c1";
		} else if ("商".equals(text)) {
			color = "#ffefa600";
		} else if ("医".equals(text)) {
			color = "#ff30c082";
		} else if ("IT".equals(text)) {
			color = "#ff27a5e3";
		}
		Bitmap src = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_userinfo_group);
		int x = src.getWidth();
		int y = src.getHeight();
		Bitmap bmp = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bmp);
		canvasTemp.drawColor(Color.parseColor(color));
		Paint p = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.WHITE);
		p.setFilterBitmap(true);
		int size = (int) (13 * context.getResources().getDisplayMetrics().density);
		p.setTextSize(size);
		float tX = (x - getFontlength(p, text)) / 2;
		float tY = (y - getFontHeight(p)) / 2 + getFontLeading(p);
		canvasTemp.drawText(text, tX, tY, p);

		return toRoundCorner(bmp, 2);
	}

	/**
	 * @return 返回指定笔和指定字符串的长度
	 */
	public static float getFontlength(Paint paint, String str) {
		return paint.measureText(str);
	}

	/**
	 * @return 返回指定笔的文字高度
	 */
	public static float getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.descent - fm.ascent;
	}

	/**
	 * @return 返回指定笔离文字顶部的基准距离
	 */
	public static float getFontLeading(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.leading - fm.ascent;
	}

	/**
	 * 获取圆角图片
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获取颜色的圆角bitmap
	 * 
	 * @param context
	 * @param color
	 * @return
	 */
	public static Bitmap getRoundBitmap(Context context, int color) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int width = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 12.0f, metrics));
		int height = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4.0f, metrics));
		int round = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 2.0f, metrics));
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawRoundRect(new RectF(0.0F, 0.0F, width, height), round,
				round, paint);
		return bitmap;
	}
	
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    public static double gps2d(double lat_a, double lng_a, double lat_b, double lng_b) {
		double EARTH_RADIUS = 6378137.0;
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
    
    public static boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = am.getRunningTasks(5);
		if (tasksInfo != null && tasksInfo.size() > 0) {
			for (int i = 0; i < tasksInfo.size(); i++) {
				ComponentName componentName = tasksInfo.get(i).topActivity;
			}
		}
		if (tasksInfo != null && tasksInfo.size() > 0) {
			ComponentName componentName = tasksInfo.get(0).topActivity;
			if (componentName != null) {
				String packname = componentName.getPackageName();
				if (!StringUtils.isNullOrEmpty(packname)
						&& packname.equals(context.getPackageName())) {
					return true;
				}
			}

		}
		return false;
	}
    
    /**
     * 
     * 将图片转化给byte[]操作
     * 
     * @param bm 图片对象
     * @return 图片byte[]
     */
    public static byte[] bitmap2Bytes(Bitmap bm)
    {
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        bytes = baos.toByteArray();
        try
        {
            baos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return bytes;
    }
    
    /**
     * [释放Bitmap内存]<BR>
     * [功能详细描述]
     * @param bitmap Bitmap 
     */
    public static void recycleIfNeeded(Bitmap bitmap)
    {
        if (bitmap != null && !bitmap.isRecycled())
        {
            bitmap.recycle();
        }
    }
    
    /**
     * 
     * 将byte[]转化成图片
     * 
     * @param b 图片的byte[]
     * @return 图片对象
     */
    public static Bitmap bytes2Bimap(byte[] b)
    {
        if (b.length != 0)
        {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 
     * 将Drawable转换为Bitmap
     * 
     * @param drawable Drawable对象
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,
                0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        
        return bitmap;
    }
    
    /**
     *图片旋转
     * @param bmpOrg 原图片
     * @param rotate 旋转角度（0~360度）
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bmpOrg, int rotate)
    {
        int width = bmpOrg.getWidth();
        int height = bmpOrg.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        
        Bitmap resizeBitmap = Bitmap.createBitmap(bmpOrg,
                0,
                0,
                width,
                height,
                matrix,
                true);
        
        if (null != bmpOrg && !bmpOrg.isRecycled())
        {
            bmpOrg.recycle();
            bmpOrg = null;
        }
        
        return resizeBitmap;
    }
    
    
    /**
     * 
     * 通过byte数组去给指定的ImageView设置圆角背景
     * 
     * @param data 图片数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param adii 圆角大小
     * @param imageView ImageView对象
     */
    public static void drawRoundCorner(byte[] data, int width, int height,
            int adii, ImageView imageView)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (bitmap != null)
        {
            drawRoundCorner(bitmap, width, height, adii, imageView);
        }
    }
    
    /**
     * 
     * 用于给指定头像生成圆角的头像
     * 
     * @param bitmap 头像图片
     * @param width 图片宽度
     * @param height 图片长度
     * @param adii 圆角大小
     * @param imageView ImageView对象
     */
    public static void drawRoundCorner(Bitmap bitmap, int width, int height,
            int adii, ImageView imageView)
    {
        Drawable dwbRound = drawRoundCornerForDrawable(bitmap,
                width,
                height,
                adii);
        imageView.setImageDrawable(dwbRound);
    }
    
    /**
     * 
     * 用于给指定头像生成圆角的头像
     * 
     * @param bitmap 头像图片
     * @param width 图片宽度
     * @param height 图片长度
     * @param adii 圆角大小
     * @return Drawable类型的圆脚头像
     */
    public static Drawable drawRoundCornerForDrawable(Bitmap bitmap, int width,
            int height, int adii)
    {
        Shape shpRound = new RoundRectShape(new float[] { adii, adii, adii,
                adii, adii, adii, adii, adii }, null, null);
        ShapeDrawable dwbRound = new ShapeDrawable(shpRound);
        dwbRound.setIntrinsicWidth(width);
        dwbRound.setIntrinsicHeight(height);
        Shader shdBitmap = new BitmapShader(bitmap, Shader.TileMode.MIRROR,
                Shader.TileMode.MIRROR);
        Matrix matrix = new Matrix();
        matrix.setScale((float) width / bitmap.getWidth(), (float) height
                / bitmap.getHeight());
        shdBitmap.setLocalMatrix(matrix);
        dwbRound.getPaint().setShader(shdBitmap);
        dwbRound.getPaint().setFlags(dwbRound.getPaint().getFlags()
                | Paint.ANTI_ALIAS_FLAG);
        
        return dwbRound;
    }
    
    /**
     * 
     * 根据传进来的聊吧成员头像生成聊吧头像<BR>
     * [功能详细描述]
     * 
     * @param backgroundId 背景图片
     * @param drawables 聊吧头像
     * @param width   头像宽度
     * @param height  头像高度
     * @param context context对象
     * @return Bitmap 生成的聊吧头像
     */
    public static Bitmap createChatBarBitmap(int backgroundId,
            List<Drawable> drawables, int width, int height, Context context)
    {
        Drawable background = context.getResources().getDrawable(backgroundId);
        
      //考虑background为null的情况
        if(null == background)
        {
            Logger.d(TAG,"Geted the drawable of backgroundId is null");
            return null;
        }
        
        // 首先需要生成指定大小的背景，画到canvas上去
        Bitmap bitmap = Bitmap.createBitmap(width,
                height,
                background.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        background.setBounds(0, 0, width, height);
        background.draw(canvas);
        if (drawables != null && drawables.size() > 0)
        {
            Rect[][] rects = createRects(width, height);
            // 然后根据list的大小进行不同画法
            Drawable drawable = null;
            int size = drawables.size();
            if (size > 4)
            {
                size = 4;
            }
            switch (size)
            {
                case 1:
                    drawable = drawables.get(0);
                    drawable.setBounds(rects[0][0]);
                    drawable.draw(canvas);
                    break;
                case 2:
                    drawable = drawables.get(0);
                    drawable.setBounds(rects[0][0]);
                    drawable.draw(canvas);
                    drawable = drawables.get(1);
                    drawable.setBounds(rects[0][1]);
                    drawable.draw(canvas);
                    break;
                case 3:
                    drawable = drawables.get(0);
                    drawable.setBounds(rects[0][0]);
                    drawable.draw(canvas);
                    drawable = drawables.get(1);
                    drawable.setBounds(rects[0][1]);
                    drawable.draw(canvas);
                    drawable = drawables.get(2);
                    drawable.setBounds(rects[1][0]);
                    drawable.draw(canvas);
                    break;
                case 4:
                    drawable = drawables.get(0);
                    drawable.setBounds(rects[0][0]);
                    drawable.draw(canvas);
                    drawable = drawables.get(1);
                    drawable.setBounds(rects[0][1]);
                    drawable.draw(canvas);
                    drawable = drawables.get(2);
                    drawable.setBounds(rects[1][0]);
                    drawable.draw(canvas);
                    drawable = drawables.get(3);
                    drawable.setBounds(rects[1][1]);
                    drawable.draw(canvas);
                    break;
                default:
                    break;
            }
        }
        return bitmap;
    }
    
    
    /**
     * 
     * 根据指定的高度和宽度生成对应的坐标<BR>
     * [功能详细描述]
     * @param width
     * @param height
     * @return
     */
    private static Rect[][] createRects(int width, int height)
    {
        Rect[][] rects = new Rect[2][2];
        int blank = 2;
        // 先算出坐标
        int imageWidth = (width - blank * 3) / 2;
        int imageHeight = (height - blank * 3) / 2;
        Rect rect = null;
        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                rect = new Rect();
                rect.left = imageWidth * j + blank * (j + 1);
                rect.top = imageHeight * i + blank * (i + 1);
                rect.right = imageWidth * (j + 1) + blank * (j + 1);
                rect.bottom = imageHeight * (i + 1) + blank * (i + 1);
                rects[i][j] = rect;
            }
        }
        return rects;
    }
    
    /**
     * 根据最小边长进行压缩图片，以便向服务器上传
     * 
     * @param path 图片路径
     * @return 压缩后的位图
     */
    public static Bitmap getFitBitmap(String path)
    {
        if (path == null)
        {
            Logger.e(TAG, "image path is null");
            return null;
        }
        try
        {
            //图片最大宽度/高度
            int imageWidth = 800;
            
            int imageHeight = 480;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比例
            double ratio = 0.0;
            // if (srcWidth * srcHeight < (IMAGE_WIDTH * IMAGE_HEIGHT))
            // {
            // return BitmapFactory.decodeFile(path);
            // }
            if (srcWidth < srcHeight)
            {
                ratio = (double) srcWidth / imageWidth;
                if (ratio > 1.0)
                {
                    destHeight = (int) (srcHeight / ratio);
                    destWidth = imageWidth;
                }
                else
                {
                    Logger.d(TAG, "small image has generated!");
                    return BitmapFactory.decodeFile(path);
                }
                
            }
            else
            {
                ratio = (double) srcHeight / imageHeight;
                if (ratio > 1.0)
                {
                    destWidth = (int) (srcWidth / ratio);
                    destHeight = imageHeight;
                }
                else
                {
                    Logger.d(TAG, "small image has generated!");
                    return BitmapFactory.decodeFile(path);
                }
                
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            double x = Math.log(ratio) / Math.log(2);
            int k = (int) Math.ceil(x);
            int j = (int) Math.pow(2, k);
            newOpts.inSampleSize = j;
            newOpts.inJustDecodeBounds = false;
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            
            // Tell to gc that whether it needs free memory, the Bitmap can
            // be cleared
            newOpts.inPurgeable = true;
            // Which kind of reference will be used to recover the Bitmap
            // data after being clear, when it will be used in the future
            newOpts.inInputShareable = true;
            // Allocate some temporal memory for decoding
            newOpts.inTempStorage = new byte[64 * 1024];
            
            Bitmap destBm = BitmapFactory.decodeFile(path, newOpts);
            
            Logger.d(TAG, "small image has generated!");
            return destBm;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 图片缩放
     * @param bmp 对图片进行缩放
     * @param scaleWidth 缩放宽度， 0~1为缩小，大于1为放大
     * @param scaleHeight 缩放高度， 0~1为缩小，大于1为放大
     * @return 缩放后的图
     */
    public Bitmap changeBitmap(Bitmap bmp, double scaleWidth, double scaleHeight)
    {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        float fScaleWidth = (float) scaleWidth;
        float fScaleHeight = (float) scaleHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(fScaleWidth, fScaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bmp,
                0,
                0,
                bmpWidth,
                bmpHeight,
                matrix,
                true);
        
        return resizeBmp;
    }
    
    /**
     * 获得图片的
     * @param localPath
     * @return
     */
    public static Bitmap getFittestBitmap(String localPath, int minWorH){
    	Bitmap bitmap = null;
    	if(StringUtils.isNullOrEmpty(localPath) || minWorH <= 0){
    		Logger.e(TAG, "localpath=null || minWorH<=0");
    		bitmap = null;
    	}else{
	    	Logger.v(TAG, "local pic,local path="+localPath);
			Options options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(localPath, options);
			int scale = 1;
			while (true) {  
				 if (options.outWidth / 2 >= minWorH && options.outHeight / 2 >= minWorH){
					 options.outWidth /= 2; 
					 options.outHeight /= 2; 
					 scale++;  
				 }else{
					 break;
				 }
			}
			Logger.v(TAG, "=========inSampleSize=" + scale);  
			options.inSampleSize = scale; 
			options.inJustDecodeBounds = false; 
			bitmap = BitmapFactory.decodeFile(localPath, options);
    	}
		return bitmap;
    }
    
    
    /**
     * 生成小图文件并获取该文件地址(与getFitBitmap(String)配合使用)<BR>
     * 默认生成图片的名字是时间.jpg<BR>
     * @param bitmap Bitmap
     * @return 生成压缩后的小图地址
     */
    public static String saveBitmap(Bitmap bitmap)
    {
        //获取保存路径
        String savePath = UriUtil.getLocalStorageDir(null, UriUtil.LocalDirType.THUMB_NAIL);
        if (null == savePath)
        {
            Logger.e(TAG, "SavePath is null.");
            return null;
        }
        String smallImgPath = savePath + DateUtil.getCurrentDateString()
                + ".jpg";
        
        Logger.d(TAG,
                "getExternalStorageState : "
                        + Environment.getExternalStorageState());
        
        File file = new File(smallImgPath);
        BufferedOutputStream bos;
        try
        {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        }
        catch (FileNotFoundException e)
        {
            Logger.e(TAG, "File is not exsit");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Logger.d(TAG, "small image file has generated! path = " + smallImgPath);
        return smallImgPath;
    }
    
    
    /**
     * 生成小图文件并获取该文件地址(与getFitBitmap(String)配合使用)<BR>
     * 默认生成图片的名字是asnamejpg<BR>
     * @param bitmap
     * @param asnamejpg
     * @return 生成压缩后的小图地址
     */
    public static String saveBitmap(Bitmap bitmap, String asnamejpg)
    {
        //获取保存路径
        String savePath = UriUtil.getLocalStorageDir(null, UriUtil.LocalDirType.THUMB_NAIL);
        if (null == savePath)
        {
            Logger.e(TAG, "SavePath is null.");
            return null;
        }
        String smallImgPath = savePath + asnamejpg;
        
        Logger.d(TAG,
                "getExternalStorageState : "
                        + Environment.getExternalStorageState());
        
        File file = new File(smallImgPath);
        BufferedOutputStream bos;
        try
        {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        }
        catch (FileNotFoundException e)
        {
            Logger.e(TAG, "File is not exsit");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Logger.d(TAG, "small image file has generated! path = " + smallImgPath);
        return smallImgPath;
    }
    
    public static synchronized void storeInSD(Bitmap bitmap , String path , String name) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		File imageFile = new File(path+name);
		try {
			if(!imageFile.exists()){
				Log.v("*******", "path="+path+";name="+name);
				imageFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(imageFile);
				if(name.endsWith("png")){
					bitmap.compress(CompressFormat.PNG, 50, fos);				
				}else if(name.endsWith("jpg") || name.endsWith("jpeg")){
					bitmap.compress(CompressFormat.JPEG, 50, fos);
				}
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
