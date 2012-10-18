package pku.shengbin.qingnang;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pku.shengbin.qingnang.QNMedicineManager.QNMedicine;
import pku.shengbin.qingnang.QNReminderManager.QNReminder;
import pku.shengbin.qingnang.QNUserManager.QNUser;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ReminderAlarmActivity extends Activity {
	private QNUser mUser;
	private QNMedicine mMedicine;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*
		LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.reminder_alarm,null);
        PopupWindow popupWindow = new PopupWindow(view, 300, 600);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        */
        setContentView(R.layout.reminder_alarm);

        Intent intent = this.getIntent();
        String userId = intent.getStringExtra("pku.shengbin.qingnang.userName");
        mUser = QNUserManager.getUserById(userId);
		String medicineId = intent.getStringExtra("pku.shengbin.qingnang.medicineID");
		mMedicine = QNMedicineManager.getMedicineByID(medicineId);
		
        TextView textView;
        String text;
		long alarmTime = intent.getLongExtra("pku.shengbin.qingnang.alarmTime", 0);
		Date date = new Date(alarmTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
		text = "当前时间：" + sdf.format(date);
        textView = (TextView) findViewById(R.id.alarm_time);
        textView.setText(text);
        
		text = "应服药品：" + mMedicine.name + " , 数量：" + intent.getIntExtra("pku.shengbin.qingnang.usageAmount", 0);
        textView = (TextView) findViewById(R.id.medicine_and_usage);
        textView.setText(text);
	
        textView = (TextView) findViewById(R.id.extra_reminder);
        text = mMedicine.attributes.get("dosage");

        OnClickListener l = new View.OnClickListener() {
			public void onClick(View v) {
				ReminderAlarmActivity.this.finish();
			}
		};
        Button button = (Button) findViewById(R.id.use);
        button.setOnClickListener(l);
        button = (Button) findViewById(R.id.skip);
        button.setOnClickListener(l);
        button = (Button) findViewById(R.id.configure);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*
				Intent i = new Intent(ReminderAlarmActivity.this, pku.shengbin.qingnang.ReminderEditActivity.class);
		    	i.putExtra("pku.shengbin.qingnang.medicineID", mMedicine.id);
		    	i.putExtra("pku.shengbin.qingnang.userID", mUser.id);
		    	ReminderAlarmActivity.this.startActivity(i);
		    	*/
				Intent i = new Intent(ReminderAlarmActivity.this, pku.shengbin.qingnang.ReminderActivity.class);
				i.putExtra("pku.shengbin.qingnang.userID", mUser.id);
				QNDataDriver.setUid("123456/" + mUser.id);
		    	ReminderAlarmActivity.this.startActivity(i);
			}
		});
        
        button = (Button) findViewById(R.id.five_minutes);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ReminderAlarmActivity.this.setAlarmAgainAfter(1);
				Toast.makeText(ReminderAlarmActivity.this, "将于五分钟后再次提醒", Toast.LENGTH_SHORT).show();
				ReminderAlarmActivity.this.finish();
			}
		});
        button = (Button) findViewById(R.id.ten_minutes);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ReminderAlarmActivity.this.setAlarmAgainAfter(10);
				Toast.makeText(ReminderAlarmActivity.this, "将于十分钟后再次提醒", Toast.LENGTH_SHORT).show();
				ReminderAlarmActivity.this.finish();
			}
		});
        button = (Button) findViewById(R.id.fiften_minutes);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ReminderAlarmActivity.this.setAlarmAgainAfter(15);
				Toast.makeText(ReminderAlarmActivity.this, "将于十五分钟后再次提醒", Toast.LENGTH_SHORT).show();
				ReminderAlarmActivity.this.finish();
			}
		});
    }
    
    private void setAlarmAgainAfter(int delayMinutes) {
    	Context ctx = this.getApplicationContext();
    	AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
    	Bundle bundle = this.getIntent().getExtras();
    	long alarmTime = bundle.getLong("pku.shengbin.qingnang.alarmTime");
    	alarmTime += delayMinutes*60*1000;
    	bundle.putLong("pku.shengbin.qingnang.alarmTime", alarmTime);
        Intent newIntent = new Intent(ctx, QNReminderManager.class);
    	newIntent.putExtras(bundle);
    	PendingIntent sender = PendingIntent.getBroadcast(ctx, newIntent.filterHashCode(), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	am.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
    }
    
}