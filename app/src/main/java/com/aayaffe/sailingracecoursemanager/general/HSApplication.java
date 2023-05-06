package com.aayaffe.sailingracecoursemanager.general;


import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.aayaffe.sailingracecoursemanager.R;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
//import com.tenmiles.helpstack.HSHelpStack;
//import com.tenmiles.helpstack.gears.HSEmailGear;

public class HSApplication extends MultiDexApplication implements OnMapsSdkInitializedCallback {
//	HSHelpStack helpStack;

	private static HSApplication instance;

	public static HSApplication getInstance() {
		return instance;
	}
	public static Context getContext() {
		return instance.getApplicationContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();

//		helpStack = HSHelpStack.getInstance(this); // Get the HSHelpStack instance
//
//		HSEmailGear emailGear = new HSEmailGear(
//				"support@avimarine.in",
//				R.xml.articles);
//
//		helpStack.setGear(emailGear); // Set the Gear
		instance = this;
		MultiDex.install(this);
		MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, this);

	}
	@Override
	public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
		switch (renderer) {
			case LATEST:
				Log.d("HSAApplication", "The latest version of the renderer is used.");
				break;
			case LEGACY:
				Log.d("HSAApplication", "The legacy version of the renderer is used.");
				break;
		}
	}
}