package cryptopals.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import cryptopals.validator.HMAC_SHA1_Validator;

public class WebServerSimulator {
	
	private static int count = 0;
	private static WebServerSimulator webServerInstance = null;
	
	private WebServerSimulator() {}
	
	public static WebServerSimulator getInstance() {
		if(webServerInstance == null) {
			synchronized(WebServerSimulator.class) {
				if(webServerInstance == null) {
					webServerInstance = new WebServerSimulator();
				}
			}			
		}		
		return webServerInstance;
	}
	
	private Map<String,String> parseURL(String url) {
		Map<String,String> keyValue = new HashMap<String,String>();
		int index = url.indexOf("file");
		String subString = url.substring(index);
		StringTokenizer tokens1 = new StringTokenizer(subString,"&");
		StringTokenizer tokens2 = null;
		String tempString = null;
		while(tokens1.hasMoreTokens()) {
			tempString = tokens1.nextToken();
			tokens2 = new StringTokenizer(tempString,"=");
			keyValue.put(tokens2.nextToken(), tokens2.nextToken());
		}
		return keyValue;
	}
	
	public boolean handleRequest(String url) {
		boolean isMatch = false;
		try {
			Map<String,String> values = parseURL(url);
			String fileName = "", signature = "";
			for(Entry key : values.entrySet()) {
				if("file".equals(key.getKey())) fileName = (String) key.getValue();
				else signature = (String) key.getValue();
			}
			byte[] calculatedSignature = HMAC_SHA1_Validator.calculateSignature(fileName);
			if(count == 0) System.out.println("Actual Signature :" + new String(calculatedSignature));
			count++;
			isMatch = HMAC_SHA1_Validator.validatorWithTimeLeak_Insecure(signature.getBytes(), calculatedSignature);
		} catch (Exception e) {
			isMatch = false;
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return isMatch;
	}
	
	public static void main(String[] args) {
		String url = "http://localhost:9000/test?file=foo&signature=46b4ec586117154dacd49d664e5d63fdc88efb51";
		WebServerSimulator webServer = WebServerSimulator.getInstance();
		webServer.handleRequest(url);
	}

}
