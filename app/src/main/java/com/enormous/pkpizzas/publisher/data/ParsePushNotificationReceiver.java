package com.enormous.pkpizzas.publisher.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.enormous.pkpizzas.publisher.R;
import com.enormous.pkpizzas.publisher.activities.ChatMessageActivity;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ParsePushNotificationReceiver extends BroadcastReceiver {

	private ParseUser user;
	public static String openedConfession = "";
    public String customerEmail,customerPhone,customerProfilePictureUrl;
    public int customerSharedOfferNumber,customerCartItemsNumber;

	@Override
	public void onReceive(Context context, Intent intent) {
		user = ParseUser.getCurrentUser();
		if(user!=null){
			try {
				//String action = intent.getAction();
				//String channel = intent.getExtras().getString("com.parse.Channel");
				JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

				String message = "New Chat!";
				if (json.has("header"))
					message = json.getString("header");

				String merchantUserId = user.getObjectId();
				if (json.has("merchantUserId"))
					merchantUserId = json.getString("merchantUserId");

				String myObjectId = user.getObjectId();
				if (json.has("myObjectId"))
					myObjectId = json.getString("myObjectId");

				String customerObjectId = "";
				if (json.has("customerObjectId"))
					customerObjectId = json.getString("customerObjectId");

				String profilePictureUrl = "";
				if (json.has("profilePictureUrl"))
					profilePictureUrl = json.getString("profilePictureUrl");

				String customerName = "";
				if (json.has("customerName"))
					customerName = json.getString("customerName");

				if(myObjectId.equals(user.getObjectId())){
					Log.d("tag", "same user notification");
					//will be commented after testing
					//generateCustomNotification(context,channel, message ,Confession,attachedPhotoURL,confessionObjectId,voteCountNum,userObjectId);
				} else {
					if("".equals(openedConfession)){
						Log.d("tag", "notification when confessions are closed");
						//
						generateCustomNotification(context, message ,merchantUserId,customerObjectId,customerName,profilePictureUrl);
					} else if((customerObjectId+merchantUserId).equals(openedConfession)){
						Log.d("tag", "confession opened but is real time - no notification");
					}else{
						Log.d("tag", "confession opened but notification in other confession");
						generateCustomNotification(context, message ,merchantUserId,customerObjectId,customerName,profilePictureUrl);

					}
				}
			} catch (JSONException e) {
				Log.d("ParsePushNotificationReceiver", "JSONException: " + e.getMessage());
			}}
	}


	public void generateCustomNotification(Context context, String message, String merchantUserId, String customerObjectId, String customerName,String profilePictureUrl) {

        //Number of notifications
		SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Service.MODE_PRIVATE);
		int notifications = sharedpreferences.getInt("key"+customerObjectId, 0);
		notifications= notifications +1;

        //Get Customer Data
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Checkin");
        query.whereEqualTo("brandObjectId",ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("userObjectId",customerObjectId);
        query.whereEqualTo("archive", false);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> checkInList, com.parse.ParseException e) {
                if (e == null) {
                    customerEmail = checkInList.get(0).getString("email");
                    customerPhone = checkInList.get(0).getString("phoneNumber");
                    customerProfilePictureUrl = checkInList.get(0).getString("profilePictureUrl");
                    customerSharedOfferNumber = checkInList.get(0).getInt("SharedOffer");
                    customerCartItemsNumber = checkInList.get(0).getInt("CartItems");
                }
            }});

		Editor editor = sharedpreferences.edit();
		editor.putInt("key"+customerObjectId, notifications);
		editor.commit();
		// Show the notification
		if(message.contains("attachmentPicture.jpg")){
			if("".equals(profilePictureUrl)){
				//Photo chat without profile picture
				new sendNotification(context).execute(message,customerName,customerObjectId);
			}else{
				//Photo Chat with profile picture
				new sendNotificationPhoto(context).execute(message,customerName,customerObjectId,profilePictureUrl);
			}
		}else{
			if("".equals(profilePictureUrl)){
				//Normal Chat no profile picture
				final Resources res = context.getResources();
				final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
				NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				builder.setLargeIcon(Bitmap.createScaledBitmap(
						Utils.decodeImageResource(res, res.getIdentifier("default_profilepic", "drawable", context.getPackageName()), 250, 250)
						, (int) res.getDimension(android.R.dimen.notification_large_icon_width)
						, (int) res.getDimension(android.R.dimen.notification_large_icon_height)
						, false))
						.setSmallIcon(R.drawable.ic_notification_icon)
						.setContentTitle(customerName)
						.setContentText(message)
						.setNumber(notifications)
						.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

				Intent notificationIntent = new Intent(context, ChatMessageActivity.class);
				// set intent so it does not start a new activity
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				notificationIntent.putExtra("customerObjectId", customerObjectId);
				notificationIntent.putExtra("customerName", customerName);
                notificationIntent.putExtra("customerEmail",customerEmail);
                notificationIntent.putExtra("customerPhone",customerPhone);
                notificationIntent.putExtra("customerProfilePictureUrl",customerProfilePictureUrl);
                notificationIntent.putExtra("customerSharedOfferNumber",customerSharedOfferNumber);
                notificationIntent.putExtra("customerCartItemsNumber",customerCartItemsNumber);
				int NOTIFICATION_ID = customerObjectId.hashCode();
				PendingIntent intent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(intent);
				Notification notification = builder.build();
				notification.defaults = Notification.DEFAULT_ALL;
				notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(NOTIFICATION_ID, notification);
			}else{
				//Normal Chat with profile picture
				if("0".equals(profilePictureUrl.substring(profilePictureUrl.length() - 1))){
					profilePictureUrl = profilePictureUrl.substring(0, profilePictureUrl.length()-2);
					profilePictureUrl = profilePictureUrl+"250";
					Log.d("TAG", profilePictureUrl+"");
				}
				new sendNotificationDefault(context).execute(message,customerName,customerObjectId,profilePictureUrl,new Integer(notifications).toString());
			}
		}
	}
	private class sendNotification extends AsyncTask<String, Void, Bitmap> {

		Context context;
		String message,customerObjectId,customerName;

		public sendNotification(Context context) {
			super();
			this.context = context;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			InputStream in;
			customerName = params[1] ;
			customerObjectId = params[2] ;

			try {
				URL url = new URL(params[0]);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				in = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(in);

				return myBitmap;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Bitmap result) {

			super.onPostExecute(result);
			final Resources res = context.getResources();
			final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			builder.setLargeIcon(Bitmap.createScaledBitmap(
					Utils.decodeImageResource(res, res.getIdentifier("default_profilepic", "drawable", context.getPackageName()), 250, 250)
					, (int) res.getDimension(android.R.dimen.notification_large_icon_width)
					, (int) res.getDimension(android.R.dimen.notification_large_icon_height)
					, false))
					.setSmallIcon(R.drawable.ic_notification_icon)
					.setContentTitle(customerName)
					.setStyle(new NotificationCompat.BigPictureStyle()
					.bigPicture(result)
					.setBigContentTitle(customerName)
					.bigLargeIcon(result));
			Intent notificationIntent = new Intent(context, ChatMessageActivity.class);
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			notificationIntent.putExtra("customerObjectId", customerObjectId);
			notificationIntent.putExtra("customerName", customerName);
            notificationIntent.putExtra("customerEmail",customerEmail);
            notificationIntent.putExtra("customerPhone",customerPhone);
            notificationIntent.putExtra("customerProfilePictureUrl",customerProfilePictureUrl);
            notificationIntent.putExtra("customerSharedOfferNumber",customerSharedOfferNumber);
            notificationIntent.putExtra("customerCartItemsNumber",customerCartItemsNumber);
			int NOTIFICATION_ID = customerObjectId.hashCode();
			PendingIntent intent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(intent);
			Notification notification = builder.build();
			//notification.bigContentView = bigView;
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(NOTIFICATION_ID, notification);
		}
	}

	private class sendNotificationPhoto extends AsyncTask<String, Void, ArrayList<Bitmap>> {

		Context context;
		String message,customerObjectId,customerName,profilePictureUrl;
		ArrayList<Bitmap> myBitmaps;

		public sendNotificationPhoto(Context context) {
			super();
			this.context = context;
		}

		@Override
		protected ArrayList<Bitmap> doInBackground(String... params) {
			Bitmap myBitmap1 = null;
			InputStream in,in1;
			customerName = params[1] ;
			customerObjectId = params[2] ;
			profilePictureUrl = params[3];

			try {
				URL url1 = new URL(profilePictureUrl);
				HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
				connection1.setDoInput(true);
				connection1.connect();
				in1 = connection1.getInputStream();
				myBitmap1 = BitmapFactory.decodeStream(in1);
				URL url = new URL(params[0]);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				in = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(in);

				myBitmaps = new ArrayList<Bitmap>();
				myBitmaps.add(myBitmap1);
				myBitmaps.add(myBitmap);
				return myBitmaps;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(ArrayList<Bitmap> result) {

			super.onPostExecute(result);
			final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			builder.setLargeIcon(result.get(0))
			.setSmallIcon(R.drawable.ic_notification_icon)
			.setContentTitle(customerName)
			.setStyle(new NotificationCompat.BigPictureStyle()
			.bigPicture(result.get(1))
			.setBigContentTitle(customerName)
			.bigLargeIcon(result.get(1)));
			Intent notificationIntent = new Intent(context, ChatMessageActivity.class);
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			notificationIntent.putExtra("customerObjectId", customerObjectId);
			notificationIntent.putExtra("customerName", customerName);
            notificationIntent.putExtra("customerEmail",customerEmail);
            notificationIntent.putExtra("customerPhone",customerPhone);
            notificationIntent.putExtra("customerProfilePictureUrl",customerProfilePictureUrl);
            notificationIntent.putExtra("customerSharedOfferNumber",customerSharedOfferNumber);
            notificationIntent.putExtra("customerCartItemsNumber",customerCartItemsNumber);
			int NOTIFICATION_ID = customerObjectId.hashCode();
			PendingIntent intent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(intent);
			Notification notification = builder.build();
			//notification.bigContentView = bigView;
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(NOTIFICATION_ID, notification);
		}
	}

	private class sendNotificationDefault extends AsyncTask<String, Void, Bitmap> {

		Context context;
		String message,customerObjectId,customerName,notifications;

		public sendNotificationDefault(Context context) {
			super();
			this.context = context;
		}

		@Override
		protected Bitmap doInBackground(String... params) {

			InputStream in;
			message = params[0];
			customerName = params[1] ;
			customerObjectId = params[2] ;
			notifications = params[4];
			try {

				URL url = new URL(params[3]);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				in = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(in);
				return myBitmap;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Bitmap result) {

            final Bitmap resultIn=result;
			super.onPostExecute(result);

            //Get Customer Data
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Checkin");
            query.whereEqualTo("brandObjectId",ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo("userObjectId",customerObjectId);
            query.whereEqualTo("archive", false);
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> checkInList, com.parse.ParseException e) {
                    if (e == null) {
                        customerEmail = checkInList.get(0).getString("email");
                        customerPhone = checkInList.get(0).getString("phoneNumber");
                        customerProfilePictureUrl = checkInList.get(0).getString("profilePictureUrl");
                        customerSharedOfferNumber = checkInList.get(0).getInt("SharedOffer");
                        customerCartItemsNumber = checkInList.get(0).getInt("CartItems");

                        userNotify();
                    }
                }

                private void userNotify() {
                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                    builder.setLargeIcon(resultIn)
                            .setSmallIcon(R.drawable.ic_notification_icon)
                            .setContentTitle(customerName)
                            .setContentText(message)
                            .setNumber(Integer.parseInt(notifications))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
                    Intent notificationIntent = new Intent(context, ChatMessageActivity.class);
                    // set intent so it does not start a new activity
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    notificationIntent.putExtra("customerObjectId", customerObjectId);
                    notificationIntent.putExtra("customerName", customerName);
                    notificationIntent.putExtra("customerEmail",customerEmail);
                    notificationIntent.putExtra("customerPhone",customerPhone);
                    notificationIntent.putExtra("customerProfilePictureUrl",customerProfilePictureUrl);
                    notificationIntent.putExtra("customerSharedOfferNumber",customerSharedOfferNumber);
                    notificationIntent.putExtra("customerCartItemsNumber",customerCartItemsNumber);
                    int NOTIFICATION_ID = customerObjectId.hashCode();
                    PendingIntent intent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(intent);
                    Notification notification = builder.build();
                    notification.defaults = Notification.DEFAULT_ALL;
                    notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(NOTIFICATION_ID, notification);
                }
            });


		}
    }
}

