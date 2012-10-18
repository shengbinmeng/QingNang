package pku.shengbin.qingnang;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;

import pku.shengbin.qingnang.QNMedicineManager.QNMedicine;
import pku.shengbin.qingnang.QNReminderManager.QNReminder;

public class QNUserManager {
	//private final static String PATH = Environment.getExternalStorageDirectory().getPath().concat(".qingnang/");
	//private final static String FILENAME = "users.data";

	public static Context appContext;
	public static ArrayList<QNUser> users;
	
	private static int saveUsers() {
		if (users == null) {
			return -1;
		}
		ObjectOutputStream out = null;

		try {
	        FileOutputStream outStream = appContext.openFileOutput("users.data", Context.MODE_PRIVATE);
			out = new ObjectOutputStream (
					outStream);
			for (int i = 0; i < users.size(); i++) {
				out.writeObject(users.get(i));
			}
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private static int loadUsers() {
		users.clear();
		QNUser user;
		try {
			FileInputStream inStream = appContext.openFileInput("users.data");
			ObjectInputStream oin = new ObjectInputStream(inStream);
			while ((user = (QNUser)oin.readObject()) != null) {
				users.add(user);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int deleteUser (QNUser u) {
		if (users == null) {
			users = new ArrayList<QNUser>();
			// load all from file
			loadUsers();
		}
		users.remove(u);
		// may delete the medicines that become not used
		
		return 0;
	}
	
	public static int addUser (QNUser u) {
		if (users == null) {
			users = new ArrayList<QNUser>();
			// load all from file
			loadUsers();
		}
		u.reminders = new ArrayList<QNReminder>();
		u.medicines = new ArrayList<QNMedicine>();
		u.id = u.name;
		users.add(u);
		return 0;
	}
	
	public static ArrayList<QNUser> getUserList() {
		if (users == null) {
			users = new ArrayList<QNUser>();
			// load all from file
			loadUsers();
		}
		return users;
	}
	
	public static QNUser getUserById(String id) {
		if (users == null) {
			users = new ArrayList<QNUser>();
			// load all from file
			loadUsers();
		}
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).id.equals(id)) {
				return users.get(i);
			}
		}
		return null;
	}
	
	public static void saveUsersData() {
		saveUsers();
	}
	
	public static class QNUser implements Serializable {
		private static final long serialVersionUID = 6752140415970174463L;
		public int type;
		public String name;
		public String id;
		public String[] onceTime = {"08:00"};
		public String[] twiceTimes = {"08:00", "20:00"};
		public String[] threeTimes = {"06:00", "14:00", "22:00"};
		public String[] fourTimes = {"06:00", "12:00", "16:00", "22:00"};
		public String[] nearMealTimes = {"08:00", "12:00", "18:00"};
		public ArrayList<QNReminder> reminders;
		public ArrayList<QNMedicine> medicines;
		
		public void addReminder (QNReminder r, Context ctx) {
			reminders.add(r);
			QNReminderManager.setAlarmForReminder(r, ctx, this);
		}
		
		public void deleteReminder (QNReminder r, Context ctx) {
			QNReminderManager.cancelAlarmForReminder(r, ctx, this);
			reminders.remove(r);
		}
		
		public void addMedicine (QNMedicine m) {
			medicines.add(m);
		}
		
		public void deleteMedicine (QNMedicine m) {
			// when delete a medicine, reminder of it should also be deleted
			QNReminder r = this.getReminderOfMedicine(m.id);
			if (r != null) {
				this.deleteReminder(r, appContext);
			}
			medicines.remove(m);
		}
		
		public void defaultTimeChanged(int which) {
			for (int i = 0; i < reminders.size(); i++) {
				QNReminder r = reminders.get(i);
				if (r.type == which) {
					this.deleteReminder(r, appContext);
					this.addReminder(r, appContext);
				}
			}
		}
		
		public QNReminder getReminderOfMedicine(String id) {
			for (int i = 0; i < reminders.size(); i++) {
				if (reminders.get(i).medicineID.equals(id)) {
					return reminders.get(i);
				}
			}
			return null;
		}
	}

}
