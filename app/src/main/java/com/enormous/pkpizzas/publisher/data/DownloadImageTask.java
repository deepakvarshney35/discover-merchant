package com.enormous.pkpizzas.publisher.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>{
	ImageView imageView;
	Context c;
	
	public DownloadImageTask(Context c, ImageView imageView) {
		this.c = c;
		this.imageView = imageView;
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		return Cache.getInstance().getImageFromCache((String) imageView.getTag(), c);
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		if (result != null) {			
			imageView.setImageBitmap(getRoundedCornerBitmap(result, 10));
		}
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		int side = 0;
		if (bitmap.getWidth() < bitmap.getHeight()) {
			side = bitmap.getWidth();
		} else {
			side = bitmap.getHeight();
		}
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		// canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
		// bitmap.getWidth() / 2, paint);
		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, side / 2, paint);

		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE); // define paint style as Stroke
		paint.setStrokeWidth(1f);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

}
