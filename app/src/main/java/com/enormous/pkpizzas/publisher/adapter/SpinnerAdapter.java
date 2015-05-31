package com.enormous.pkpizzas.publisher.adapter;

import java.util.ArrayList;

import com.enormous.pkpizzas.publisher.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpinnerAdapter extends ArrayAdapter<SpinnerInfo> {

    private Activity context;
    ArrayList<SpinnerInfo> data = null;

    public SpinnerAdapter(Activity context, int resource, ArrayList<SpinnerInfo> data)
    {
        super(context, resource, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {   // Ordinary view in Spinner, we use android.R.layout.simple_spinner_item
        return super.getView(position, convertView, parent);   
    }


	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {   // This view starts when we click the spinner.
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_custom_list, parent, false);
        }

        SpinnerInfo item = data.get(position);

        if(item != null)
        {   // Parse the data from each object and set it.
            ImageView myItemLogo = (ImageView) row.findViewById(R.id.imageViewSpinner);
            TextView myItem = (TextView) row.findViewById(R.id.Header);
            if(myItemLogo != null)
            {
            	Bitmap bitmaps =  BitmapFactory.decodeResource(row.getResources(), item.getItemLogo());
            	myItemLogo.setImageBitmap(bitmaps);
               //myFlag.setBackgroundDrawable(row.getResources().getDrawable(item.getItemLogo()));
            }
            if(myItem  != null)
            	myItem.setText(item.getItemName());

        }

        return row;
    }

	public int getPosition(String b3) {
		// TODO Auto-generated method stub
		 int pos=0;
			if("Offer".equals(b3))
			{pos=0;}
			if("Brochure".equals(b3))
			{pos=1;}
			if("Portfolio".equals(b3))
			{pos=2;}
			if("Catalogue".equals(b3))
			{pos=3;}
			if("Menu".equals(b3))
			{pos=4;}
			if("Map".equals(b3))
			{pos=5;}
			if("Products".equals(b3))
			{pos=6;}
			if("URL".equals(b3))
			{pos=7;}
		return pos;
	}
}
