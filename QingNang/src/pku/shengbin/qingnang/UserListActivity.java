package pku.shengbin.qingnang;

import java.util.List;

import pku.shengbin.qingnang.QNUserManager.QNUser;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class UserListActivity extends ListActivity {
	private List<QNUser> mUserList;
	private String type; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list);
		type = this.getIntent().getStringExtra("pku.shengbin.qingnang.Type");

		this.initUserList();
		
		ListView listView = (ListView) findViewById(android.R.id.list);        
        listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
        	public void onCreateContextMenu(ContextMenu menu, View v,
	        	ContextMenuInfo menuInfo) {
		        	menu.setHeaderTitle("操作");
		        	menu.add(0,0,0,"删除该用户");
	        	}
        	}
        );
        
		if(type == "reminder") {
			//findViewById(R.id.user_list_activity).setBackgroundColor(Color.BLUE);
		} else {
			//findViewById(R.id.user_list_activity).setBackgroundColor(Color.MAGENTA);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final QNUser user = mUserList.get(info.position);
		int id = item.getItemId();
		if (id == 0) {
			new AlertDialog.Builder(this)
        	.setMessage("将删除该用户的所有信息（包括药品和提醒），确定吗？")
        	.setPositiveButton("是", 
        		new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface arg0, int arg1) {
	        			QNUserManager.deleteUser(user);
	        			updateUserList();
    					Toast.makeText(UserListActivity.this, "删除用户成功!", Toast.LENGTH_SHORT).show();
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
		}
		
		return super.onContextItemSelected(item);
	}

	protected void initUserList() {
		mUserList = QNUserManager.getUserList();
		if(type == "reminder") {
			this.setListAdapter(new UserListAdapter(this, this.mUserList, 0));	
		} else {
			this.setListAdapter(new UserListAdapter(this, this.mUserList, 1));	
		}
    }
	
	public void updateUserList() {
		//mUserList = QNUserManager.getUserList();
		((UserListAdapter)this.getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		updateUserList();
		super.onResume();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i;
		if(type == "reminder") {
			i = new Intent(this, ReminderActivity.class);
		} else {
			i = new Intent(this, MedicineActivity.class);
		}
		String userId = mUserList.get(position).id;
		i.putExtra("pku.shengbin.qingnang.userID", userId);
		QNDataDriver.setUid("123456/" + userId);

    	startActivity(i);
	}
	
	
}
