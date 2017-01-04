package com.aayaffe.sailingracecoursemanager;

import android.app.Application;

import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.gears.HSEmailGear;

public class HSApplication extends Application {
	HSHelpStack helpStack;

	@Override
	public void onCreate() {
		super.onCreate();

		helpStack = HSHelpStack.getInstance(this); // Get the HSHelpStack instance

		HSEmailGear emailGear = new HSEmailGear(
				"avimarineinnovations@gmail.com",
				R.xml.articles);

		helpStack.setGear(emailGear); // Set the Gear
	}
}