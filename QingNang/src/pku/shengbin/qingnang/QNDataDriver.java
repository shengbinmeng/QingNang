package pku.shengbin.qingnang;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import pku.shengbin.qingnang.QNReminderManager.QNCustomReminder;
import pku.shengbin.qingnang.QNReminderManager.QNReminder;

public class QNDataDriver {
	public static Context appContext;
	private static String host = "127.0.0.1";
	private static int port = 8866;
	private static String currentUid = "123456/user";
	private static ArrayList<String> lastIds = new ArrayList<String>();;
	
	public static void setUid(String uid) {
		if (!currentUid.equals(uid)) {
			currentUid = uid;
		}
	}
	
	private static JSONObject sendRequestForResult (JSONObject request) {
		
		Socket socket = new Socket(); // 建立
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);  
        String server = settings.getString("custom_server", "www.qingnang.com");
        if (server != null) host = server;
		SocketAddress socAddress = new InetSocketAddress(host, port); 
		try {
			byte[] byteData = request.toString().getBytes("utf-8");
			int len = byteData.length + 4;
			byte[] byteSend = new byte[len];
			ProtocolHelper.Int32ToBytesHelper(len, 4, byteSend, 0);
			System.arraycopy(byteData, 0, byteSend, 4, byteData.length);
			socket.connect(socAddress, 5000);
			OutputStream os = new DataOutputStream(socket.getOutputStream()); // 发送
			os.write(byteSend);
			os.flush();

			// 接收
			InputStream is = socket.getInputStream();
			byte[] byLen = new byte[4];
			is.read(byLen);
			int totalLen = ProtocolHelper.BytesToInt32Helper(byLen, 0, 4);
			int dataLen = totalLen - 4;
			byteData = new byte[dataLen]; // 获得第二个对象的byte[]
			int currentLen = 0;
			int readLen = 0;
			while(currentLen < dataLen) {
				readLen = is.read(byteData, currentLen, dataLen - currentLen);
				if (readLen > 0) {
					currentLen += readLen;
				} else {
					break;
				}
			}
			
			String jsonString = new String(byteData, "utf-8");
			
			JSONTokener jsonParser = new JSONTokener(jsonString);   
		    JSONObject result = (JSONObject) jsonParser.nextValue();  
			return result;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String requestXML (String id, boolean alreadyHave) {
		JSONObject request = new JSONObject();
		try {
	    	request.put("v", 1);
		    request.put("op", 1);
		    request.put("uid", currentUid);
		    JSONObject med = new JSONObject();
		    med.put("pd", id);
		    med.put("ow", alreadyHave);
		    JSONArray array = new JSONArray();
		    array.put(med);
		    request.put("dt", array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		JSONObject result = sendRequestForResult(request);
		if (result == null) return null;
	    try {
	    	JSONArray array = result.optJSONArray("dt");
			JSONObject med = array.getJSONObject(0);
			String xmlContent = med.getString("xmldt");
			return xmlContent;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	    
	}
	
	//发送提醒设置
	public static boolean updateReminder (QNReminder reminder) {
		JSONObject request = new JSONObject();    
		try {
	    	request.put("v", 1);
		    request.put("op", 2);
		    request.put("uid", currentUid);
		    JSONArray array = new JSONArray();
		    JSONObject rmd = new JSONObject();
		    rmd.put("pd", reminder.medicineID);
		    rmd.put("tp", reminder.type);
		    rmd.put("nb", reminder.everyTimeAmount);
		    String strTemp = "";
		    if(reminder.type == QNReminder.CUSTOMED_TIME && reminder.customReminders.size() > 0)
		    {
		    	String strH = "";
		    	for(QNCustomReminder cr : reminder.customReminders)
		    	{
		    		strTemp = strH + cr.date.toString() + "," + cr.amount;
		    		strH = "1";
		    	}
		    }
		    else
		    {
		    	strTemp = reminder.beginDate.toString();
		    }
		    
		    rmd.put("st", strTemp);
		    rmd.put("et", reminder.endDate);
		    array.put(rmd);
		    request.put("dt", array);		    
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}  
	  
		JSONObject result = sendRequestForResult(request);
		if (result == null) return false;

		try {
			boolean success = result.getBoolean("cs");
			return success;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	//请求图片
	public static JSONObject requestPicture(String medicineID)
	{
		JSONObject request = new JSONObject();    
	    try {
	    	request.put("v", 1);
		    request.put("op", 3);
		    request.put("dt", medicineID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
	  
		JSONObject result = sendRequestForResult(request);
		if (result == null) return null;
		try {
			boolean success = result.getBoolean("cs");
			if (success == false) return null;
			/*Map<String,ArrayList<Byte>> mapData = new HashMap<String, ArrayList<Byte>>();
			JSONArray arrayPicture = result.getJSONArray("dt");
			ArrayList<Byte> listPicture = new ArrayList<Byte>();
			for(int i=0;i<arrayPicture.length();i++)
			{
				listPicture.add((Byte)arrayPicture.get(i));
			}
			mapData.put(result.getString("tp"), listPicture);*/
			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	//检查版本号
	public static JSONObject requestCheckVersion(String id, String vr)
	{
		JSONObject request = new JSONObject();    
	    try {
	    	request.put("v", 1);
		    request.put("op", 5);
		    request.put("uid", id);
		    request.put("vr", vr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
	  
		JSONObject result = sendRequestForResult(request);
		if (result == null) return null;
		try {
			boolean success = result.getBoolean("cs");
			if (success == false) return null;
			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	//上传本地已有的xml对应的扫描码
	public static JSONObject uploadLocalXML(int op, ArrayList<String> ls)
	{
		JSONObject request = new JSONObject();    
	    try {
	    	request.put("v", 1);
		    request.put("op", op);
		    JSONArray arrayLocalXML = new JSONArray();
		    for(String xml : ls)
		    {
		    	arrayLocalXML.put(xml);
		    }
		    request.put("dt", arrayLocalXML);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
	  
		JSONObject result = sendRequestForResult(request);
		if (result == null) return null;
		try {
			boolean success = result.getBoolean("cs");
			if (success == false) return null;
			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	//更改用户名
	public static Boolean requestEditUserName(String oldName, String newName)
	{
		JSONObject request = new JSONObject();    
	    try {
	    	request.put("v", 1);
		    request.put("op", 8);
		    request.put("uido", oldName);
		    request.put("uid", newName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}  
	  
		JSONObject result = sendRequestForResult(request);
		if (result == null) return false;
		try {
			return result.getBoolean("cs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static ArrayList<String> requestRecommends () {
		JSONObject request = new JSONObject();    
	    try {
	    	request.put("v", 1);
		    request.put("op", 4);
		    request.put("uid", currentUid);
		    
		    if (lastIds.size() == 0) {
		    	lastIds.add("6920980232069");
			    lastIds.add("6928982602934");
			    lastIds.add("6938583800523");
		    }
		    
			JSONArray lastCodes = new JSONArray(); 
			for (int i = 0; i < lastIds.size(); i++) {
			    lastCodes.put(lastIds.get(i));
			}
			
			request.put("dt", lastCodes);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
	  
		JSONObject result = sendRequestForResult(request);
		if (result == null) return null;
		try {
			boolean success = result.getBoolean("cs");
			if (success == false) return null;
			ArrayList<String> recIds = new ArrayList<String>();
			JSONArray array = result.getJSONArray("nf");
			lastIds.clear();
			for (int i = 0; i < array.length(); i++) {
				recIds.add(array.getString(i));
				lastIds.add(array.getString(i));
			}
			return recIds;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	public static class ProtocolHelper {
		public static boolean Int32ToBytesHelper(int data,int targetlen, byte[] target,int start)
		{
			byte b4 = (byte)(data & 0xff);
			byte b3 = (byte)((data >> 8) & 0xff);
			byte b2 = (byte)((data >> 16) & 0xff);
			byte b1 = (byte)((data >> 24) & 0xff);
			
			if(targetlen==4)
			{
				target[start]=b1;
				target[start+1]=b2;
				target[start+2]=b3;
				target[start+3]=b4;
			}
			else if(targetlen==2)
			{
				target[start]=b3;
				target[start+1]=b4;
			}
			
			return true;
		}
		
		public static int BytesToInt32Helper(byte[] bytes,int start,int len)
		{
			int i = 0;
			i= (0xff000000&(((int)bytes[start])<<24)   
				|0x00ff0000&(((int)bytes[start+1])<<16)   
				|0x0000ff00&(((int)bytes[start+2])<<8)   
				|0x000000ff&(((int)bytes[start+3])<<0)); 
			
			return i;
		}
	} 

	
}

