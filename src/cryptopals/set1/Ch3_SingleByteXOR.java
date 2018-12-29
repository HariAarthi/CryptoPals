package cryptopals.set1;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Ch3_SingleByteXOR {
	
	static int highScore = 0;
	static String candidateString = null;
	static byte possibleKey;
	static String inputHex = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
	
	static int[] charFrequency = new int[]{25,8,16,17,27,13,11,19,23,3,6,18,14,22,24,12,2,20,21,26,15,7,10,5,9,1,4};
	
	public static void resetVariables() {
		highScore = 0;
		candidateString = null;
		possibleKey = (byte)0;
	}

	/*public static HashMap<String,Integer> sortByHighFrequency(Map<String,Integer> countMap) {
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
	}*/
	
	public static String findCipher(String encryptedText) {		
		//String encryptedText = Ch1_HexToBase64.hexDecoding(inputHex);
		byte[] inputHexBytes = encryptedText.getBytes();
		StringBuffer result = new StringBuffer();
		/*HashMap<String,Integer> sortedHashMap = countFrequency(encryptedText);
		Iterator<String> keySet = sortedHashMap.keySet().iterator();*/
		for(int i = 0; i < 256; i++) {
			xorAgainstKey(inputHexBytes,(byte)i);
		}
		highScore = 0;
		return candidateString;
	}
	
	public static void xorAgainstKey(byte[] inputStr, byte potentialKey) {
		byte[] resultKey = new byte[inputStr.length];
		for(int i = 0; i < inputStr.length; i++) {
			resultKey[i] = (byte)(inputStr[i] ^ potentialKey);
		}
		
		int temp = calculateScore(resultKey);
		if(temp > highScore) {
			candidateString = new String(resultKey);
			highScore = temp;
			possibleKey = potentialKey;
		}
	}
	
	public static int calculateScore(byte[] resultKey) {
		int score = 0;
			for(byte b : resultKey) {
				try {
					score += ((char)b == ' ') ? (charFrequency[26]) : charFrequency[((char)b) - 'a'];
				}catch(Exception e) {/*e.printStackTrace();*/}
			}		
		return score;
	}
	
	public static void main(String[] args) {
		System.out.println(findCipher(Ch1_HexToBase64.hexDecoding(inputHex)));
		System.out.println(highScore);
	}

}