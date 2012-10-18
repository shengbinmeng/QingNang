package pku.shengbin.qingnang;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import pku.shengbin.qingnang.QNMedicineManager.QNMedicine;
import pku.shengbin.qingnang.QNReminderManager.QNCustomReminder;
import pku.shengbin.qingnang.QNReminderManager.QNReminder;
import pku.shengbin.qingnang.QNUserManager.QNUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ReminderEditActivity extends Activity {
	private QNUser mUser;
	private QNMedicine mMedicine;
	private QNReminder mExistingReminder;
	private QNReminder mReminder;

	private Spinner mUsageTypeSpinner;	
	private LinearLayout mDefaultUsageSetting;
	private EditText mUsageAmountEdit;
	private EditText mBeginDateEdit;
	private EditText mEndDateEdit;
	
	private ArrayList<QNCustomReminder> 	mCustomReminders;
	private LinearLayout mCustomUsageSetting;
	private ListView mListView;
	private ReminderEditListAdapter mListAdapter;
	
	private Button mConformButton, mCancelButton;
	private boolean isEdit = false;
	private int mDefaultType = 2;
	private int mDefaultAmount = 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reminder_edit);
        String userID = this.getIntent().getStringExtra("pku.shengbin.qingnang.userID");
        mUser = QNUserManager.getUserById(userID);
        String medicineID = this.getIntent().getStringExtra("pku.shengbin.qingnang.medicineID");
        mMedicine = QNMedicineManager.getMedicineByID(medicineID);
		mReminder = new QNReminder();
		mReminder.medicineID = mMedicine.id;
		mReminder.medicineName = mMedicine.name;
		
		try {
			mDefaultType = Integer.parseInt(mMedicine.attributes.get("alarmsub")) ;
		} catch (Exception e) {
			mDefaultType = 2;
		}
		
		try {
			mDefaultAmount = Integer.parseInt(mMedicine.attributes.get("adutl_number")) ;
		} catch (Exception e) {
			mDefaultAmount = 2;
		}
		
        //TODO need to know it is to add or to edit
        mExistingReminder = mUser.getReminderOfMedicine(medicineID);
		TextView textView = (TextView) findViewById(R.id.medicine_name);
		textView.setText(mMedicine.name);
		textView.setGravity(Gravity.CENTER);
		ImageView imageView = (ImageView) findViewById(R.id.medicine_image);
		imageView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_launcher));
		
		initMedicineManual();
		initUsageTypeSpinner();
		initDefaultUsageSetting();
		initCustomUsageSetting();
		initBottomButtons();
    }
	
	private void initMedicineManual() {
		TextView textView = (TextView) findViewById(R.id.manual_item1);
		textView.setText("用法用量");
		textView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MessageBox.show(ReminderEditActivity.this, "用法用量", mMedicine.attributes.get("dosage"));
		    }
		});
		textView = (TextView) findViewById(R.id.manual_item2);
		textView.setText("适应症/功能主治");
		textView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MessageBox.show(ReminderEditActivity.this, "适应症/功能主治", mMedicine.attributes.get("indication"));
		    }
		});
		textView = (TextView) findViewById(R.id.manual_item3);
		textView.setText("查看完整说明书");
		textView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(ReminderEditActivity.this, MedicineDetailActivity.class);
		    	i.putExtra("pku.shengbin.qingnang.medicineID", mMedicine.id);
		    	startActivity(i);
		    }
		});
	}
	
	private void initUsageTypeSpinner() {
		mUsageTypeSpinner = (Spinner) findViewById(R.id.usage_type_selector);
		
		String [] options = {"每日一次", "每日两次" ,"每日三次" ,"每日四次", "饭前饭后", "自设提醒" } ;
		ArrayAdapter< String> adapter = new ArrayAdapter< String> ( this ,android.R .layout .simple_spinner_item ,options) ;
		adapter.setDropDownViewResource ( android.R .layout .simple_spinner_dropdown_item ) ;
		mUsageTypeSpinner.setAdapter (adapter) ;
		mUsageTypeSpinner.setOnItemSelectedListener ( new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 != 2 && mExistingReminder == null) {
					DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if (arg1 == DialogInterface.BUTTON_NEGATIVE) {
								mUsageTypeSpinner.setSelection (2) ;
								return ;
							}
						}
						
					};
					
					new AlertDialog.Builder(ReminderEditActivity.this)  
			        .setMessage("请按照说明书要求设定用药方式,不合理的用药方式可能会影响药效或产生毒副作用。要继续更改吗？")  
			        .setTitle("提示")  
			        .setCancelable(true)  
			        .setNegativeButton(android.R.string.cancel, listener)
			        .setPositiveButton(android.R.string.ok, listener)
			        .show();
				}
				if (arg2 == 5) {
					mDefaultUsageSetting.setVisibility(View.GONE);	
					mCustomUsageSetting.setVisibility(View.VISIBLE);
				} else {
					mDefaultUsageSetting.setVisibility(View.VISIBLE);	
					mCustomUsageSetting.setVisibility(View.GONE);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		
		if (mExistingReminder != null) {
			mUsageTypeSpinner.setSelection (mExistingReminder.type);
		} else {
			//TODO set default according to the manual
			mUsageTypeSpinner.setSelection (mDefaultType) ;
		}
	}
	
	private void initDefaultUsageSetting () {
		if (mExistingReminder != null) {
			mReminder.type = mExistingReminder.type;
			mReminder.everyTimeAmount = mExistingReminder.everyTimeAmount;
			mReminder.beginDate = (Date) mExistingReminder.beginDate.clone();
			mReminder.endDate = (Date) mExistingReminder.endDate.clone();
		} else {
			//TODO set valued according to medicine attributes
			mReminder.type = mDefaultType;
			mReminder.everyTimeAmount = mDefaultAmount;
			mReminder.beginDate = new Date();
			mReminder.endDate = new Date();
			mReminder.endDate.setYear(9999 - 1900);
		}
		
		mDefaultUsageSetting = (LinearLayout) findViewById(R.id.default_usage_setting);
		
		mUsageAmountEdit = (EditText) findViewById(R.id.usage_amount_edit);
		mUsageAmountEdit.setText(mReminder.everyTimeAmount + "");
		mUsageAmountEdit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(v.isFocusable()) return;
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						if (arg1 == DialogInterface.BUTTON_POSITIVE) {
							mUsageAmountEdit.setFocusable(true);
							mUsageAmountEdit.setFocusableInTouchMode(true);
							mUsageAmountEdit.requestFocus();
							Timer timer = new Timer();
							timer.schedule(new TimerTask() {
							@Override
								public void run() {
									imm.showSoftInput(mUsageAmountEdit, InputMethodManager.RESULT_SHOWN);
									imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
								}
							}, 300);
						} else {
							mUsageAmountEdit.clearFocus();
							mUsageAmountEdit.setFocusable(false);
					        imm.hideSoftInputFromWindow(mUsageAmountEdit.getWindowToken(),0);
						}
					}
					
				};
				new AlertDialog.Builder(ReminderEditActivity.this)  
		        .setMessage("请按照说明书要求设定用药数量,不合理的用药数量可能会影响药效或产生毒副作用。要继续更改吗？")  
		        .setTitle("提示")  
		        .setCancelable(true)  
		        .setNegativeButton(android.R.string.cancel, listener)
		        .setPositiveButton(android.R.string.ok, listener)
		        .show();
			}
		});
		
		mBeginDateEdit = (EditText) findViewById(R.id.begin_date_edit);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		mBeginDateEdit.setText(df.format(mReminder.beginDate));
		mBeginDateEdit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Calendar c = Calendar.getInstance();
				c.setTime(mReminder.beginDate);
		        new DatePickerDialog (
		            ReminderEditActivity.this,
		            new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// TODO Auto-generated method stub
							c.set(year, monthOfYear, dayOfMonth);
							mReminder.beginDate = c.getTime();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
							mBeginDateEdit.setText(df.format(mReminder.beginDate));
						}
		            }, 
		            c.get(Calendar.YEAR),
		            c.get(Calendar.MONTH),
		            c.get(Calendar.DAY_OF_MONTH)
		        ).show();
			}
		});
		
		mEndDateEdit = (EditText) findViewById(R.id.end_date_edit);
		if (mReminder.endDate.getYear() == 9999 - 1900) {
			mEndDateEdit.setText("自行停止");
		} else {
			mEndDateEdit.setText(df.format(mReminder.endDate));	
		}
		mEndDateEdit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				c.setTime(mReminder.endDate);
				
				LayoutInflater inflater = ReminderEditActivity.this.getLayoutInflater();
				View inflatedView = inflater.inflate(R.layout.end_date_edit,null);
				final CheckBox cb = (CheckBox) inflatedView.findViewById(R.id.stop_myself);
				final DatePicker dp = (DatePicker) inflatedView.findViewById(R.id.end_date_picker);
				cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							dp.setEnabled(false);
						} else {
							dp.setEnabled(true);
						}
					}
					
				});
				if (mReminder.endDate.getYear() == 9999 - 1900) {
					cb.setChecked(true);
				} else {
					cb.setChecked(false);
				}
				
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						if (arg1 == DialogInterface.BUTTON_NEGATIVE) return;
						if (cb.isChecked()) {
							mReminder.endDate.setYear(9999 - 1900);
							mEndDateEdit.setText("自行停止");
						} else {
							mReminder.endDate.setYear(dp.getYear() - 1900);
							mReminder.endDate.setMonth(dp.getMonth());
							mReminder.endDate.setDate(dp.getDayOfMonth());
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
							mEndDateEdit.setText(df.format(mReminder.endDate));
						}
					}
	        	};
				new AlertDialog.Builder(ReminderEditActivity.this)
	        	.setTitle("设置结束日期：")
	        	.setIcon(android.R.drawable.ic_input_get)
	        	.setView(inflatedView)
	        	.setPositiveButton(R.string.ok, listener)
	        	.setNegativeButton(R.string.cancel, listener)
	        	.show();
			}
		});
	}
	
	private void initCustomUsageSetting () {
		mCustomReminders = new ArrayList<QNCustomReminder>();
		if (mExistingReminder != null && mExistingReminder.customReminders != null) {
			for (int i = 0; i < mExistingReminder.customReminders.size(); i++) {
				mCustomReminders.add(mExistingReminder.customReminders.get(i));
			}
		} else {
			QNCustomReminder reminder = new QNCustomReminder();
			reminder.medicineID = mMedicine.id;
			reminder.medicineName = mMedicine.name;
			
			reminder.date = new Date();
			reminder.amount = mDefaultAmount; // a default value
			mCustomReminders.add(reminder);
		}
		
		mCustomUsageSetting = (LinearLayout) findViewById(R.id.custom_reminder_setting);
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				QNCustomReminder reminder = new QNCustomReminder();
				reminder.medicineID = mMedicine.id;
				reminder.medicineName = mMedicine.name;
				
				reminder.date = new Date();
				reminder.amount = mDefaultAmount; // a default value
				mCustomReminders.add(reminder);
				mListAdapter.notifyDataSetChanged();
		    }
		};
		
		TextView textView = (TextView) findViewById(R.id.add_reminder);
		textView.setText("添加提醒");
		textView.setOnClickListener(listener);
		ImageView imageView = (ImageView) findViewById(R.id.add_reminder_icon);
		imageView.setOnClickListener(listener);
				
		mListView = (ListView) this.findViewById(android.R.id.list); 
		mListAdapter = new  ReminderEditListAdapter (this, mCustomReminders, 0);
		mListView.setAdapter(mListAdapter);
	}
	
	private void initBottomButtons() {
		mConformButton = (Button) this.findViewById(R.id.ok);  
        mCancelButton = (Button) this.findViewById(R.id.cancel);

        View.OnClickListener listener = new View.OnClickListener() {
			public void onClick(View v) {
    			if (v == mCancelButton) {
    				ReminderEditActivity.this.finish();
    		        return ;
    			} else {
					mReminder.type = mUsageTypeSpinner.getSelectedItemPosition();

    				if (mReminder.type == QNReminder.CUSTOMED_TIME) {
    					if (mCustomReminders.size() == 0) {
    						DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface arg0, int arg1) {
    								// TODO Auto-generated method stub
    								final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    								if (arg1 == DialogInterface.BUTTON_POSITIVE) {
    		    						if (mExistingReminder != null) mUser.reminders.remove(mExistingReminder);
    		            				ReminderEditActivity.this.finish();
    								} else {
    									return;
    								}
    							}
    						};
    						new AlertDialog.Builder(ReminderEditActivity.this)  
    				        .setMessage("未设置任何提醒，将清除该药品的提醒，确定吗？")  
    				        .setTitle("提示")  
    				        .setCancelable(true)  
    				        .setNegativeButton(android.R.string.cancel, listener)
    				        .setPositiveButton(android.R.string.ok, listener)
    				        .show();
    					} else {
    						mReminder.customReminders = mCustomReminders;
    						if (mExistingReminder != null) mUser.deleteReminder(mExistingReminder,ReminderEditActivity.this);
    						mUser.addReminder(mReminder,ReminderEditActivity.this);
    						Toast.makeText(ReminderEditActivity.this, "设置提醒成功", Toast.LENGTH_SHORT).show();
            				ReminderEditActivity.this.finish();
    					}
    				} else {
    					mReminder.everyTimeAmount = Integer.parseInt(mUsageAmountEdit.getText().toString());
						if (mExistingReminder != null) mUser.deleteReminder(mExistingReminder,ReminderEditActivity.this);
    					mUser.addReminder(mReminder, ReminderEditActivity.this);
						Toast.makeText(ReminderEditActivity.this, "设置提醒成功", Toast.LENGTH_SHORT).show();
        				ReminderEditActivity.this.finish();
    				}		
					
       		        return ;
    			}
    			
    		}
        };
        
        mConformButton.setOnClickListener(listener);
        mCancelButton.setOnClickListener(listener);
	}
		
}
