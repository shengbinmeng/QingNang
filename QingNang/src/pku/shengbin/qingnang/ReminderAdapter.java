package pku.shengbin.qingnang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pku.shengbin.qingnang.QNMedicineManager.QNMedicine;
import pku.shengbin.qingnang.QNReminderManager.QNCustomReminder;
import pku.shengbin.qingnang.QNReminderManager.QNReminder;
import pku.shengbin.qingnang.QNUserManager.QNUser;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ReminderAdapter extends BaseAdapter {

	private ReminderActivity mActivity;
	private ArrayList<QNReminder> mReminders;
	private QNUser mUser;
	private LayoutInflater mInflater;
	private int[] mTableHeaderPositions = new int[6];
	ArrayList<QNReminder> todayReminders = new ArrayList<QNReminder>();
	private ArrayList<QNReminder> onceReminders = new ArrayList<QNReminder>(), twiceReminders = new ArrayList<QNReminder>(), threeReminders = new ArrayList<QNReminder>();
	private ArrayList<QNReminder> fourReminders = new ArrayList<QNReminder>(), mealReminders = new ArrayList<QNReminder>();
	private ArrayList<QNCustomReminder> customReminders = new ArrayList<QNCustomReminder>();
	private int mLastPosition;

	public ReminderAdapter(Context context, ArrayList<QNReminder> reminders, QNUser user) {
		mReminders = reminders;
		mUser = user;
        mInflater = LayoutInflater.from(context);
        mActivity = (ReminderActivity) context;
	}
	
	public void updateContentToDate(Calendar current) {
		todayReminders.clear();
		onceReminders.clear();
		twiceReminders.clear();
		threeReminders.clear();
		fourReminders.clear();
		mealReminders.clear();
		customReminders.clear();
		
		QNReminder reminder;
    	for (int i = 0; i < mReminders.size(); i++) {
    		reminder = mReminders.get(i);
    		if (reminder.needAlarmAtDate(current)) {
    			todayReminders.add(reminder);
    		}
    	}
    	
		for (int i = 0; i < todayReminders.size(); i++) {
			QNReminder r = todayReminders.get(i);
			switch (r.type) {
			case QNReminder.ONCE_PER_DAY:
				onceReminders.add(r);
				break;
			case QNReminder.TWICE_PER_DAY:
				twiceReminders.add(r);
				break;
			case QNReminder.THREE_TIMES_PER_DAY:
				threeReminders.add(r);
				break;
			case QNReminder.FOUR_TIMES_PER_DAY:
				fourReminders.add(r);
				break;
			case QNReminder.BEFOR_AND_AFTER_MEAL:
				mealReminders.add(r);
				break;
			case QNReminder.CUSTOMED_TIME: 
				{
					Calendar cal = Calendar.getInstance();
					for (int j = 0; j < r.customReminders.size(); j++) {
						QNCustomReminder custom = r.customReminders.get(j);
						cal.setTime(custom.date);
						if (isAtSameDay(cal, current)) {
							customReminders.add(custom);
						}
					}
				}
				break;
			}
		}
		
		int lastPosition = -1;
		if (onceReminders.size() != 0) {
			lastPosition += 1;
			mTableHeaderPositions[0] = lastPosition;
			lastPosition += onceReminders.size();
		} else {
			mTableHeaderPositions[0] = -1;
		}
		
		if (twiceReminders.size() != 0) {
			lastPosition += 1;
			mTableHeaderPositions[1] = lastPosition;
			lastPosition += twiceReminders.size();
		} else {
			mTableHeaderPositions[1] = -1;
		}
		if (threeReminders.size() != 0) {
			lastPosition += 1;
			mTableHeaderPositions[2] = lastPosition;
			lastPosition += threeReminders.size();
		} else {
			mTableHeaderPositions[2] = -1;
		}
		if (fourReminders.size() != 0) {
			lastPosition += 1;
			mTableHeaderPositions[3] = lastPosition;
			lastPosition += fourReminders.size();
		} else {
			mTableHeaderPositions[3] = -1;
		}
		if (mealReminders.size() != 0) {
			lastPosition += 1;
			mTableHeaderPositions[4] = lastPosition;
			lastPosition += mealReminders.size();
		} else {
			mTableHeaderPositions[4] = -1;
		}
		
		mLastPosition = lastPosition;
    	
    	this.notifyDataSetChanged();
	}
	
	public ArrayList<String> getTodayMedicinesNearTime(double index) {
		// index: 0, 1, ..., 47
		ArrayList<String> result = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date date;
		double d;
		for (int i = 0; i < todayReminders.size(); i++) {
			QNReminder r = todayReminders.get(i);
			try {
				switch (r.type) {
				case QNReminder.ONCE_PER_DAY:
					date = sdf.parse(mUser.onceTime[0]);
					d = date.getHours() + date.getMinutes() / 60.0;
					if ((int)(d / 0.5) == index) {
						result.add(r.medicineName + "(" + mUser.onceTime[0] + ")");
					}
					break;
				case QNReminder.TWICE_PER_DAY:
					for (int j = 0; j < mUser.twiceTimes.length; j++) {
						date = sdf.parse(mUser.twiceTimes[j]);
						d = date.getHours() + date.getMinutes() / 60.0;
						if ((int)(d / 0.5) == index) {
							result.add(r.medicineName + "(" + mUser.twiceTimes[j] + ")");
						}
					}
					break;
				case QNReminder.THREE_TIMES_PER_DAY:
					for (int j = 0; j < mUser.threeTimes.length; j++) {
						date = sdf.parse(mUser.threeTimes[j]);
						d = date.getHours() + date.getMinutes() / 60.0;
						if ((int)(d / 0.5) == index) {
							result.add(r.medicineName + "(" + mUser.threeTimes[j] + ")");
						}
					}				
					break;
				case QNReminder.FOUR_TIMES_PER_DAY:
					for (int j = 0; j < mUser.fourTimes.length; j++) {
						date = sdf.parse(mUser.fourTimes[j]);
						d = date.getHours() + date.getMinutes() / 60.0;
						if ((int)(d / 0.5) == index) {
							result.add(r.medicineName + "(" + mUser.fourTimes[j] + ")");
						}
					}				
					break;
				case QNReminder.BEFOR_AND_AFTER_MEAL:
					for (int j = 0; j < mUser.nearMealTimes.length; j++) {
						date = sdf.parse(mUser.nearMealTimes[j]);
						d = date.getHours() + date.getMinutes() / 60.0;
						if ((int)(d / 0.5) == index) {
							result.add(r.medicineName + "(" + mUser.nearMealTimes[j] + ")");
						}
					}
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
		
		for (int i = 0; i < this.customReminders.size(); i++) {
			QNCustomReminder r = this.customReminders.get(i);
			d = r.date.getHours() + r.date.getMinutes() / 60.0;
			if ((int)(d / 0.5) == index) {
				result.add(r.medicineName + "(" + sdf.format(r.date) + ")");
			}
		}
		
		return result;
	}
	
	
	public int getCount() {
		return mLastPosition + 1 + customReminders.size() * 2;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		int[] result = checkPosition(position);
		int header = result[0], section = result[1], row = result[2];
		if (header >= 0) {
			HeaderRowViewHolder headerRow;
			if (convertView == null || convertView.findViewById(R.id.usage_type_name) == null) {
				// no reusable header view, so inflate one
	            convertView = mInflater.inflate(R.layout.reminder_section_header, null);
				headerRow = new HeaderRowViewHolder();
				headerRow.usageTypeName = (TextView) convertView.findViewById(R.id.usage_type_name);
				headerRow.usageTypeTime = (TextView) convertView.findViewById(R.id.usage_type_time);
				headerRow.timeSettingIcon = (ImageView) convertView.findViewById(R.id.setting_icon);
				convertView.setTag(headerRow);
			} else {
				headerRow = (HeaderRowViewHolder) convertView.getTag();
			}
			
			switch (header) {
			case 0:
				headerRow.usageTypeName.setText("每日一次");
				headerRow.usageTypeTime.setText(mUser.onceTime[0]);
				headerRow.timeSettingIcon.setTag(0);
				break;
			case 1:
				headerRow.usageTypeName.setText("每日两次");
				headerRow.usageTypeTime.setText(mUser.twiceTimes[0] + " " + mUser.twiceTimes[1]);
				headerRow.timeSettingIcon.setTag(1);
				break;
			case 2:
				headerRow.usageTypeName.setText("每日三次");
				headerRow.usageTypeTime.setText(mUser.threeTimes[0] + " " + mUser.threeTimes[1] + " " + mUser.threeTimes[2]);
				headerRow.timeSettingIcon.setTag(2);
				break;
			case 3:
				headerRow.usageTypeName.setText("每日四次");
				headerRow.usageTypeTime.setText(mUser.fourTimes[0] + " " + mUser.fourTimes[1] + " " + mUser.fourTimes[2] + " " + mUser.fourTimes[3]);
				headerRow.timeSettingIcon.setTag(3);
				break;
			case 4:
				headerRow.usageTypeName.setText("饭前饭后");
				headerRow.usageTypeTime.setText(mUser.nearMealTimes[0] + " " + mUser.nearMealTimes[1] + " " + mUser.nearMealTimes[2]);
				headerRow.timeSettingIcon.setTag(4);
				break;
			case 5:
				headerRow.usageTypeName.setText("自设提醒");
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				headerRow.usageTypeTime.setText(sdf.format(customReminders.get(row).date));
				headerRow.timeSettingIcon.setTag(5 + row);
				break;
			}
			
			headerRow.timeSettingIcon.setOnClickListener(new TimeSettingIconClickListener());	
			
		} else {
			ReminderRowViewHolder reminderRow;
	        if (convertView == null || convertView.findViewById(R.id.usage_type_name) != null) {
	            convertView = mInflater.inflate(R.layout.reminder_row, null);
	            reminderRow = new ReminderRowViewHolder();
	            reminderRow.reminderMedicine = (TextView) convertView.findViewById(R.id.reminder_medicine);
	            reminderRow.reminderDeleteIcon = (ImageView) convertView.findViewById(R.id.reminder_delete_icon);
	            convertView.setTag(reminderRow);
	        } else {
	            reminderRow = (ReminderRowViewHolder) convertView.getTag();
	        }
	        
	        QNReminder reminder = null;
	        switch (section) {
	        // for different reminder types
	        case 0:
	        	reminder = onceReminders.get(row);
	        	break;
	        case 1:
	        	reminder = twiceReminders.get(row);
	        	break;
	        case 2:
	        	reminder = threeReminders.get(row);
	        	break;
	        case 3:
	        	reminder = fourReminders.get(row);
	        	break;
	        case 4:
	        	reminder = mealReminders.get(row);
	        	break;
	        case 5:
	        	// deals below
	        	break;
	        }
	        
	        if (reminder != null) {
	        	final QNReminder r = reminder;
		        reminderRow.reminderMedicine.setText(reminder.medicineName);
		        reminderRow.reminderMedicine.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(mActivity, ReminderEditActivity.class);
				    	i.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
				    	i.putExtra("pku.shengbin.qingnang.userID", mUser.id);
				    	mActivity.startActivity(i);
					}
				});
		        reminderRow.reminderDeleteIcon.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						MessageBox.showCancelable(mActivity, "提示", "此操作将删除该药品的所有提醒，确定吗？",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == DialogInterface.BUTTON_NEGATIVE) return;
								mUser.deleteReminder(r, mActivity);
								mActivity.updateContent();
								Toast.makeText(mActivity, "删除提醒成功!", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
	        } else {
	        	// case 5, custom reminder
	        	final QNCustomReminder custom = customReminders.get(row);
	        	reminderRow.reminderMedicine.setText(custom.medicineName);
	        	reminderRow.reminderMedicine.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(mActivity, ReminderEditActivity.class);
				    	i.putExtra("pku.shengbin.qingnang.medicineID", custom.medicineID);
				    	i.putExtra("pku.shengbin.qingnang.userID", mUser.id);
				    	mActivity.startActivity(i);
					}
				});
	        	reminderRow.reminderDeleteIcon.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						MessageBox.showCancelable(mActivity, "提示", "确定要删除该提醒？",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == DialogInterface.BUTTON_NEGATIVE) return;
								QNReminder reminder = mUser.getReminderOfMedicine(custom.medicineID);
								mUser.deleteReminder(reminder, mActivity);
								reminder.customReminders.remove(custom);
								if(reminder.customReminders.size() > 0) mUser.addReminder(reminder, mActivity);
								mActivity.updateContent();
								Toast.makeText(mActivity, "删除提醒成功!", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
	        }

		}
				
        return convertView;
	}

	private static class ReminderRowViewHolder {
		TextView reminderMedicine;
		ImageView reminderDeleteIcon;
	}
	private static class HeaderRowViewHolder {
		TextView usageTypeName;
		TextView usageTypeTime;
		ImageView timeSettingIcon;
	}
	
	private class TimeSettingIconClickListener implements View.OnClickListener {
		public void onClick(View v) {
			final int header = (Integer) v.getTag();
			if (header >= 5) {
				int row = header - 5;
				QNCustomReminder custom = customReminders.get(row);
				Intent i = new Intent(mActivity, ReminderEditActivity.class);
		    	i.putExtra("pku.shengbin.qingnang.medicineID", custom.medicineID);
		    	i.putExtra("pku.shengbin.qingnang.userID", mUser.id);
		    	mActivity.startActivity(i);
			} else {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				View inflatedView = inflater.inflate(R.layout.time_pickers,null);
	        	final ViewGroup timePickers = (ViewGroup) inflatedView.findViewById(R.id.time_pickers);
				for (int i = 0; i <= header; i++) {
					timePickers.getChildAt(i).setVisibility(View.VISIBLE);
				}
				String[] times = null;
				switch (header) {
				case 0:
		        	times = mUser.onceTime;
					break;
				case 1:
					times = mUser.twiceTimes;
					break;
				case 2:
					times = mUser.threeTimes;
					break;
				case 3:
					times = mUser.fourTimes;
					break;
				case 4:
					times = mUser.nearMealTimes;
					break;
				}
				int count = ((header == 4) ? 3 : (header + 1));
				for (int i = 0; i < count; i++) {
					TimePicker tp = (TimePicker) timePickers.getChildAt(i);

					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
					Date date = null;
					try {
						date = sdf.parse(times[i]);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (date == null) date = new Date();
					}
					tp.setCurrentHour(date.getHours());
					tp.setCurrentMinute(date.getMinutes());
				}
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						if (arg1 == DialogInterface.BUTTON_NEGATIVE) return;
						String[] times = null;
						switch (header) {
						case 0:
				        	times = mUser.onceTime;
							break;
						case 1:
							times = mUser.twiceTimes;
							break;
						case 2:
							times = mUser.threeTimes;
							break;
						case 3:
							times = mUser.fourTimes;
							break;
						case 4:
							times = mUser.nearMealTimes;
							break;
						}
						int count = ((header == 4) ? 3 : (header + 1));
						for (int i = 0; i < count; i++) {
							TimePicker tp = (TimePicker) timePickers.getChildAt(i);
				        	Date date = new Date();
				        	date.setHours(tp.getCurrentHour());
				        	date.setMinutes(tp.getCurrentMinute());
							SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				        	times[i] = sdf.format(date);
						}
						mUser.defaultTimeChanged(header);
						mActivity.updateContent();
					}
	        	};
				new AlertDialog.Builder(mActivity)
	        	.setTitle("依次设置提醒时间：")
	        	.setIcon(android.R.drawable.ic_input_get)
	        	.setView(inflatedView)
	        	.setPositiveButton(R.string.ok, listener)
	        	.setNegativeButton(R.string.cancel, listener)
	        	.show();
				
			}
		}
	}
	
	private int[] checkPosition(int p) {
		int[] result = new int[3]; //header index, section index, row index
		if (p > mLastPosition) {
			if ((p - mLastPosition) % 2 == 1) {
				// is header
				result[0] = 5;
				result[1] = -1;
				result[2] = (p - mLastPosition -1) / 2;;
			} else {
				result[0] = -1;
				result[1] = 5;
				result[2] = (p - mLastPosition) / 2 - 1;
			}
		} else if (mTableHeaderPositions[4] >= 0 && p >= mTableHeaderPositions[4]) {
			if (p == mTableHeaderPositions[4]) {
				// is header
				result[0] = 4;
				result[1] = -1;
				result[2] = -1;
			} else {
				result[0] = -1;
				result[1] = 4;
				result[2] = p - mTableHeaderPositions[4] - 1;
			}
		} else if (mTableHeaderPositions[3] >= 0 && p >= mTableHeaderPositions[3]) {
			if (p == mTableHeaderPositions[3]) {
				// is header
				result[0] = 3;
				result[1] = -1;
				result[2] = -1;
			} else {
				result[0] = -1;
				result[1] = 3;
				result[2] = p - mTableHeaderPositions[3] - 1;
			}
		} else if (mTableHeaderPositions[2] >= 0 && p >= mTableHeaderPositions[2]) {
			if (p == mTableHeaderPositions[2]) {
				// is header
				result[0] = 2;
				result[1] = -1;
				result[2] = -1;
			} else {
				result[0] = -1;
				result[1] = 2;
				result[2] = p - mTableHeaderPositions[2] - 1;
			}
		} else if (mTableHeaderPositions[1] >= 0 && p >= mTableHeaderPositions[1]) {
			if (p == mTableHeaderPositions[1]) {
				// is header
				result[0] = 1;
				result[1] = -1;
				result[2] = -1;
			} else {
				result[0] = -1;
				result[1] = 1;
				result[2] = p - mTableHeaderPositions[1] - 1;
			}
		} else if (mTableHeaderPositions[0] >= 0 && p >= mTableHeaderPositions[0]) {
			if (p == mTableHeaderPositions[0]) {
				// is header
				result[0] = 0;
				result[1] = -1;
				result[2] = -1;
			} else {
				result[0] = -1;
				result[1] = 0;
				result[2] = p - mTableHeaderPositions[0] - 1;
			}
		}

		return result;
	}
	
	private boolean isAtSameDay (Calendar c1, Calendar c2) {
		if ( c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
		 c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && 
		 c1.get(Calendar.DATE) == c2.get(Calendar.DATE) ) {
			return true;
		} else return false;	
	}
}
