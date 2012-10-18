package pku.shengbin.qingnang;

import pku.shengbin.qingnang.QNMedicineManager.QNMedicine;
import pku.shengbin.qingnang.QNUserManager.QNUser;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import java.util.ArrayList;

public class MedicineListActivity extends ListActivity {
	private QNUser mUser;
	private ArrayList<QNMedicine> mMedicineList;
	private MedicineListAdapter mAdapter;
	private LinearLayout mBottom;
	private Button mAddButton, mDeleteButton, mCancelButton;
	private ListView mListView;
	private boolean inSelectMode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medicine_list);
		
		String userID = this.getIntent().getStringExtra("pku.shengbin.qingnang.userID");
        mUser = QNUserManager.getUserById(userID);
        
		initBottomButtons();
		
		initMedicineList();

		mListView = (ListView) this.findViewById(android.R.id.list);        
        mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
        	public void onCreateContextMenu(ContextMenu menu, View v,
	        	ContextMenuInfo menuInfo) {
		        	menu.setHeaderTitle("操作");
		        	menu.add(0,0,0,"添加提醒");
		        	menu.add(0,1,1,"查看说明书");
		        	menu.add(0,2,2,"删除该药品");
	        	}
        	}
        ); 
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		QNMedicine medicine = mMedicineList.get(info.position);
		int id = item.getItemId();
		if (id == 0) {
	    	Intent i = new Intent(this, ReminderEditActivity.class);
	    	i.putExtra("pku.shengbin.qingnang.medicineID", medicine.id);
	    	i.putExtra("pku.shengbin.qingnang.userID", mUser.id);
	    	startActivity(i);
		} else if (id == 1) {	
	    	Intent i = new Intent(this, MedicineDetailActivity.class);
	    	i.putExtra("pku.shengbin.qingnang.medicineID", medicine.id);
	    	startActivity(i);
		} else if (id == 2) {
			mUser.deleteMedicine(medicine);
			mAdapter.notifyDataSetChanged();
			Toast.makeText(MedicineListActivity.this, "删除药品成功!", Toast.LENGTH_SHORT).show();
		}
		
		return super.onContextItemSelected(item);
	}
	
	private void initMedicineList() {	
		mMedicineList = mUser.medicines;
		mAdapter = new MedicineListAdapter(this, mMedicineList);
		this.setListAdapter(mAdapter);
    }
	
	private void initBottomButtons() {
		mAddButton = (Button) this.findViewById(R.id.add);  
        mDeleteButton = (Button) this.findViewById(R.id.delete);
        mCancelButton = (Button) this.findViewById(R.id.cancel);
		mBottom = (LinearLayout) this.findViewById(R.id.bottom);
        mBottom.setVisibility(View.GONE);

        View.OnClickListener listener = new View.OnClickListener() {
			public void onClick(View v) {
    			// TODO Auto-generated method stub
    			if (v == mCancelButton) {
    				mBottom.setVisibility(View.GONE);
    				mAdapter.setSelectMode(false);
    		        mAdapter.notifyDataSetChanged();
    		        inSelectMode = false;
    		        return ;
    			} 
    			
    			Integer[] positions = mAdapter.getSelectPositions();
    			if (positions.length == 0) {
    				MessageBox.show(MedicineListActivity.this, "提示", "请先选择操作项!");
    			} else {
    				if (v == mAddButton) {
    					for (int i = 0; i < positions.length; i++) {
    						QNMedicine medicine = mMedicineList.get(positions[i]);		
    				    	Intent intent = new Intent(MedicineListActivity.this, ReminderEditActivity.class);
    				    	intent.putExtra("pku.shengbin.qingnang.medicineID", medicine.id);
    				    	intent.putExtra("pku.shengbin.qingnang.userID", mUser.id);
    				    	startActivity(intent);
    					}
    				} else if (v == mDeleteButton) {
    					ArrayList<QNMedicine> selected = new ArrayList<QNMedicine>();
    					for (int i = 0; i < positions.length; i++) {
    						selected.add(mMedicineList.get(positions[i]));
    					}
    					for (int i = 0; i < selected.size(); i++) {
    						mUser.deleteMedicine(selected.get(i));
    					}
    					mAdapter.clearSelectState();
    					mAdapter.notifyDataSetChanged();
    					Toast.makeText(MedicineListActivity.this, "删除药品成功!", Toast.LENGTH_SHORT).show();
    				}
    				
    				
    			}
    		}
        };
        
        mAddButton.setOnClickListener(listener);
        mCancelButton.setOnClickListener(listener);
        mDeleteButton.setOnClickListener(listener);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (inSelectMode) {
			CheckBox checkBox = (CheckBox) v.findViewById(R.id.item_cb);
			checkBox.toggle();
			mAdapter.setCheckItem(position, checkBox.isChecked());
		} else {
			QNMedicine medicine = mMedicineList.get(position);
			Intent i = new Intent(this, ReminderEditActivity.class);
	    	i.putExtra("pku.shengbin.qingnang.medicineID", medicine.id);
	    	i.putExtra("pku.shengbin.qingnang.userID", mUser.id);
	    	startActivity(i);
		}
	}
	
	// Menu item Ids
    public static final int MENU_ADDMED = Menu.FIRST;
    public static final int MENU_BATCH = Menu.FIRST + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);     
		menu.add(0, MENU_ADDMED, 1, "添加药品");
		menu.add(0, MENU_BATCH, 1, "批量操作");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case MENU_ADDMED:
	        {
				Intent i = new Intent(this, MedicineAddActivity.class);
		    	i.putExtra("pku.shengbin.qingnang.userID", mUser.id);
		    	startActivity(i);
		    	break;        	
	        }
	        case MENU_BATCH:
	        {
		        mBottom.setVisibility(View.VISIBLE);
		        mAdapter.setSelectMode(true);
		        mAdapter.notifyDataSetChanged();
		        inSelectMode = true;
	        }
        }
        return super.onOptionsItemSelected(item);
    } 
    
    private void updateMedicineList() {
    	mAdapter.notifyDataSetChanged();
    }
    
    @Override
	protected void onResume() {
		updateMedicineList();
		super.onResume();
	}
}