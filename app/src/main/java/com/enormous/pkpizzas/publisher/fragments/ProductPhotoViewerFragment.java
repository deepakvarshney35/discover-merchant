package com.enormous.pkpizzas.publisher.fragments;

import com.enormous.pkpizzas.publisher.R;
import com.enormous.pkpizzas.publisher.adapter.TouchImageView;
import com.enormous.pkpizzas.publisher.data.ImageLoader;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Saurabh on 7/10/2014.
 */
public class ProductPhotoViewerFragment extends Fragment {

	private TouchImageView productImageView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_product_photo_viewer, container, false);
		productImageView = (TouchImageView) view.findViewById(R.id.productImageView);

		//get image url from arguments and display image
		String imageUrl = getArguments().getString("imageUrl");
		ImageLoader.getInstance().displayImage(getActivity(), imageUrl, productImageView, false, 640, 640, 0);
		return view;
	}

	public static ProductPhotoViewerFragment newInstance(String url) {
		ProductPhotoViewerFragment fragment = new ProductPhotoViewerFragment();
		Bundle bundle = new Bundle();
		bundle.putString("imageUrl", url);
		fragment.setArguments(bundle);
		return fragment;
	}
}