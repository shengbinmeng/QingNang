package pku.shengbin.qingnang;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import pku.shengbin.qingnang.QNReminderManager.QNReminder;
import pku.shengbin.qingnang.QNUserManager.QNUser;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ReminderActivity extends ListActivity {
	private QNUser mUser;
	
	private ImageView mPreviousIcon;
	private ImageView mNextIcon;
	private Calendar mCalendar;
	private TextView mDateText;
	private TextView mCurrentTimeText;
	private LinearLayout mTimeLine;
	
	private ListView mListView;
	private ReminderAdapter mListAdapter;
	private ArrayList<QNReminder> 	mReminders;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reminder);
        
        String userID = this.getIntent().getStringExtra("pku.shengbin.qingnang.userID");
        mUser = QNUserManager.getUserById(userID);

		TextView titleText = (TextView) findViewById(R.id.title);
		titleText.setText(mUser.name + "的提醒");
		
		mCalendar = Calendar.getInstance();
		mCurrentTimeText = (TextView) findViewById(R.id.current_time);
		mTimeLine = (LinearLayout) this.findViewById(R.id.time_line);
		TextView tv = (TextView) this.findViewById(R.id.time_pos);
    	tv.setText("");
    	tv.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
    	tv.setTextSize(10);
    	tv.setTextScaleX(4);
    	LayoutParams params = tv.getLayoutParams();
    	int posNum = 48;
		for (int i = 0; i < posNum - 1; i++) {
    		tv = new TextView(this);
        	tv.setText("");
        	tv.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        	tv.setTextSize(10);
        	tv.setTextScaleX(4);
        	mTimeLine.addView(tv, params);   
    	}
		
		initDateChangeIcons();
		initReminderList();
		updateContent();
    } 
    
    private void initDateChangeIcons() {
    	mDateText = (TextView) findViewById(R.id.date);
    	mPreviousIcon = (ImageView) findViewById(R.id.previous);
    	mNextIcon = (ImageView) findViewById(R.id.next);
    	OnClickListener listener = new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == mPreviousIcon) {
					mCalendar.add(Calendar.DATE, -1);
				} else if (v == mNextIcon) {
					mCalendar.add(Calendar.DATE, 1);
				}
				updateContent();
			}
    	};
		mPreviousIcon.setOnClickListener(listener);
		mNextIcon.setOnClickListener(listener);
    }
    
    private void initReminderList() {
    	mReminders = mUser.reminders;
    	mListView = (ListView) this.findViewById(android.R.id.list); 
		mListAdapter = new ReminderAdapter(this, mReminders, mUser);
		mListView.setAdapter(mListAdapter);
    }
    
    public void updateContent() {
		mListAdapter.updateContentToDate(mCalendar);
    	Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		mCurrentTimeText.setText("当前时间 " + sdf.format(c.getTime()));
		mDateText.setText((mCalendar.get(Calendar.MONTH)+1) + "月" + mCalendar.get(Calendar.DATE) + "日");
		int minutesPast = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
		//double p = minutesPast / (24.0 * 60.0);
		double p = (c.get(Calendar.HOUR_OF_DAY) * 2 + (int)(c.get(Calendar.MINUTE) / 30)) / 48.0;
		int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
		int paddingLeft = (int) (p * screenWidth);
		if (paddingLeft > screenWidth - 20) paddingLeft = screenWidth - 20;
		this.findViewById(R.id.current_time_arrow).setPadding(paddingLeft, 0, 0, 0);
		paddingLeft -= 50;
		if (paddingLeft < 0) paddingLeft = 0;
		if (paddingLeft > screenWidth - 150) paddingLeft = screenWidth - 150; // make sure the text fully shows
		this.findViewById(R.id.current_time).setPadding(paddingLeft, 0, 0, 0);
		
		updateTimeLine();
    }
    
    private void updateTimeLine() {
    	for (int i = 0; i < mTimeLine.getChildCount(); i++) {
    		TextView tv = (TextView) mTimeLine.getChildAt(i);
    		tv.setText("");
    	}
    	for (int i = 0; i < mTimeLine.getChildCount(); i++) {
    		final ArrayList<String> medicines = mListAdapter.getTodayMedicinesNearTime(i);
    		if (medicines.size() > 0) {
    			TextView tv = (TextView) mTimeLine.getChildAt(i);
    			tv.setText("|");
    			tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						//TextView tv = (TextView) v;
						//tv.setTextColor(Color.RED);
						String content = "";
						for (int k = 0; k < medicines.size(); k++) {
							 content += (medicines.get(k) + "\n");
						}
						MessageBox.show(ReminderActivity.this, "该时间点附近的药品", content);
					}
				});
    		}
    	}
    }
    	
    
    @Override
	protected void onResume() {
		updateContent();
		super.onResume();
	}
}