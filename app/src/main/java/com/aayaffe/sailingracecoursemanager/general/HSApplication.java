package com.aayaffe.sailingracecoursemanager.general;

import android.app.Application;

import com.aayaffe.sailingracecoursemanager.R;
import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.gears.HSEmailGear;

public class HSApplication extends Application {
	HSHelpStack helpStack;

	@Override
	public void onCreate() {
		super.onCreate();

		helpStack = HSHelpStack.getInstance(this); // Get the HSHelpStack instance

		HSEmailGear emailGear = new HSEmailGear(
				"support@avimarine.in",
				R.xml.articles);

		helpStack.setGear(emailGear); // Set the Gear
	}
}