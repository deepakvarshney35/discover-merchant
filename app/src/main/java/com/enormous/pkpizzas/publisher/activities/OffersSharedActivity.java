package com.enormous.pkpizzas.publisher.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.enormous.pkpizzas.publisher.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OffersSharedActivity extends Activity {

	private ListView offersListView;
	private offersListAdapter offersAdapter;
	private ArrayList<String> offerItems;
	private ArrayList<Date> offerItemsTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shared_offers);
		
		//Portrait mode
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		ActionBar actionBar = getActionBar();
		
		offersListView = (ListView)findViewById(android.R.id.list);
		//set actionBar properties
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.indigo500)));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		offerItems = new ArrayList<String>();
		offerItemsTime = new ArrayList<Date>();
		offersAdapter = new offersListAdapter(this,R.layout.listview_offers, offerItems , offerItemsTime);
		offersListView.setAdapter(offersAdapter);
		
		ParseQuery<ParseObject> check = ParseQuery.getQuery("Checkin");
		check.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
		check.whereEqualTo("userObjectId", getIntent().getExtras().getString("customersObjectId"));
		check.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> checkins, ParseException e) {
				// TODO Auto-generated method stub
				for(ParseObject checkin : checkins){
					checkin.put("SharedOffer", 0);
					checkin.saveInBackground();
				}
			}
		});
		
		final ProgressDialog progress;
		progress = ProgressDialog.show(OffersSharedActivity.this, "Loading",
			    "Loading...", true);
		ParseQuery<ParseObject> query = ParseQuery.getQuery("SharedOffer");
		//query.whereEqualTo("brandName", ParseUser.getCurrentUser().get("brandName"));
		query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
		query.whereEqualTo("userObjectId", getIntent().getExtras().getString("customersObjectId"));
		query.addDescendingOrder("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> offerNameList, ParseException e) {
				if (e == null) {
					for(ParseObject offerName: offerNameList)	{
						offerItems.add(offerName.getString("itemName"));
						offerItemsTime.add(offerName.getDate("date"));
					}
					CharSequence[] offers = offerItems.toArray(new CharSequence[offerItems.size()]);
					if(offers.length!=0){
						progress.dismiss();
						offersAdapter.notifyDataSetChanged();
					} else{
						progress.dismiss();
						Toast.makeText(OffersSharedActivity.this, "No offers Shared", Toast.LENGTH_SHORT).show();;
					}
					
					
				} else {
					progress.dismiss();
					Toast.makeText(OffersSharedActivity.this, "ERROR", Toast.LENGTH_SHORT).show();;
				}
			}
		});
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.scale_in, R.anim.slide_out_right);
	}


	class offersListAdapter extends ArrayAdapter<String> {

		private Context context;
		private ArrayList<String> offers;
		private int resource;
		private ArrayList<Date> offerItemsTime;

		class ViewHolder {
			TextView tvHelpAlertName, tvTime,Message;
			//ImageView ivPushProfilePic;

			public ViewHolder(View view) {
				tvHelpAlertName = (TextView) view.findViewById(R.id.tvOfferShared);
				//ivPushProfilePic = (ImageView) view.findViewById(R.id.ivPushProfilePic);
				tvTime = (TextView) view.findViewById(R.id.tvHelpAlertTime);
				Message = (TextView)view.findViewById(R.id.tvOfferSharedDesc);
			}
		}

		public offersListAdapter(Context context, int resource, ArrayList<String> offers, ArrayList<Date> offerItemsTime) {
			super(context, resource, offers);

			this.context = context;
			this.offers = offers;
			this.resource = resource;
			this.offerItemsTime = offerItemsTime;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final ViewHolder holder;
			SimpleDateFormat df1 = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat day = new SimpleDateFormat("dd");
			SimpleDateFormat month = new SimpleDateFormat("MM");
			SimpleDateFormat df2 = new SimpleDateFormat("dd-MM\nhh:mm a");
			View rowView = convertView;

			if (rowView == null) {
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();
				rowView = inflater.inflate(resource, null, true);
				holder = new ViewHolder(rowView);
				rowView.setTag(holder);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}
			holder.tvHelpAlertName.setText(offers.get(position));
			//holder.ivPushProfilePic.setImageDrawable(context.getResources().getDrawable(R.drawable.plus));
			//holder.ivPushProfilePic.setTag(getIntent().getExtras().getString("customersPicUrl"));
			holder.Message.setText(getIntent().getExtras().getString("customersName")+" shared this on FB.");
			Calendar c = Calendar.getInstance();
			int dayCal = c.get(Calendar.DAY_OF_MONTH);
			int monthCal = c.get(Calendar.MONTH) + 1; //java month starts with 0
			if(Integer.parseInt(day.format(offerItemsTime.get(position)))==dayCal && Integer.parseInt(month.format(offerItemsTime.get(position)))==monthCal){
				holder.tvTime.setText("Today\n"+df1.format(offerItemsTime.get(position)));
			}
			else{
				holder.tvTime.setText(df2.format(offerItemsTime.get(position)));
			}
			
			//new DownloadImageTask(context, holder.ivPushProfilePic).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			return rowView;
		}

}
}
