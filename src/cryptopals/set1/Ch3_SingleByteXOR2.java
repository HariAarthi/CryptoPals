package cryptopals.set1;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Ch3_SingleByteXOR2 {
	
	static String inputHex = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
	
	public static HashMap<String,Integer> sortByHighFrequency(Map<String,Integer> countMap) {
		List entrySet = new LinkedList(countMap.entrySet());
		Collections.sort(entrySet, new Comparator() {
			public int compare(Object o1, Object o2) {
				int returnValue = ((Comparable) 
						((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
				if(returnValue > 0) return -1;
				if(returnValue < 0) return 1;
				return returnValue;
			}
		});
		HashMap<String,Integer> sortedHashMap = new LinkedHashMap();
		Iterator iter = entrySet.iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			sortedHashMap.put((String)entry.getKey(), (Integer)entry.getValue());
		}
		return sortedHashMap;
	}
	
	public static HashMap<String,Integer> countFrequency(String encryptedText) {
		String tmp = null;
		Map<String,Integer> countMap = new HashMap();
		for(int i = 0; i < encryptedText.length(); ) {
			tmp = encryptedText.charAt(i++) + "";// + encryptedText.charAt(i++);
			if(countMap.containsKey(tmp)) {
				countMap.put(tmp, countMap.get(tmp)+1);
			} else {
				countMap.put(tmp, 1);
			}
		}
		return sortByHighFrequency(countMap);
	}
	
	public static String findCipher() {		
		String encryptedText = Ch1_HexToBase64.hexDecoding(inputHex);
		byte[] inputHexBytes = encryptedText.getBytes();
		StringBuffer result = new StringBuffer();
		HashMap<String,Integer> sortedHashMap = countFrequency(encryptedText);
		Iterator<String> keySet = sortedHashMap.keySet().iterator();
		String key;
		byte keyByte;
		while(keySet.hasNext()) {
			key = keySet.next();
			xorAgainstKey(encryptedText,getKey(encryptedText.substring(0, 1).getBytes(),(byte)key.charAt(0)));
		}
		return result.toString();
	}
	
	public static String getKey(byte[] inputStr, byte tryAgainst) {
		byte[] resultKey = new byte[inputStr.length];
		for(int i = 0; i < inputStr.length; i++) {
			resultKey[i] = (byte)(inputStr[i] ^ tryAgainst);
		}
		return new String(resultKey);
	}
	
	public static void xorAgainstKey(String inputHex, String key) {
		byte[] inputHexBytes = inputHex.getBytes();
		byte[] keyBytes = key.getBytes();
		byte[] resultBytes = new byte[inputHexBytes.length];
		int i = 0;
		for(byte b : inputHexBytes) {
			resultBytes[i] = (byte)(inputHexBytes[i] ^ keyBytes[0]);
			i++;
		}
		try {
			System.out.println(new String(resultBytes));
			/*System.out.println("*");
			System.out.println(Ch1_HexToBase64.convertToBase64(new String(resultBytes)));
			System.out.println("**");*/
		}catch(Exception e) {}
		
	}
	
	public static void main(String[] args) {
		System.out.println(findCipher());
	}

}