package pku.shengbin.qingnang;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import pku.shengbin.qingnang.QNReminderManager.QNCustomReminder;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

public class ReminderEditListAdapter extends BaseAdapter {

	private ArrayList<QNCustomReminder> 	mReminders;
	private int								mType;
	private LayoutInflater 					mInflater;
	private Context 						mContext;

	public ReminderEditListAdapter(Context context, ArrayList<QNCustomReminder> reminders, int type) {
		mReminders = reminders;
		mType = type;
        mInflater = LayoutInflater.from(context);
        mContext = context;
	}
	
	public int getCount() {
		int n = mReminders.size();
		return n;
	}

	public Object getItem(int position) {
		return mReminders.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private void setRowInfo(ReminderEditRowViewHolder reminderEditRow, final int position) {
		
		if (mType == 1 || mType == 0) {
			QNCustomReminder reminder = mReminders.get(position);
			Calendar c = Calendar.getInstance();
			c.setTime(reminder.date);
			reminderEditRow.reminderTitle.setText("提醒" + (position+1));
	    	reminderEditRow.reminderDate.setText((c.get(Calendar.YEAR)) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE));
	    	reminderEditRow.reminderTime.setText(new SimpleDateFormat("HH:mm").format(reminder.date));
	        reminderEditRow.usageAmount.setText(reminder.amount + "");
		}
		
		reminderEditRow.removeIcon.setTag(position);
		reminderEditRow.removeIcon.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//int position = (Integer) v.getTag();
				mReminders.remove(position);
				ReminderEditListAdapter.this.notifyDataSetChanged();
		    }
		});
		
		reminderEditRow.reminderDate.setTag(position);
		reminderEditRow.reminderDate.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final Calendar c = Calendar.getInstance();
				c.setTime(mReminders.get(position).date);
		        new DatePickerDialog (
		            mContext,
		            new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// TODO Auto-generated method stub
							c.set(year, monthOfYear, dayOfMonth);
							mReminders.get(position).date = c.getTime();
							ReminderEditListAdapter.this.notifyDataSetChanged();
						}
		            }, 
		            c.get(Calendar.YEAR),
		            c.get(Calendar.MONTH),
		            c.get(Calendar.DAY_OF_MONTH)
		        ).show();
			}
			
		});
		
		reminderEditRow.reminderTime.setTag(position);
		reminderEditRow.reminderTime.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final Calendar c = Calendar.getInstance();
				c.setTime(mReminders.get(position).date);
				new TimePickerDialog (
			            mContext,
			            new TimePickerDialog.OnTimeSetListener() {
							public void onTimeSet(TimePicker arg0, int hourOfDay,
									int minute) {
								// TODO Auto-generated method stub
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								mReminders.get(position).date = c.getTime();
								ReminderEditListAdapter.this.notifyDataSetChanged();
							}
			            }, 
			            c.get(Calendar.HOUR_OF_DAY),
			            c.get(Calendar.MINUTE),
			            true
			        ).show();
			}
			
		});
		
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		// A ReminderEditRowViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ReminderEditRowViewHolder reminderEditRow;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.reminder_edit_row, null);

            // Creates a ReminderEditRowViewHolder and store references to the two children views
            // we want to bind data to.
            reminderEditRow = new ReminderEditRowViewHolder();
            reminderEditRow.reminderTitle = (TextView) convertView.findViewById(R.id.reminder_title);
            reminderEditRow.removeIcon = (ImageView) convertView.findViewById(R.id.remove_reminder_icon);
            reminderEditRow.reminderDate = (EditText) convertView.findViewById(R.id.reminder_date);
            reminderEditRow.reminderTime = (EditText) convertView.findViewById(R.id.reminder_time);
            reminderEditRow.usageAmount = (EditText) convertView.findViewById(R.id.reminder_usage_amount);
            
            convertView.setTag(reminderEditRow);
        } else {
            // Get the ReminderEditRowViewHolder back to get fast access to the children views
            reminderEditRow = (ReminderEditRowViewHolder) convertView.getTag();
        }

		// Bind the data efficiently with the reminderEditRow.
		setRowInfo(reminderEditRow, position);
		
        return convertView;
	}

	private static class ReminderEditRowViewHolder {
		TextView reminderTitle;
		ImageView removeIcon;
	    EditText reminderDate;
	    EditText reminderTime;
	    EditText usageAmount;
	}
}
