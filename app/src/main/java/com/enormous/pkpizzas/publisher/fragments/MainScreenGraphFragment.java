package com.enormous.pkpizzas.publisher.fragments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.enormous.pkpizzas.publisher.activities.ChatMessageActivity;
import com.enormous.pkpizzas.publisher.data.Customers;
import com.enormous.pkpizzas.publisher.data.ImageLoader;
import com.enormous.pkpizzas.publisher.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle.GridStyle;
import com.jjoe64.graphview.LineGraphView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainScreenGraphFragment extends ListFragment {

	SwipeRefreshLayout swipeRefreshLayout;
	ListView clientProfileList;
	Handler handler;
	public static ArrayList<Customers> customers ;
	CustomersListViewAdapter adapter;
	ImageView exportCSV;
    public static Object customerTag;
	LinearLayout layout,graphPage;
	LinearLayout noInternetLinearLayout;
	TextView noVisitorsPlaceholder;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_graph_main_screen, container, false);

		findViews(view);

		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		handler = new Handler();
		//db = new DatabaseHandler(getActivity());
		customers = new ArrayList<Customers>();

		//add header to categoriesListView
		View header = inflater.inflate(R.layout.listview_graph_header, null);
		clientProfileList.addHeaderView(header);
		noVisitorsPlaceholder = (TextView)header.findViewById(R.id.noVisitorsPlaceholder);
		noVisitorsPlaceholder.setVisibility(View.GONE);
		
		adapter = new CustomersListViewAdapter();
		clientProfileList.setAdapter(adapter);


		noInternetLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GetCustomersTask getCustomersTask = new GetCustomersTask();
				getCustomersTask.execute();				
			}
		});
		
		//adapter = new ArrayAdapter(getActivity(),R.layout.listview_customers,R.id.customerName,customerNames);

		//fetch brand items
		//GetCustomersTask getCustomersTask = new GetCustomersTask();
		//getCustomersTask.execute();
		GetCustomersTask getCustomersTask = new GetCustomersTask();
		getCustomersTask.execute();

		//add ability to pull to refresh
		swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
				android.R.color.holo_green_light, 
				android.R.color.holo_orange_light, 
				android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				swipeRefreshLayout.setRefreshing(true);
				//getPushNotification(pushList, alertsAdapter);
				GetCustomersTask getCustomersTask = new GetCustomersTask();
				getCustomersTask.execute();
				//alertsAdapter.notifyDataSetChanged();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						swipeRefreshLayout.setRefreshing(false);
					}
				}, 5000);
			}
		});
		if (swipeRefreshLayout != null) {
			swipeRefreshLayout.setRefreshing(true);
		}

		/*		refreshGraph.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				graph(view);
			}
		});*/

		exportCSV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					exportEmailInCSV();
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
					emailIntent.setType("application/*");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ParseUser.getCurrentUser().getEmail()}); 
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Client List"); 
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, ".CSV file"); 
					emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Discover/clients.csv"));
					startActivity(Intent.createChooser(emailIntent, "Send CSV file via..."));
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
		return view;
	}
	public void findViews(View view) {
		clientProfileList = (ListView) view.findViewById(android.R.id.list);
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
		exportCSV = (ImageView)view.findViewById(R.id.exportCSV);
		graphPage = (LinearLayout) view.findViewById(R.id.graphPage);
		noInternetLinearLayout = (LinearLayout)view.findViewById(R.id.noInternetLinearLayout);
	}

	private void graph(View view) {
		int num = customers.size();
		if(num!=0){
			Date date = new Date(System.currentTimeMillis());
			Long time = date.getTime();
			int yToday = 0;
			int yYest = 0;
			int yYest1 = 0;
			int yYest2 = 0;
			int yYest3 = 0;
			int yYest4 = 0;
			for (int i=0; i<num; i++) {
				if((time-86400000)<customers.get(i).getUpdatedAt().getTime()){
					yToday++;
				} 
				if((time-86400000*2)<customers.get(i).getUpdatedAt().getTime() && (time-86400000)>customers.get(i).getUpdatedAt().getTime()){
					yYest++;
				}  
				if((time-86400000*4)<customers.get(i).getUpdatedAt().getTime() && (time-86400000*2)>customers.get(i).getUpdatedAt().getTime() ){
					yYest1++;
				}
				if((time-86400000*6)<customers.get(i).getUpdatedAt().getTime() && (time-86400000*4)>customers.get(i).getUpdatedAt().getTime() ){
					yYest2++;
				}
				if((time-86400000*8)<customers.get(i).getUpdatedAt().getTime() && (time-86400000*6)>customers.get(i).getUpdatedAt().getTime() ){
					yYest3++;
				}
				if((time-86400000*10)<customers.get(i).getUpdatedAt().getTime() && (time-86400000*8)>customers.get(i).getUpdatedAt().getTime() ){
					yYest4++;
				}
			}
			// init example series data
			GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
					new GraphViewData(1, yYest4)
					,new GraphViewData(1, yYest3)
					,new GraphViewData(1, yYest2)
					, new GraphViewData(2, yYest1)
					, new GraphViewData(3, yYest)
					, new GraphViewData(4, yToday)
			});

			GraphView graphView = new LineGraphView (
					getActivity().getApplicationContext() // context
					, "Visitors" // heading
					){
				@Override
				protected String formatLabel(double value, boolean isValueX) {
					// return as Integer
					return ""+((int) value);
				}
			};
			graphView.addSeries(exampleSeries); // data

			graphView.getGraphViewStyle().setGridColor(Color.BLACK);
			graphView.getGraphViewStyle().setGridStyle(GridStyle.BOTH);
			graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.RED);
			graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
			// set view port, start=2, size=40
			//graphView.setViewPort(2, 10);
			graphView.setScrollable(true);
			// optional - activate scaling / zooming
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -2);
			Calendar d = Calendar.getInstance();
			d.add(Calendar.DATE, -3);
			Calendar e = Calendar.getInstance();
			e.add(Calendar.DATE, -4);
			Calendar f = Calendar.getInstance();
			f.add(Calendar.DATE, -5);
			graphView.setHorizontalLabels(new String[] {f.get(Calendar.DAY_OF_MONTH)+"-"+(f.get(Calendar.MONTH)+1),e.get(Calendar.DAY_OF_MONTH)+"-"+(e.get(Calendar.MONTH)+1),d.get(Calendar.DAY_OF_MONTH)+"-"+(d.get(Calendar.MONTH)+1), c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1), "Yest.", "Today"});
			graphView.setScalable(true);		
			//layout.addView(graphView); 
			//refreshGraph.setVisibility(View.GONE);
		}
	}

	private class CustomersListViewAdapter extends BaseAdapter {

		public CustomersListViewAdapter() {
		}

		@Override
		public int getCount() {
			return customers.size();
		}

		@Override
		public Object getItem(int pos) {
			return customers.get(pos);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int pos, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				row = infalInflater.inflate(R.layout.listview_customers, parent, false);
			}

			SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MyPrefs", Service.MODE_PRIVATE);
			int notifications = sharedpreferences.getInt("key"+customers.get(pos).getUserObjectId(), 0);

			TextView customerName = (TextView) row.findViewById(R.id.customerName);
			//TextView customerInformation = (TextView) row.findViewById(R.id.customerInformation);
			ImageView customerProfilePicture = (ImageView) row.findViewById(R.id.customerProfilePicture);
			TextView categoryCountTextView = (TextView) row.findViewById(R.id.categoryCountTextView);

			LinearLayout select =(LinearLayout)  row.findViewById(R.id.select);
            LinearLayout customerDetails =(LinearLayout)  row.findViewById(R.id.customerDetails);

            customerTag = customers.get(pos);

			customerName.setText(customers.get(pos).getName());
			//customerInformation.setText("Email: "+customers.get(pos).getEmail()+" \nPhone: "+customers.get(pos).getPhone());
			if(customers.get(pos).getProfilePictureUrl()!=null){
			ImageLoader.getInstance().displayImage(getActivity().getApplicationContext(), customers.get(pos).getProfilePictureUrl(), customerProfilePicture, false, 50, 50, 0);
			}
			int count = customers.get(pos).getCartItemsNumber()+customers.get(pos).getSharedOfferNumber();
			count = count + notifications;
			if(count>0){
				categoryCountTextView.setVisibility(View.VISIBLE);
				categoryCountTextView.setText(""+count);
			}else{
				categoryCountTextView.setVisibility(View.GONE);
			}

			customerProfilePicture.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					View customDialog = getActivity().getLayoutInflater().inflate(R.layout.dialog_profile_view, null);
					ImageView photo = (ImageView) customDialog.findViewById(R.id.customerProfilePicture);
					TextView name = (TextView) customDialog.findViewById(R.id.customeName);
					String picUrl = customers.get(pos).getProfilePictureUrl();
					name.setText(customers.get(pos).getName());  
					if(picUrl!=null){
					if("0".equals(picUrl.substring(picUrl.length() - 1))){
						picUrl = customers.get(pos).getProfilePictureUrl()+"0";
					}
					ImageLoader.getInstance().clearImageCache();
					ImageLoader.getInstance().displayImage(getActivity().getApplicationContext(), picUrl, photo, false, 500, 500, 0);
					}
					builder.setView(customDialog);
					final Dialog dialog = builder.create();
					dialog.show();
				}

			});

            customerDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToClientInfo2 = new Intent(getActivity(), ChatMessageActivity.class);
                    goToClientInfo2.putExtra("customerObjectId",customers.get(pos).getUserObjectId());
                    goToClientInfo2.putExtra("customerName",customers.get(pos).getName());
                    goToClientInfo2.putExtra("customerEmail",customers.get(pos).getEmail());
                    goToClientInfo2.putExtra("customerPhone",customers.get(pos).getPhone());
                    goToClientInfo2.putExtra("customerProfilePictureUrl",customers.get(pos).getProfilePictureUrl());
                    goToClientInfo2.putExtra("customerSharedOfferNumber",customers.get(pos).getSharedOfferNumber());
                    goToClientInfo2.putExtra("customerCartItemsNumber",customers.get(pos).getCartItemsNumber());
                    startActivity(goToClientInfo2);
                    getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
                }

            });

			//customerProfilePicture.setImageBitmap();

			return row;
		}
	}
	public void exportEmailInCSV() throws IOException {
		{

			File folder = new File(Environment.getExternalStorageDirectory()
					+ "/Discover");

			boolean var = false;
			if (!folder.exists())
				var = folder.mkdir();

			System.out.println("" + var);


			final String filename = folder.toString() + "/" + "clients.csv";

			// show waiting screen
			CharSequence contentTitle = getString(R.string.app_name);
			final ProgressDialog progDailog = ProgressDialog.show(
					getActivity(), contentTitle, "even geduld aub...",
					true);//please wait
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
				}
			};

			new Thread() {
				public void run() {
					try {

						FileWriter fw = new FileWriter(filename);

						fw.append("Name");
						fw.append(',');

						fw.append("Phone");
						fw.append(',');

						fw.append("Email");
						fw.append(',');

						fw.append('\n');


						for (Customers cn : customers) {
							String log = "Name: " + cn.getName() + " ,Phone: " + cn.getPhone();
							// Writing Contacts to log
							Log.d("Export: ", log);
							fw.append(cn.getName());
							fw.append(',');
							fw.append(cn.getPhone());
							fw.append(',');
							fw.append(cn.getEmail());
							fw.append(',');
							fw.append('\n');
						}

						fw.close();

					} catch (Exception e) {
					}
					handler.sendEmptyMessage(0);
					progDailog.dismiss();
				}
			}.start();

		}

	}


	public class GetCustomersTask extends AsyncTask<Void, Void, ArrayList<Customers>> {

		ArrayList<Customers> customers;
		private boolean failed = false;
		public GetCustomersTask() {
			customers = new ArrayList<Customers>();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (swipeRefreshLayout != null) {
				swipeRefreshLayout.setRefreshing(true);
			}
		}

		@Override
		protected ArrayList<Customers> doInBackground(Void... params) {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Checkin");
			query.whereEqualTo("brandObjectId",ParseUser.getCurrentUser().getObjectId() );
			query.addDescendingOrder("updatedAt");
			query.whereEqualTo("archive", false);
			try {				
				List<ParseObject> results = query.find();
				for (ParseObject result : results) {
					customers.add(new Customers(result.getString("fullName"), result.getString("phoneNumber"), result.getString("email"), result.getString("profilePictureUrl"), result.getString("userObjectId"),result.getDate("date"),result.getInt("SharedOffer"),result.getInt("CartItems")));
					//db.addContact(new Customers(result.getString("fullName"), result.getString("phoneNumber")));
				}
			}
			catch (Exception e) {
				Log.e("TEST", "Error retrieving brand items: " + e.getMessage());
				failed = true;
			}
			return customers;
		}

		@Override
		protected void onPostExecute(ArrayList<Customers> result) {
			super.onPostExecute(result);
			MainScreenGraphFragment.this.customers.clear();
			MainScreenGraphFragment.this.customers.addAll(result);
			if (result.size() == 0) {				
				exportCSV.setVisibility(View.GONE);
				noVisitorsPlaceholder.setVisibility(View.VISIBLE);
				//layout.setVisibility(View.GONE);
			}
			else {
				exportCSV.setVisibility(View.VISIBLE);
				noInternetLinearLayout.setVisibility(View.GONE);
				graphPage.setVisibility(View.VISIBLE);
				noVisitorsPlaceholder.setVisibility(View.GONE);
			}
			adapter.notifyDataSetChanged();
			if (swipeRefreshLayout != null) {
				swipeRefreshLayout.setRefreshing(false);
			}
			if(failed){
				noInternetLinearLayout.setVisibility(View.VISIBLE);
				graphPage.setVisibility(View.GONE);
			}
		}
	}
	@Override
	public void onResume() {

        adapter.notifyDataSetChanged();

		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
	}
}
