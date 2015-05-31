package com.enormous.pkpizzas.publisher;

import java.io.File;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;

import android.app.Application;

public class DiscoverApplication extends Application {
    
	public static int DISPLAY_DPI;
    public static File EXTERNAL_CACHE_DIR;

	@Override
	public void onCreate() {
		super.onCreate();
		
		//get app's external cache directory
        EXTERNAL_CACHE_DIR = getExternalCacheDir();

		//get display dpi
        DISPLAY_DPI = getResources().getDisplayMetrics().densityDpi;
        
        //Parse crash reports
        ParseCrashReporting.enable(this);
        
		//initialize Parse
		Parse.initialize(this, "Xq2EuNKKIiRAkTiJ2mJjrzCnHNQzdUDo89ocdFyx", "95HyKnMwzFcXtNSNU6yjNeippbgmWML0AejfnifX");
		
		// Set your Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(getString(R.string.app_id));
	}

}
