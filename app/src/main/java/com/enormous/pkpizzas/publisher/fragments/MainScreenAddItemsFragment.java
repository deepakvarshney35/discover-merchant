package com.enormous.pkpizzas.publisher.fragments;

import android.support.v4.app.ListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.enormous.pkpizzas.publisher.activities.AddItemsActivity;
import com.enormous.pkpizzas.publisher.activities.OrdersActivity;
import com.enormous.pkpizzas.publisher.data.Item;
import com.enormous.pkpizzas.publisher.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainScreenAddItemsFragment extends ListFragment {
	private ImageView addOffer;
	ProgressBar progressBar;
	public ListAdapter listAdapter;

	public static ArrayList<Item> items;
	ListView list;
	//public String[] type = {"Offer","Coupon","Catalogue","Products","Brochure","Portfolio","Menu","Map","URL"};
	public String[] type = {"Offer","Document","Products","Map","URL"};
	String TAG = "tag.MainScreenAddItemsFragment";

	RelativeLayout itemScreen,OrdersRelativeLayout;
	LinearLayout noInternetLinearLayout;
	TextView noItemsPlaceholder;

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_offer_main_screen, container, false);

		//Find Views
		findviews(view);

		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		noItemsPlaceholder.setVisibility(View.GONE);

		noInternetLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshItemOnResume();
				refreshItems();
			}
		});

        OrdersRelativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToOrders = new Intent(getActivity(), OrdersActivity.class);
                goToOrders.putExtra("orderId","NA");
                startActivity(goToOrders);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out);
            }
        });

		addOffer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(items.size()>0){
					for (Item itm : items){
						if("Products".equals(itm.getType())){
							List<String> list = new ArrayList<String>(Arrays.asList(type));
							list.remove("Products");
							type = list.toArray(new String[0]);
						}
					}
				}
				View dialoglayout = inflater.inflate(R.layout.dialog_item_type_layout, null);
				AlertDialog.Builder alert=new AlertDialog.Builder(getActivity());
				alert.setView(dialoglayout);
				ListView list = (ListView) dialoglayout.findViewById(R.id.typeList);
				list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, type));
				list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int which, long arg3) {
						ParseUser user = ParseUser.getCurrentUser();
						String selectedType = type[which];
						if(user!=null){
							Intent i = new Intent(getActivity().getApplicationContext(), AddItemsActivity.class);
							// sending data to new activity
							i.putExtra("OFFERTYPE", selectedType);
							i.putExtra("itemObjectId", "00000");
							startActivity(i);
						}
					}
				});
				/*alert.setTitle("Select type of Item to be added...");
				alert.setItems(type, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String selectedType = type[which];
						ParseUser user = ParseUser.getCurrentUser();
						if(user!=null){
							Intent i = new Intent(getActivity().getApplicationContext(), ItemsActivity.class);
							// sending data to new activity
							i.putExtra("OFFERTYPE", selectedType);
							i.putExtra("itemObjectId", "00000");
							startActivity(i);
						}
					}
				});*/
				alert.show();
			}
		}
				);
		refreshItems();
		return view;
	}

	public void refreshItems(){
		ParseUser user = ParseUser.getCurrentUser();
		if (user != null) {
			progressBar.setVisibility(View.VISIBLE);
			addOffer.setVisibility(View.GONE);
			list.setVisibility(View.GONE);
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
			query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
			query.addAscendingOrder("createdAt");
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> itemList, ParseException e) {
					if (e == null) {
						progressBar.setVisibility(View.GONE);
						addOffer.setVisibility(View.VISIBLE);
						list.setVisibility(View.VISIBLE);
						noInternetLinearLayout.setVisibility(View.GONE);
						itemScreen.setVisibility(View.VISIBLE);
						if (itemList.size()==0) {
							noItemsPlaceholder.setVisibility(View.VISIBLE);
						}else{
							noItemsPlaceholder.setVisibility(View.GONE);
						}
					} else {
						progressBar.setVisibility(View.GONE);
						list.setVisibility(View.GONE);
						addOffer.setVisibility(View.GONE);
						noInternetLinearLayout.setVisibility(View.VISIBLE);
						itemScreen.setVisibility(View.GONE);
						Log.e(TAG, "Retreiving Items Error: " + e.getMessage());
					}
				}
			});
		}
	}

	public void refreshItemOnResume(){
		ParseUser user = ParseUser.getCurrentUser();
		if (user != null) {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
			query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
			query.addAscendingOrder("createdAt");
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> itemList, ParseException e) {
					if (e == null) {
						items = new ArrayList<Item>();
						for(ParseObject item : itemList){
							items.add(new Item(item.getString("itemName"), item.getString("itemType"),item.getInt("itemColorid"), item.getString("itemDescription"), item.getParseFile("itemDocument"),item.getObjectId(),item.getString("itemMap"),item.getString("itemURL")));
						}
						if(getActivity()!=null){
							listAdapter = new ListAdapter(getActivity(), items);
							setListAdapter(listAdapter);
							listAdapter.notifyDataSetChanged();
							list.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> arg0, View vi, int CurrentSelectedItem,long arg3) {
									// Launching new Activity on selecting single List Item
									Intent i = new Intent(getActivity().getApplicationContext(), AddItemsActivity.class);
									// sending data to new activity
									i.putExtra("itemObjectId", items.get(CurrentSelectedItem).getObjectId());
									i.putExtra("OFFERTYPE", items.get(CurrentSelectedItem).getType());
									i.putExtra("position", CurrentSelectedItem);
									startActivity(i);
								}
							});}
					} else {
						Log.e(TAG, "Retreiving Items Error: " + e.getMessage());
					}
				}
			});
		}
	}

	private void findviews(View view) {
		addOffer = (ImageView)view. findViewById(R.id.button1);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		list = (ListView)view.findViewById(android.R.id.list);
		noInternetLinearLayout = (LinearLayout) view.findViewById(R.id.noInternetLinearLayout);
		itemScreen = (RelativeLayout)view.findViewById(R.id.diss);
		noItemsPlaceholder = (TextView)view.findViewById(R.id.noItemsPlaceholder);
        OrdersRelativeLayout = (RelativeLayout) view.findViewById(R.id.OrdersRelativeLayout);
	}
	@Override
	public void onResume() {
		super.onResume();
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		refreshItemOnResume();
	}

	public class ListAdapter extends ArrayAdapter<Item> {

		private Context _context;
		private ArrayList<Item> _items;

		public ListAdapter(Context context, ArrayList<Item> items) {
			super(context, R.layout.list_group, items);
			_context = context;
			_items = items;
		}
		class ViewHolder {
			ImageView delete_confirm;

			public ViewHolder(View view) {
				delete_confirm = (ImageView) view.findViewById(R.id.btnDeleteConfirmImage);
			}
		}
		@SuppressLint("InflateParams")
		@Override
		public View getView(int groupPosition,View convertView, ViewGroup parent) {
			String headerTitle = _items.get(groupPosition).getName();
			//String headerTitle = (String) getItem(groupPosition);
			final ViewHolder holder;
			final Integer childDraw = _items.get(groupPosition).getColorId();
			String ItemTypeTitle = _items.get(groupPosition).getType();

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_group, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			Bitmap bitmaps;
			ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1); 

			holder.delete_confirm.setTag(_items.get(groupPosition));
			holder.delete_confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 
					final PopupMenu popup = new PopupMenu(_context, holder.delete_confirm);
					popup.getMenuInflater().inflate(R.menu.graph_popup_menu, popup.getMenu());

					popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

						@Override
						public boolean onMenuItemClick(MenuItem item) {
							// 
							switch (item.getItemId()) {
							case R.id.delete:
								_items.remove(holder.delete_confirm.getTag());
								notifyDataSetChanged();
								Item itm = (Item)holder.delete_confirm.getTag();
								ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
								query.whereEqualTo("brandObjectId", ParseUser.getCurrentUser().getObjectId());
								query.whereEqualTo("objectId", itm.getObjectId());
								query.findInBackground(new FindCallback<ParseObject>() {

									@Override
									public void done(List<ParseObject> itemList, ParseException e) {
										if(e==null){
											for(ParseObject item : itemList){
												item.deleteInBackground();
											}
										}
									}
								});
								break;

							}
							return true;
						}
					});
					popup.show();
				}

			});

			if(ItemTypeTitle.equals("Offer"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.coupon_w, 0, 0, 0);
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.offer);
				iv.setImageBitmap(bitmaps);}
			else if(ItemTypeTitle.equals("Brochure"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.brochure_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.portfolio_2);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Portfolio"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.portfolio_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.portfolio_2);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Catalogue"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.portfolio_2);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Document"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.portfolio_2);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Menu"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.portfolio_2);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Map"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.map_2);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("Products"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.products);
				iv.setImageBitmap(bitmaps);
			}
			else if(ItemTypeTitle.equals("URL"))
			{//iv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.catalogue_w, 0, 0, 0);}
				bitmaps =  BitmapFactory.decodeResource(convertView.getResources(), R.drawable.url);
				iv.setImageBitmap(bitmaps);
			}
			TextView lblListHeader = (TextView) convertView
					.findViewById(R.id.lblListHeader);
			RelativeLayout ChangeColor = (RelativeLayout) convertView
					.findViewById(R.id.ChangeColor);
			//ChangeColor.setBackgroundColor(childDraw);
			lblListHeader.setTypeface(null, Typeface.BOLD);
			lblListHeader.setText(headerTitle);

			return convertView;
		}
	}
}


