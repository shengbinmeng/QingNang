package pku.shengbin.qingnang;

import java.util.List;

import pku.shengbin.qingnang.QNUserManager.QNUser;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserListAdapter extends BaseAdapter{

	private List<QNUser> 					mUsers;
	private int								mType;
	private LayoutInflater 					mInflater;

	public UserListAdapter(Context context, List<QNUser> users, int type) {
		mUsers = users;
		mType = type;
        mInflater = LayoutInflater.from(context);
	}
	
	public int getCount() {
		int n = mUsers.size();
		return n;
	}

	public Object getItem(int position) {
		return mUsers.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private void setRowInfo(UserRowViewHolder userRow, int position) {
		QNUser user = mUsers.get(position);
		userRow.userNameTextView.setText(user.name);
		if (mType == 0) {
			userRow.descriptionTextView.setText("共" + user.reminders.size() + "个提醒");
		}
		if (mType == 1) {
			userRow.descriptionTextView.setText("共" + user.medicines.size() + "种药品");
		}
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		// A UserRowViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        UserRowViewHolder userRow;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_row, null);

            // Creates a UserRowViewHolder and store references to the two children views
            // we want to bind data to.
            userRow = new UserRowViewHolder();
            userRow.userNameTextView = (TextView) convertView.findViewById(R.id.user_name);
            userRow.descriptionTextView = (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(userRow);
        } else {
            // Get the UserRowViewHolder back to get fast access to the children views
            userRow = (UserRowViewHolder) convertView.getTag();
        }

		// Bind the data efficiently with the userRow.
		setRowInfo(userRow, position);
		
        return convertView;
	}

	private static class UserRowViewHolder {
	    TextView userNameTextView;
	    TextView descriptionTextView;
	}
}
