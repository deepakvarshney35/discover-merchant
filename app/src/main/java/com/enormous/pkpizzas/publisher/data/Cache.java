package com.enormous.pkpizzas.publisher.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.Toast;

public class Cache {

	static Cache instance;
	LruCache<String, Bitmap> lruCache;

	protected Cache() {
		int maxSize = (int) Runtime.getRuntime().maxMemory() / 4;
		lruCache = new LruCache<String, Bitmap>(maxSize);
	}

	public static Cache getInstance() {
		if (instance == null) {
			instance = new Cache();
		}
		return instance;
	}

	public void writeImageToCache(Bitmap bitmap, String fileName, Context c) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(c.getCacheDir().getAbsolutePath() + "/" + fileName);
			bitmap.compress(CompressFormat.JPEG, 50, fos);
			Log.d("TEST", "IMAGE WRITTEN TO CACHE");
		}
		catch (Exception e) {
			Log.d("TEST", "Error writing to cache: " + e.getMessage());
		}
		finally {
			if (fos != null) {
				try {
					fos.close();
				}
				catch (Exception e) {
					Log.d("TEST", e.getMessage());
				}
			}
		}
	}

	public Bitmap getImageFromCache(String url, Context c) {
		Bitmap bitmap;
		String fileName = url.replaceAll("[/:.]", "");
		File imageFile = new File(c.getCacheDir(), fileName);
		if ((bitmap = lruCache.get(fileName)) != null) {
			//			Log.d("TEST", "Image exists in memory cache");
			return bitmap; 
		}
		else if (imageFile.exists()) {
			Log.d("TEST", "Image exists in disk cache");
			bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			lruCache.put(fileName, bitmap);
			return lruCache.get(fileName);
		}
		else {
			Log.d("TEST", "Downloading image from Parse");
			bitmap = downloadBitmap(url);
			if (bitmap != null) {
				writeImageToCache(bitmap, fileName, c);
				lruCache.put(fileName, bitmap);
				return lruCache.get(fileName);
			}
		}
		return null;
	}
	
	private Bitmap downloadBitmap(String url) {
		HttpURLConnection hConnection = null;
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			URL imageURL = new URL(url);
			hConnection = (HttpURLConnection) imageURL.openConnection();
			hConnection.setReadTimeout(3000);
			is = hConnection.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (hConnection != null) {
				try {
					hConnection.disconnect();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	public void clearCache(Context c) {
		//clear disk cache
		File[] files = c.getCacheDir().listFiles();
		for (File file : files) {
			file.delete();
		}
		
		//clear memory cache
		lruCache.evictAll();
		
		Toast.makeText(c, files.length + " file(s) deleted", Toast.LENGTH_LONG).show();
		Log.d("TEST", files.length + "file(s) deleted");
	}
}
