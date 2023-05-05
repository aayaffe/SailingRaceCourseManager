package com.aayaffe.sailingracecoursemanager.general;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.aayaffe.sailingracecoursemanager.R;
//import com.tenmiles.helpstack.HSHelpStack;
//import com.tenmiles.helpstack.gears.HSEmailGear;

public class HSApplication extends MultiDexApplication {
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

	}
}