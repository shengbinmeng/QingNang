package pku.shengbin.qingnang;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class QNMedicineManager {
	static Context appContext;
	
	public static String getXMLWithID(String id) {
		String xmlContent = null;
		boolean loadFromJson = false;
		try {
			FileInputStream inStream = appContext.openFileInput(id + ".txt");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while((length = inStream.read(buffer)) != -1) {
			    stream.write(buffer,0,length);
			}
			stream.close();
			inStream.close();
			xmlContent = stream.toString();
			return xmlContent;
		} catch (Exception e) {
	        e.printStackTrace();
	    }
		
		if (xmlContent == null) loadFromJson = true;
		
		if (loadFromJson) {
			xmlContent = QNDataDriver.requestXML(id, false);
			saveXMLWithID (id, xmlContent);
		}
		return xmlContent;
	}
	
	private static void saveXMLWithID (String id, String xmlContent) {
		try {

            FileOutputStream outStream = appContext.openFileOutput(id + ".txt", Context.MODE_PRIVATE);
            outStream.write(xmlContent.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	void deleteXMLWithID (String id) {
		
	}
	
	public static HashMap<String, String> getAttributesByID (String id) {
		HashMap<String, String> attributes = new HashMap<String, String>() ;
		try{
			
			String xmlContent = QNMedicineManager.getXMLWithID (id);
			InputStream is = new ByteArrayInputStream(xmlContent.getBytes());
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			
			NodeList allNodes = doc.getElementsByTagName("root");
			Node rootNode = allNodes.item(0);
			for(Node node = rootNode.getFirstChild(); node != null; node = node.getNextSibling()) {
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					if(element.getNodeName().equals("name") && element.hasAttributes()) {  
			        	attributes.put("name", element.getAttribute("readablename"));            
					}
					// for the readable manual
					else if(element.getNodeName().equals("dosage") && element.hasChildNodes()) {  
			        	attributes.put("dosage", element.getFirstChild().getNodeValue());            
					}
					else if(element.getNodeName().equals("indication") && element.hasChildNodes()) {  
			        	attributes.put("indication", element.getFirstChild().getNodeValue());            
					}
					else if(element.getNodeName().equals("reaction") && element.hasChildNodes()) {  
			        	attributes.put("reaction", element.getFirstChild().getNodeValue());            
					}
					else if(element.getNodeName().equals("attention") && element.hasChildNodes()) {  
			        	attributes.put("attention", element.getFirstChild().getNodeValue());            
					}
					else if(element.getNodeName().equals("forbid") && element.hasChildNodes()) {  
			        	attributes.put("forbid", element.getFirstChild().getNodeValue());            
					}
					
					else if(element.getNodeName().equals("property") && element.hasChildNodes()) {  
			        	attributes.put("property", element.getFirstChild().getNodeValue());            
					}
					else if(element.getNodeName().equals("store") && element.hasChildNodes()) {  
			        	attributes.put("store", element.getFirstChild().getNodeValue());            
					}
					else if(element.getNodeName().equals("validity") && element.hasChildNodes()) {  
			        	attributes.put("validity", element.getFirstChild().getNodeValue());            
					}
					else if(element.getNodeName().equals("component") && element.hasChildNodes()) {  
			        	attributes.put("component", element.getFirstChild().getNodeValue());            
					}
					// more
					
					// for reminder setting
					else if(element.getNodeName().equals("period") && element.hasChildNodes()) {  
			        	attributes.put("period", element.getFirstChild().getNodeValue());            
					}
				} else {
					// for reminder setting
					if (node.getNodeName().equals("alarm") && node.hasChildNodes()) {
						Element element = (Element) node.getFirstChild();
						if (element.hasChildNodes())
						attributes.put("alarmsub", element.getFirstChild().getNodeValue());
					}
					if(node.getNodeName().equals("detailusage") && node.hasChildNodes()) { 
						Element element = (Element) node.getFirstChild();
						if (element.hasAttribute("number")) {
				        	attributes.put("adutl_number", element.getAttribute("number"));            
						}
					}
					
				}
			} 
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return attributes;
	}
	
	public static QNMedicine getMedicineByID (String id) {
		QNMedicine medicine = new QNMedicine();
		medicine.id = id;
		medicine.attributes = getAttributesByID(id);
		medicine.name = medicine.attributes.get("name");
		//for test
		//medicine.name = id + "的名称";
		return medicine;
	}
	
	public static ArrayList<QNMedicine> getRecommendMedicines() {
		ArrayList<QNMedicine> recommends = new ArrayList<QNMedicine>();
		ArrayList<String> recIds = QNDataDriver.requestRecommends();
		if (recIds == null) return null;
		for (int i = 0; i < recIds.size(); i++) {
			QNMedicine med = getMedicineByID(recIds.get(i));
			recommends.add(med);
		}
		
		//for test
		for (int i = 0; i < 0; i++) {
			QNMedicine med = getMedicineByID("recommend" + i);
			recommends.add(med);
		}
		return recommends;
		
	}
	
	public static class QNMedicine implements Serializable{

		private static final long serialVersionUID = 5998475795954249675L;
		String id;
		String name;
		HashMap<String,String> attributes;
	}
}
