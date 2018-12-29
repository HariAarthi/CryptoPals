package cryptopals.set1;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Base64;

public class Ch6_BreakRepeatingKeyXOR {

	public static String fileName = "6.txt";
	public static double minimumHammDist = -1;
	public static int potentialKeySize = 0;
	public static byte[] decodedBytes;
	
	public static int getHammingDistance(byte[] b1, byte[] b2) {
		byte temp;
		int editDistance = 0;
		for(int i = 0; i < b1.length; i++) {
			temp = (byte)(b1[i] ^ b2[i]);
			editDistance += Integer.bitCount((int)temp);
		}
		return editDistance;
	}
	
	public static double calcualteHammingDist(int keySize) {
		int start = 0, end = keySize;
		int tempHammingDist;
		int blockCount = decodedBytes.length / keySize - 1;
		double results = 0;
		try {
			for(int i = 0; i < blockCount; i++) {
				tempHammingDist = getHammingDistance(Arrays.copyOfRange(decodedBytes, start, end), Arrays.copyOfRange(decodedBytes, end, end+keySize));
				results += tempHammingDist/keySize;
				start = end+keySize;end=start+keySize;
			}			
		}catch(Exception e) {}		
		return results/(double)blockCount;
	}
	
	public static void calculateKeySize(/*byte[] inputStr,*/int start, int end) {
		double averageHammDist;
		for(int i = start; i <= end; i++) {
			averageHammDist = calcualteHammingDist(i);
			if(/*minimumHammDist > 0 &&*/ minimumHammDist > averageHammDist) { minimumHammDist = averageHammDist; potentialKeySize = i; }
			if(minimumHammDist == -1) { minimumHammDist = averageHammDist;potentialKeySize = i; }
		}
	}
	
	public static void findKeySize() {
		try {
			calculateKeySize(/*base64Decoded,*/2,40);
			System.out.println("potentialKeySize : " + potentialKeySize+"\n");
		} catch (Exception e) {	}		
	}
	
	public static void readFile() {
		try {
			DataInputStream ffs = new DataInputStream(new FileInputStream(new File(fileName/*, "UTF-8"*/)));
			byte[] fileBytes = new byte[ffs.available()];
			ffs.read(fileBytes, 0, ffs.available());
			decodedBytes = org.apache.shiro.codec.Base64.decode(fileBytes);//Shiro decode works but not Java 8?!
							//Base64.getDecoder().decode(new String(fileBytes));
			ffs.close();
		}catch(Exception e) {}
	}
	
	public static byte[][] transposeBlocks() {
		int count = 0;
		//System.out.println("decoded bytes length : "+decodedBytes.length);
		int padding = potentialKeySize - decodedBytes.length%potentialKeySize;
		//System.out.println("padding bytes needed : " + (padding));
		byte[][] tempArray = new byte[potentialKeySize][(decodedBytes.length+padding)/potentialKeySize];
		try {
			for(byte b : decodedBytes) {
				tempArray[count%potentialKeySize][count/potentialKeySize] = b;
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return tempArray;
	}
	
	public static byte[] fetchPotentialKey() {
		byte[][] transposedBlocks = transposeBlocks();
		byte[] potentialKey = new byte[transposedBlocks.length];
		int i=0;
		for(byte[] byteArray : transposedBlocks) {
			Ch3_SingleByteXOR.resetVariables();
			Ch3_SingleByteXOR.findCipher(new String(byteArray));
			potentialKey[i++] = Ch3_SingleByteXOR.possibleKey;
		}
		System.out.println("key : "+new String(potentialKey)+"\n");
		return potentialKey;
	}
	
	public static byte[] applyRepeatedXORKeyToDecrypt(byte[] decodedString, byte[] xorKey) {
		int key = 0;
		byte[] decryptedBytes = new byte[decodedString.length];
		for(byte b : decodedString) {
			decryptedBytes[key] = (byte)(b ^ xorKey[key % potentialKeySize]);				
			key++;
		}
		return decryptedBytes;
	}
	
	public static void main(String[] args) {
		//System.out.println(getHammingDistance("this is a test".getBytes(),"wokka wokka!!!".getBytes()));
		readFile();
		findKeySize();
		byte[] potentialKey = fetchPotentialKey();
		System.out.println(new String(applyRepeatedXORKeyToDecrypt(decodedBytes, potentialKey)));
	}
}