package com.enormous.pkpizzas.publisher.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.enormous.pkpizzas.publisher.adapter.SwipeDismissListViewTouchListener;
import com.enormous.pkpizzas.publisher.data.MyBeacon;
import com.enormous.pkpizzas.publisher.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

public class BeaconSetupActivity extends Activity implements BeaconConsumer {

	private BeaconManager BeaconManager;

	private ArrayList<MyBeacon> NewBeacons;
	private ArrayList<MyBeacon> SavedBeacons;

	private String BeaconAddress ;
	private int BeaconMajor ;
	private int BeaconMinor ;
	private int BeaconProximity ;
	String scannedUUID[];
	String scannedMessage[];

	private LinearLayout LayoutAboveAll;
	private ListView listView;
	private ImageView addOffer;
	public static BeaconListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.beacon_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent); 
		listView = (ListView)findViewById(R.id.listbeacons);
		LayoutAboveAll = (LinearLayout)findViewById(R.id.LayoutAboveAll);

		BeaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
		try {
			BeaconManager.setForegroundScanPeriod(1100l); // 1100 mS
			BeaconManager.setForegroundBetweenScanPeriod(10000l); // 30,000ms = 30 seconds
			BeaconManager.updateScanPeriods();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

		//Detecting ibeacons instead of altbeacons...
		BeaconManager.getBeaconParsers().add(new BeaconParser().
				setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

		BeaconManager.bind(this);

		GetBeaconListFromParse();

		addOffer = (ImageView) findViewById(R.id.button1);
		addOffer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder alert=new AlertDialog.Builder(BeaconSetupActivity.this);
				if(NewBeacons!=null){
					if(NewBeacons.size()!=0){
						alert.setTitle("Select Beacon:");
						scannedUUID = new String[NewBeacons.size()];
						scannedMessage = new String[NewBeacons.size()];

						for(int i=0; i<NewBeacons.size();i++){
							String prox;
							int beaconProximity = (int)NewBeacons.get(i).getBeaconProximity();
							if(beaconProximity==0){
								prox = "Immediate";	
							}else if (beaconProximity==1){
								prox="Close";
							}else if (beaconProximity==2){
								prox="Far";
							}else if (beaconProximity==3){
								prox="Very Far";
							}else{
								prox="Very very far";
							}
							scannedUUID[i] = NewBeacons.get(i).getBeaconUUID();
							scannedMessage[i] = NewBeacons.get(i).getBeaconUUID()+" - "+prox;
						}
						alert.setItems(scannedMessage, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								String uuid = NewBeacons.get(which).getBeaconUUID();
								BeaconAddress = NewBeacons.get(which).getAddress();
								BeaconMajor = NewBeacons.get(which).getBeaconMajor();
								BeaconMinor = NewBeacons.get(which).getBeaconMinor();
								BeaconProximity = NewBeacons.get(which).getBeaconProximity();
								logToDisplay(uuid);
							}
						});
						alert.show();}else{
							Toast.makeText(BeaconSetupActivity.this, "No Beacons Found", Toast.LENGTH_SHORT).show();
						}
				}
			}});


		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				Log.d("TEST", "clicked list");
			}
		});

		// ------------------- Swipe to delete item in list view-------------------

		SwipeDismissListViewTouchListener touchListener =
				new SwipeDismissListViewTouchListener(
						listView,
						new SwipeDismissListViewTouchListener.DismissCallbacks() {
							@Override
							public boolean canDismiss(int p) {
								return true;
							}

							@Override
							public void onDismiss(ListView listView, int[] reverseSortedPositions) {

								for (int position : reverseSortedPositions) {
									ParseQuery<ParseObject> query = ParseQuery.getQuery("Beacon");
									query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
									query.whereEqualTo("BeaconUUID", SavedBeacons.get(position).getBeaconUUID());
									query.findInBackground(new FindCallback<ParseObject>() {
										public void done(List<ParseObject> itemList, ParseException e) {
											if (e == null) {
												for(ParseObject Item : itemList){
													Item.deleteInBackground();	 
													ParseUser.getCurrentUser().put("UUID", "");
													ParseUser.getCurrentUser().saveInBackground();
												}
											}}});
									SavedBeacons.remove(position);

									Log.d("check", "done "+position);

									mAdapter.notifyDataSetChanged();

								}}
						});
		listView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during ListView scrolling,
		// we don't look for swipes.
		listView.setOnScrollListener(touchListener.makeScrollListener());
		// ------------------------------------------------------------------------

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume(){
		//Portrait mode
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onResume();
		try {
			if (!org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");			
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
						System.exit(0);					
					}					
				});
				builder.show();
			}
		}
		catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");			
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
					System.exit(0);					
				}

			});
			builder.show();

		}


	}

	private void GetBeaconListFromParse() {

		ParseUser user =ParseUser.getCurrentUser();
		LayoutAboveAll.setVisibility(View.VISIBLE);
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Beacon");
		query.whereEqualTo("username", user.getUsername());
		query.addAscendingOrder("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> itemList, ParseException e) {
				SavedBeacons = new ArrayList<MyBeacon>();
				for(ParseObject item : itemList){
					if(item.getString("BeaconUUID")!=null){
						SavedBeacons.add(new MyBeacon(item.getString("BeaconUUID").toString(),item.getInt("BeaconMajor"),item.getInt("BeaconMinor"),item.getInt("BeaconProximity"),item.getString("BeaconAddress").toString()));}
				}
				mAdapter = new BeaconListAdapter(getApplicationContext(), SavedBeacons);
				// setting list adapter
				listView.setAdapter(mAdapter);
				LayoutAboveAll.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onBeaconServiceConnect() {
		BeaconManager.setRangeNotifier(new RangeNotifier() {

			@Override 
			public void didRangeBeaconsInRegion(Collection<Beacon> iBeacons, Region region) {
				Iterator<Beacon> iterator = iBeacons.iterator();
				NewBeacons = new ArrayList<MyBeacon>();
				while (iterator.hasNext()) {
					Beacon beacon = iterator.next(); 
					String BeaconUUID = beacon.getId1().toString();
					BeaconAddress = beacon.getBluetoothAddress();
					BeaconMajor = beacon.getId2().toInt();
					BeaconMinor = beacon.getId3().toInt();
					Double Proximity = beacon.getDistance();
					BeaconProximity = Proximity.intValue();
					//Log.d("BEACON", "type code "+beacon.getBeaconTypeCode()+"contents "+beacon.describeContents()+"distance "+beacon.getDistance()+"manff "+beacon.getManufacturer()+"rssi "+beacon.getRssi()+"describe contents "+beacon.describeContents());

					if (!BeaconUUID.equals("00000000-0000-0000-0000-000000000000")) {							
						NewBeacons.add(new MyBeacon(BeaconUUID, BeaconMajor, BeaconMinor, BeaconProximity, BeaconAddress));
					}
				}

			}

		});


		try {
			BeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
		} catch (RemoteException e) {   }

	}
	@Override 
	protected void onDestroy() {
		super.onDestroy();
		//ParseUser CURRENT_USER = ParseUser.getCurrentUser();
		//CURRENT_USER.put("BeaconName", name.getText().toString());
		//CURRENT_USER.saveInBackground();
		BeaconManager.unbind(this);
		Log.d("test", "finish");
	}
	@Override 
	protected void onPause() {
		super.onPause();
		BeaconManager.unbind(this);
		//if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, true);    		
	}
	private void logToDisplay(final String line) {
		final ParseUser CURRENT_USER = ParseUser.getCurrentUser();
		LayoutAboveAll.setVisibility(View.VISIBLE);
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("UUID", line);
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e==null & objects.size()==0){
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Beacon");
					query.whereEqualTo("username", CURRENT_USER.getUsername());
					//query.whereEqualTo("BeaconUUID", line);
					query.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> itemList, ParseException e) {
							if (e == null) {
								Log.d("check", itemList.toString());
								for(ParseObject Item : itemList){
									Log.d("check", itemList.toString());
									ParseUser CURRENT_USER = ParseUser.getCurrentUser();
									CURRENT_USER.put("UUID", line);
									CURRENT_USER.saveInBackground();
									Item.put("username", (ParseUser.getCurrentUser()).getUsername());
									Item.put("BeaconUUID", line);
									Item.put("BeaconMajor", BeaconMajor);
									Item.put("BeaconMinor", BeaconMinor);
									Item.put("BeaconAddress",BeaconAddress);
									Item.put("BeaconProximity", BeaconProximity);
									try {
										Item.save();
										GetBeaconListFromParse();
									} catch (ParseException e1) {
										e1.printStackTrace();
									}	 
								}
								if("[]".equals(itemList.toString()) && e==null){
									ParseUser CURRENT_USER = ParseUser.getCurrentUser();
									CURRENT_USER.put("UUID", line);
									CURRENT_USER.saveInBackground();
									ParseObject Item = new ParseObject("Beacon");
									Item.put("username", (ParseUser.getCurrentUser()).getUsername());
									Item.put("BeaconUUID", line);
									Item.put("BeaconMajor", BeaconMajor);
									Item.put("BeaconMinor", BeaconMinor);
									Item.put("BeaconAddress",BeaconAddress);
									Item.put("BeaconProximity", BeaconProximity);
									try {
										Item.save();
										GetBeaconListFromParse();
									} catch (ParseException edd) {
										edd.printStackTrace();
									}								
								}
							} else{
								LayoutAboveAll.setVisibility(View.GONE);
								Toast.makeText(BeaconSetupActivity.this, "Error Saving Beacon.", Toast.LENGTH_LONG).show();
							}
						}
					});
				}else{
					LayoutAboveAll.setVisibility(View.GONE);
					Toast.makeText(BeaconSetupActivity.this, "Already assigned. Use another beacon.", Toast.LENGTH_LONG).show();
				} 	 
			}
		});
	}
	public static int[] convertIntegers(List<Integer> integers)
	{
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	@SuppressLint("InflateParams")
	public class BeaconListAdapter extends BaseAdapter {

		private Context _context;
		ArrayList<MyBeacon> _beacon;

		public BeaconListAdapter(Context context,ArrayList<MyBeacon> beacon) {
			_context = context;
			_beacon =beacon;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public int getCount() {
			return _beacon.size();
		}

		@Override
		public Object getItem(int groupPosition) {
			return _beacon.get(groupPosition);
		}

		@Override
		public long getItemId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getView(final int groupPosition,View convertView, ViewGroup parent) {
			final String uuid = _beacon.get(groupPosition).getBeaconUUID();
			int major = _beacon.get(groupPosition).getBeaconMajor();
			int minor = _beacon.get(groupPosition).getBeaconMinor();
			int proximity = _beacon.get(groupPosition).getBeaconProximity();
			String address = _beacon.get(groupPosition).getAddress();

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.activity_beaconsetup, null);
			}
			TextView uuidtv = (TextView)convertView
					.findViewById(R.id.textView6);
			TextView prox = (TextView)convertView
					.findViewById(R.id.textView7);
			TextView maj = (TextView)convertView
					.findViewById(R.id.textView8);
			TextView min = (TextView)convertView
					.findViewById(R.id.textView9);
			TextView addr = (TextView)convertView
					.findViewById(R.id.textView10);

			//final Button but = (Button)convertView.findViewById(R.id.editText1);
			//ParseUser CURRENT_USER = ParseUser.getCurrentUser();
			/*			if(!uuid.equals(CURRENT_USER.get("UUID"))){
				but.setText("Beacon "+(groupPosition+1)+":	Click to set as Default Beacon");
				but.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("check", "Default set");
						but.setBackgroundColor(but.getContext().getResources().getColor(R.color.paletteColor2));
						but.setText("Beacon "+(groupPosition+1)+":	Default");
						ParseUser CURRENT_USER = ParseUser.getCurrentUser();
						CURRENT_USER.put("UUID", uuid);
						CURRENT_USER.saveInBackground();
						ParseQuery<ParseObject> query = ParseQuery.getQuery("Beacon");
				query.whereEqualTo("username", CURRENT_USER.getUsername());
				query.whereEqualTo("BeaconUUID", uuid);
				query.findInBackground(new FindCallback<ParseObject>() {
					public void done(List<ParseObject> itemList, ParseException e) {
						if (e == null) {
							Log.d("check", itemList.toString());
							for(ParseObject Item : itemList){
								Log.d("check", itemList.toString());
								Item.put("BeaconSelected", "yes");
								try {
									Item.save();
									} catch (ParseException e1) {
										e1.printStackTrace();
									}
							}
						}}});

					}
				});}
			else{
				but.setBackgroundColor(but.getContext().getResources().getColor(R.color.paletteColor2));
				but.setText("Beacon "+(groupPosition+1)+":	Default");
				BeaconSetupActivity.mAdapter.notifyDataSetChanged();
			}*/

			uuidtv.setText(uuid);
			if(proximity==0){
				prox.setText("Immediate");	
			}else if (proximity==1){
				prox.setText("Close");
			}else if (proximity==2){
				prox.setText("Far");
			}else if (proximity==3){
				prox.setText("Very Far");
			}else{
				prox.setText("Very very far");
			}

			maj.setText(""+major);
			min.setText(""+minor);
			addr.setText(address);

			return convertView;
		}
	}
}
