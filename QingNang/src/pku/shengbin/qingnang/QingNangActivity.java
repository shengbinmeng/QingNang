package pku.shengbin.qingnang;
import java.util.ArrayList;
import java.util.List;

import pku.shengbin.qingnang.QNUserManager.QNUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class QingNangActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);
    	
        setContentView(R.layout.main);
    	Resources res = this.getResources(); // Resource object to get Drawables
	    TabHost tabHost = this.getTabHost();  // The activity TabHost
	    TabSpec spec;
	    Intent intent; 
 
	    intent = new Intent(this, UserListActivity.class);
	    intent.putExtra("pku.shengbin.qingnang.Type", "reminder");
	    spec = tabHost.newTabSpec("reminder_tab")
	    .setIndicator("提醒", res.getDrawable(android.R.drawable.ic_popup_reminder))
	    .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent(this, UserListActivity.class);
	    intent.putExtra("pku.shengbin.qingnang.Type", "user");
	    spec = tabHost.newTabSpec("medicine_tab")
	    .setIndicator("药箱", res.getDrawable(android.R.drawable.ic_popup_disk_full))
	    .setContent(intent);
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(0);
	    
	    QNUserManager.appContext = this.getApplicationContext();
	    QNMedicineManager.appContext = this.getApplicationContext();
	    QNDataDriver.appContext = this.getApplicationContext();
    }
    
    // Menu item Ids
    public static final int MENU_REFRESH = Menu.FIRST;
    public static final int MENU_ADDUSER = Menu.FIRST + 1;
    public static final int MENU_SETTINGS = Menu.FIRST + 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu); 
		menu.add(0, MENU_ADDUSER, 0, "添加用户");
		menu.add(0, MENU_REFRESH, 1, "使用说明");
		menu.add(0, MENU_SETTINGS, 2, "设置选项");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case MENU_REFRESH:
	        {
				MessageBox.show(QingNangActivity.this, "如何使用青囊", "blablablablablablabalbalbablabalblablablabalbla...");
	        	break;	        	
	        }
	        case MENU_ADDUSER:
	        {
        		final List<String> autoNames = new ArrayList<String>();
	        	autoNames.add("Me");
	        	final AutoCompleteTextView nameEdit = new AutoCompleteTextView(this);
	        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	                    android.R.layout.simple_dropdown_item_1line, autoNames.toArray(new String[0]));
	        	nameEdit.setAdapter(adapter);
	        	nameEdit.setThreshold(1);

	        	DialogInterface.OnClickListener ok_listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						String input = nameEdit.getText().toString();
						if (input.equals("")) {
							MessageBox.show(QingNangActivity.this, "提示", "请输入内容!");
							return ;
						}
						
						try{
							//TODO add a user
							QNUser user = new QNUser();
							user.type = 0;
							user.name = input;
							QNUserManager.addUser(user);
							Toast.makeText(QingNangActivity.this, "添加用户成功!", Toast.LENGTH_SHORT).show();
							((UserListActivity) (QingNangActivity.this.getCurrentActivity())).updateUserList();
						}catch(Exception e) {
							MessageBox.show(QingNangActivity.this, "出错了", "添加用户出现异常:"+ e.getMessage());
						}
					}
	        	};

	        	new AlertDialog.Builder(this)
	        	.setTitle("输入姓名:")
	        	.setIcon(android.R.drawable.ic_input_get)
	        	.setView(nameEdit)
	        	.setPositiveButton("确定", ok_listener)
	        	.setNeutralButton("通讯录", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);   
						QingNangActivity.this.startActivityForResult(intent, 111);
					}
				})
	        	.setNegativeButton("取消", null)
	        	.show();
	        	break;	        	
	        }
	        case MENU_SETTINGS:
	        {
	        	startActivity(new Intent(this, Settings.class));
	        	break;
	        }
	        default:
	        	break;
        }
       
        return super.onOptionsItemSelected(item);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode)
    	{
    		case (111) :
    		{
	    		if (resultCode == Activity.RESULT_OK)
	    		{
		    		Uri contactData = data.getData();
		    		Cursor c = managedQuery(contactData, null, null, null, null);
		    		c.moveToFirst();
		    		int displayNameColumn = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		    		String displayName = c.getString(displayNameColumn);
		    		try{
						//TODO add a user
						QNUser user = new QNUser();
						user.name = displayName;
						user.type = 1;
						QNUserManager.addUser(user);
						Toast.makeText(QingNangActivity.this, "添加用户成功!", Toast.LENGTH_SHORT).show();
						((UserListActivity) (QingNangActivity.this.getCurrentActivity())).updateUserList();
					} catch(Exception e) {
						MessageBox.show(QingNangActivity.this, "出错了", "添加用户出现异常:"+ e.getMessage());
					}
	    		}
    		
    		break;
    		
    		}

    	}
    }
    
    
    // Since TabActivity not response to onKeyDown, we use dispatchKeyEvent to do this
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
        	new AlertDialog.Builder(this)
        	.setMessage("将退出青囊，确定吗？")
        	.setPositiveButton("是", 
        		new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface arg0, int arg1) {
	        			finish();
	        		}
        		}
        	)
        	.setNegativeButton("否", 
        		new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
        		}
        	)
        	.show();
        	
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

	@Override
	public void finish() {
		QNUserManager.saveUsersData();
		super.finish();
	}
    
}