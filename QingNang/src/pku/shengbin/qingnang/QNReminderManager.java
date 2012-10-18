package pku.shengbin.qingnang;
 
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pku.shengbin.qingnang.QNUserManager.QNUser;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.os.Bundle;
 
public class QNReminderManager extends BroadcastReceiver {
		
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			Intent newIntent = new Intent(context, ReminderAlarmActivity.class);
			newIntent.putExtras(bundle);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
		} catch (Exception e) {
			Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	public static void setAlarmForReminder(QNReminder r, Context c, QNUser u) {
		Context ctx = c.getApplicationContext();
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		
		if (r.type == QNReminder.CUSTOMED_TIME) {
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < r.customReminders.size(); i++) {
				cal.setTime(r.customReminders.get(i).date);
				if(cal.before(Calendar.getInstance())) continue;
				Intent intent = new Intent(ctx, QNReminderManager.class);
				intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
				intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
				intent.putExtra("pku.shengbin.qingnang.userName", u.name);
				intent.putExtra("pku.shengbin.qingnang.alarmTime", cal.getTimeInMillis());
				intent.putExtra("pku.shengbin.qingnang.usageAmount", r.customReminders.get(i).amount);
				// In reality, you would want to have a static variable for the request code
				PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
			}
		} else {
			Calendar calBegin = Calendar.getInstance();
			calBegin.setTime(r.beginDate);
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(r.endDate);
			calEnd.add(Calendar.DATE, 1);
			
			Calendar calAlarm = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

			switch (r.type) {
			case QNReminder.ONCE_PER_DAY:
				while (calBegin.before(calEnd)) {
					try {
						Date date = sdf.parse(u.onceTime[0]);
						calAlarm.setTime(calBegin.getTime());
						calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
						calAlarm.set(Calendar.MINUTE, date.getMinutes());
						if (calAlarm.before(Calendar.getInstance())) {
							calBegin.add(Calendar.DATE, 1);
							continue;
						}
						Intent intent = new Intent(ctx, QNReminderManager.class);
						intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
						intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
						intent.putExtra("pku.shengbin.qingnang.userName", u.name);
						intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
						intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

						// In reality, you would want to have a static variable for the request code
						PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
						if (calEnd.get(Calendar.YEAR) > 6999) {
							am.setRepeating(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), 24*3600*1000, sender);
							break;
						}
						am.set(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), sender);
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			case QNReminder.TWICE_PER_DAY:
				while (calBegin.before(calEnd)) {
					try {
						for (int i = 0; i < u.twiceTimes.length; i++) {
							Date date = sdf.parse(u.twiceTimes[i]);
							calAlarm.setTime(calBegin.getTime());
							calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
							calAlarm.set(Calendar.MINUTE, date.getMinutes());
							if (calAlarm.before(Calendar.getInstance())) continue;
							Intent intent = new Intent(ctx, QNReminderManager.class);
							intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
							intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
							intent.putExtra("pku.shengbin.qingnang.userName", u.name);
							intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
							intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

							// In reality, you would want to have a static variable for the request code
							PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
							if (calEnd.get(Calendar.YEAR) > 6999) {
								am.setRepeating(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), 24*3600*1000, sender);
							} else {
								am.set(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), sender);
							}
						}
						
						if (calEnd.get(Calendar.YEAR) > 6999) break;
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			case QNReminder.THREE_TIMES_PER_DAY:
				while (calBegin.before(calEnd)) {
					try {
						for (int i = 0; i < u.threeTimes.length; i++) {
							Date date = sdf.parse(u.threeTimes[i]);
							calAlarm.setTime(calBegin.getTime());
							calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
							calAlarm.set(Calendar.MINUTE, date.getMinutes());
							if (calAlarm.before(Calendar.getInstance())) continue;
							Intent intent = new Intent(ctx, QNReminderManager.class);
							intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
							intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
							intent.putExtra("pku.shengbin.qingnang.userName", u.name);
							intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
							intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

							// In reality, you would want to have a static variable for the request code
							PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
							if (calEnd.get(Calendar.YEAR) > 6999) {
								am.setRepeating(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), 24*3600*1000, sender);
							} else {
								am.set(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), sender);
							}
						}
						if (calEnd.get(Calendar.YEAR) > 6999) break;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			case QNReminder.FOUR_TIMES_PER_DAY:
				while (calBegin.before(calEnd)) {
					try {
						for (int i = 0; i < u.fourTimes.length; i++) {
							Date date = sdf.parse(u.fourTimes[i]);
							calAlarm.setTime(calBegin.getTime());
							calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
							calAlarm.set(Calendar.MINUTE, date.getMinutes());
							if (calAlarm.before(Calendar.getInstance())) continue;
							Intent intent = new Intent(ctx, QNReminderManager.class);
							intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
							intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
							intent.putExtra("pku.shengbin.qingnang.userName", u.name);
							intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
							intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

							// In reality, you would want to have a static variable for the request code
							PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
							if (calEnd.get(Calendar.YEAR) > 6999) {
								am.setRepeating(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), 24*3600*1000, sender);
							} else {
								am.set(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), sender);
							}
						}
						if (calEnd.get(Calendar.YEAR) > 6999) break;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			case QNReminder.BEFOR_AND_AFTER_MEAL:
				while (calBegin.before(calEnd)) {
					try {
						for (int i = 0; i < u.nearMealTimes.length; i++) {
							Date date = sdf.parse(u.twiceTimes[i]);
							calAlarm.setTime(calBegin.getTime());
							calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
							calAlarm.set(Calendar.MINUTE, date.getMinutes());
							if (calAlarm.before(Calendar.getInstance())) continue;
							Intent intent = new Intent(ctx, QNReminderManager.class);
							intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
							intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
							intent.putExtra("pku.shengbin.qingnang.userName", u.name);
							intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
							intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

							// In reality, you would want to have a static variable for the request code
							PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
							if (calEnd.get(Calendar.YEAR) > 6999) {
								am.setRepeating(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), 24*3600*1000, sender);
							} else {
								am.set(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), sender);
							}
						}
						if (calEnd.get(Calendar.YEAR) > 6999) break;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			}
		}
	}
	
	public static void cancelAlarmForReminder(QNReminder r, Context c, QNUser u) {
		Context ctx = c.getApplicationContext();
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		
		if (r.type == QNReminder.CUSTOMED_TIME) {
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < r.customReminders.size(); i++) {
				cal.setTime(r.customReminders.get(i).date);
				Intent intent = new Intent(ctx, QNReminderManager.class);
				intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
				intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
				intent.putExtra("pku.shengbin.qingnang.userName", u.name);
				intent.putExtra("pku.shengbin.qingnang.alarmTime", cal.getTimeInMillis());
				intent.putExtra("pku.shengbin.qingnang.usageAmount", r.customReminders.get(i).amount);
				// In reality, you would want to have a static variable for the request code
				PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				am.cancel(sender);
			}
		} else {
			Calendar calBegin = Calendar.getInstance();
			calBegin.setTime(r.beginDate);
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(r.endDate);
			
			Calendar calAlarm = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

			switch (r.type) {
			case QNReminder.ONCE_PER_DAY:
				while (calBegin.before(calEnd)) {
					try {
						Date date = sdf.parse(u.onceTime[0]);
						calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
						calAlarm.set(Calendar.MINUTE, date.getMinutes());
						Intent intent = new Intent(ctx, QNReminderManager.class);
						intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
						intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
						intent.putExtra("pku.shengbin.qingnang.userName", u.name);
						intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
						intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

						// In reality, you would want to have a static variable for the request code
						PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
						am.cancel(sender);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (calEnd.get(Calendar.YEAR) > 6999) break;
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			case QNReminder.TWICE_PER_DAY:
				while (calBegin.before(calEnd)) {
					try {
						for (int i = 0; i < u.twiceTimes.length; i++) {
							Date date = sdf.parse(u.twiceTimes[i]);
							calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
							calAlarm.set(Calendar.MINUTE, date.getMinutes());
							Intent intent = new Intent(ctx, QNReminderManager.class);
							intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
							intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
							intent.putExtra("pku.shengbin.qingnang.userName", u.name);
							intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
							intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

							// In reality, you would want to have a static variable for the request code
							PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
							am.cancel(sender);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (calEnd.get(Calendar.YEAR) > 6999) break;
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			case QNReminder.THREE_TIMES_PER_DAY:
				while (calBegin.before(calEnd)) {
					try {
						for (int i = 0; i < u.threeTimes.length; i++) {
							Date date = sdf.parse(u.threeTimes[i]);
							calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
							calAlarm.set(Calendar.MINUTE, date.getMinutes());
							Intent intent = new Intent(ctx, QNReminderManager.class);
							intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
							intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
							intent.putExtra("pku.shengbin.qingnang.userName", u.name);
							intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
							intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

							// In reality, you would want to have a static variable for the request code
							PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
							am.cancel(sender);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (calEnd.get(Calendar.YEAR) > 6999) break;
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			case QNReminder.FOUR_TIMES_PER_DAY:
				while (calBegin.before(calEnd)) {
					try {
						for (int i = 0; i < u.fourTimes.length; i++) {
							Date date = sdf.parse(u.fourTimes[i]);
							calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
							calAlarm.set(Calendar.MINUTE, date.getMinutes());
							Intent intent = new Intent(ctx, QNReminderManager.class);
							intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
							intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
							intent.putExtra("pku.shengbin.qingnang.userName", u.name);
							intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
							intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

							// In reality, you would want to have a static variable for the request code
							PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
							am.cancel(sender);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (calEnd.get(Calendar.YEAR) > 6999) break;
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			case QNReminder.BEFOR_AND_AFTER_MEAL:
				while (calBegin.before(calEnd)) {
					try {
						for (int i = 0; i < u.nearMealTimes.length; i++) {
							Date date = sdf.parse(u.twiceTimes[i]);
							calAlarm.set(Calendar.HOUR_OF_DAY, date.getHours());
							calAlarm.set(Calendar.MINUTE, date.getMinutes());
							Intent intent = new Intent(ctx, QNReminderManager.class);
							intent.putExtra("pku.shengbin.qingnang.medicineID", r.medicineID);
							intent.putExtra("pku.shengbin.qingnang.medicineName", r.medicineName);
							intent.putExtra("pku.shengbin.qingnang.userName", u.name);
							intent.putExtra("pku.shengbin.qingnang.alarmTime", calAlarm.getTimeInMillis());
							intent.putExtra("pku.shengbin.qingnang.usageAmount", r.everyTimeAmount);

							// In reality, you would want to have a static variable for the request code
							PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
							am.cancel(sender);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (calEnd.get(Calendar.YEAR) > 6999) break;
					calBegin.add(Calendar.DATE, 1);
				}
				break;
			}
		}
	}
	
	public static void addAlarm(Context c, Date date, String mediName, String userName) {
		
	}
	
	public static void deleteAlarm(Context c, Date date, String mediName, String userName) {
		Context ctx = c.getApplicationContext();
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ctx, QNReminderManager.class);
		intent.putExtra("pku.shengbin.qingnang.medicineName", mediName);
		intent.putExtra("pku.shengbin.qingnang.userName", userName);
		intent.putExtra("pku.shengbin.qingnang.alarmTime", date.getTime());
		// In reality, you would want to have a static variable for the request code
		PendingIntent sender = PendingIntent.getBroadcast(ctx, intent.filterHashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(sender);
	}
	
	
	public static class QNReminder implements Serializable {
		static final int ONCE_PER_DAY = 0;
		static final int TWICE_PER_DAY = 1;
		static final int THREE_TIMES_PER_DAY = 2;
		static final int FOUR_TIMES_PER_DAY = 3;
		static final int BEFOR_AND_AFTER_MEAL = 4;
		static final int CUSTOMED_TIME = 5;

		private static final long serialVersionUID = -6558567763990087330L;
		public int type;
		// custom
		public ArrayList<QNCustomReminder> customReminders;
		// default
		public String medicineID;
		public String medicineName;
		public Date beginDate;
		public Date endDate;
		public int everyTimeAmount;
		
		public QNCustomReminder hasCustomReminderAtDate(Calendar cal) {
			Calendar c = Calendar.getInstance();
			for (int i = 0; i < customReminders.size(); i++) {
				c.setTime(customReminders.get(i).date);
				if (c.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && c.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && c.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
					return customReminders.get(i);
				}
			}
			return null; 
		}
		
		public boolean needAlarmAtDate (Calendar cal) {
			if (type == QNReminder.CUSTOMED_TIME) {
				if (hasCustomReminderAtDate(cal) != null) {
					return true;
				} else {
					return false;
				}
			} else {
				Calendar beginCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				beginCal.setTime(beginDate);
				endCal.setTime(endDate);
				
				if (cal.after(endCal)) {
					if (cal.get(Calendar.DATE) > endCal.get(Calendar.DATE)) return false;
					if (cal.get(Calendar.MONTH) > endCal.get(Calendar.MONTH)) return false;
					if (cal.get(Calendar.YEAR) > endCal.get(Calendar.YEAR)) return false;
				} 
				if (cal.before(beginCal)) {
					if (cal.get(Calendar.DATE) < beginCal.get(Calendar.DATE)) return false;
					if (cal.get(Calendar.MONTH) < beginCal.get(Calendar.MONTH)) return false;
					if (cal.get(Calendar.YEAR) < beginCal.get(Calendar.YEAR)) return false;
				}
				
				return true;
			}
		}
	}
	
	public static class QNCustomReminder implements Serializable {
		private static final long serialVersionUID = -6940596027623522109L;
		public String medicineID;
		public String medicineName;
		public Date date;
		public int amount;
	}

}