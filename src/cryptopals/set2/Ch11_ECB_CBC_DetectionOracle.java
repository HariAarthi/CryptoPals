package cryptopals.set2;

import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;

import cryptopals.utils.CipherUtils;

public class Ch11_ECB_CBC_DetectionOracle {
	
	public static byte[] key = CipherUtils.getRandomIV(16); //generate random key 
	
	
	public static byte[] appendBytes(byte[] inputBytes) {
		String prefixString = "Hood!CCCAAAAAAAA";
		String suffixString = "Hood!AAACCCAAAAA";
		
		int randomPrefixBytes = (new Random().nextInt(key.length-1));
		int randomSuffixBytes = key.length - randomPrefixBytes;
		byte[] alteredBytes = new byte[inputBytes.length+key.length];
		
		//add necessary bytes of prefix
		System.arraycopy(prefixString.getBytes(), 0, alteredBytes, 0, randomPrefixBytes);
		System.arraycopy(inputBytes, 0, alteredBytes, randomPrefixBytes, inputBytes.length);
		System.arraycopy(suffixString.getBytes(), 0, alteredBytes, randomPrefixBytes+inputBytes.length, randomSuffixBytes);
		//new String(alteredBytes);
		return alteredBytes;
	}
	
	public static byte[] mockEncryptingBox(byte[] inputBytes) throws Exception {
		int chooseMode = (new Random().nextInt() % 2);
		String algo = "AES", padding = "NoPadding";
		String mode = algo+"/"+((chooseMode == 0)?"ECB":"CBC")+"/"+padding;
		//System.out.println("mode used : " + mode);
		Cipher cipher = CipherUtils.getCipher(algo, mode, true, key);
		inputBytes = appendBytes(inputBytes);
		byte[] encryptedBytes = cipher.doFinal(inputBytes);
		return encryptedBytes;
	}
	
	public static int detectAESMode(byte[] plainTextBytes) {
		try {			
			//fetch encrypted bytes of the prefixed bytes
			byte[] encryptBytes = mockEncryptingBox(plainTextBytes);
			
			//fetch cipher text of prefix & suffix bytes
			byte[] fetchPrefixBytes = Arrays.copyOfRange(encryptBytes, 16, 32);
			byte[] fetchSuffixBytes = Arrays.copyOfRange(encryptBytes, 32, 48);
			
			//if they are same, the it is ECB
			if(Arrays.equals(fetchPrefixBytes,fetchSuffixBytes)) return 0;
			return 1;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void main(String[] args) {
		byte[] plainTextBytes = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes();
		int mode = detectAESMode(plainTextBytes);
		System.out.println("Random pick was : " + ((mode == 0) ? "ECB mode" : "CBC mode"));
	}

}
