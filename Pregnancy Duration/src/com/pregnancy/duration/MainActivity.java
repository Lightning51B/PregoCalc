package com.pregnancy.duration;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

	private SharedPreferences prefs;
	static String prefName = "pregnancyDuration";
	static String dueDatePerf = "dueDatePerf";

	private TextView dueDate;
	private TextView daysLeft;
	private TextView progress;
	private TextView trimester;
	private TextView nextTrimester;
	private DateTime todayCal = new DateTime();
	private Integer totalDays;
	private Integer weeks;
	private Integer daysInWeek;
	private boolean toggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = getSharedPreferences(prefName, MODE_PRIVATE);

		Long storedDate = prefs.getLong(dueDatePerf, MODE_PRIVATE);
		if (storedDate.intValue() == 0) {
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
		} else {
			DateTime dueDateCal = new DateTime(storedDate);
			update(dueDateCal);
		}
	}

	/**
	 * handle onclick events
	 */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.daysLeft:
			if (toggle) {
				daysLeft.setText(totalDays + " Days");
				daysLeft.setTextSize(60);
			} else {
				daysLeft.setText(weeks + " Weeks " + daysInWeek + " Days");
				daysLeft.setTextSize(30);
			}
			toggle = !toggle;
			break;
		case R.id.dueDate:
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
		default:
			break;
		}
	}

	public void saveDate(DateTime dueDateCal) {
		prefs = getSharedPreferences(prefName, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(dueDatePerf, dueDateCal.getMillis());
		if (editor.commit()) {
			Toast.makeText(getBaseContext(), "Due date saved successfully",
					Toast.LENGTH_SHORT).show();
		}
		update(dueDateCal);
	}

	/**
	 * Update the current view
	 * 
	 * @param dueDateCal
	 */
	public void update(DateTime dueDateCal) {

		// Dates
		totalDays = Days.daysBetween(todayCal.toDateMidnight(),
				dueDateCal.toDateMidnight()).getDays();
		weeks = totalDays / 7;
		daysInWeek = totalDays % 7;
		Integer progressWeek = weeks > 0 ? 40 - weeks : 40;
		Integer progressDays = 7 - daysInWeek;

		// Calculate trimester
		String trimesterStr;
		if (progressWeek < 14)
			trimesterStr = "1st";
		else if (progressWeek < 28)
			trimesterStr = "2nd";
		else
			trimesterStr = "3rd";

		String nextTrimesterStr;
		if (trimesterStr.equals("1st"))
			nextTrimesterStr = 14 - progressWeek + " Weeks";
		else if (trimesterStr.equals("2nd"))
			nextTrimesterStr = 28 - progressWeek + " Weeks";
		else
			nextTrimesterStr = "Last Trimester";

		// Set values to text views
		TextView babyName = (TextView) findViewById(R.id.babyName);
		babyName.setText("Your Baby is coming in");

		daysLeft = (TextView) findViewById(R.id.daysLeft);
		daysLeft.setText(totalDays + " Days");
		daysLeft.setOnClickListener(this);

		dueDate = (TextView) findViewById(R.id.dueDate);
		dueDate.setText(dueDateCal.toString("MM/dd/yyyy"));
		dueDate.setOnClickListener(this);

		progress = (TextView) findViewById(R.id.progress);
		progress.setText(progressWeek + " Weeks " + progressDays + "Days");

		trimester = (TextView) findViewById(R.id.trimester);
		trimester.setText(trimesterStr);

		nextTrimester = (TextView) findViewById(R.id.nextTrimester);
		nextTrimester.setText(nextTrimesterStr);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
