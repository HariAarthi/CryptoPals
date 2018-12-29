package cryptopals.set2;

import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;

import cryptopals.utils.CipherUtils;

public class Ch11_ECB_CBC_DetectionOracle {
	
	public static byte[] key = CipherUtils.getRandomIV(16); //generate random key 
	
	public static byte[] mockEncryptingBox(byte[] inputBytes) throws Exception {
		int chooseMode = (new Random().nextInt() % 2);
		String algo = "AES", padding = "NoPadding";
		String mode = algo+"/"+((chooseMode == 0)?"ECB":"CBC")+"/"+padding;
		//System.out.println("mode used : " + mode);
		Cipher cipher = CipherUtils.getCipher(algo, mode, true, key);
		byte[] encryptedBytes = cipher.doFinal(inputBytes);
		return encryptedBytes;
	}
	
	public static int detectAESMode(byte[] plainTextBytes) {
		String commonString = "Hood!CCCAAAAAAAA";
		String prefix, suffix;
		prefix = suffix = commonString;
		try {
			//compute necessary bytes of prefix to be added
			int prefixBytesNeeded = key.length - (plainTextBytes.length % key.length);
			byte[] prefixedBytes = new byte[plainTextBytes.length+prefixBytesNeeded+32];
			
			//add necessary bytes of prefix
			System.arraycopy(prefix.getBytes(), 0, prefixedBytes, 0, prefix.length());
			System.arraycopy(plainTextBytes, 0, prefixedBytes, prefix.length(), plainTextBytes.length);
			System.arraycopy(suffix.getBytes(), 0, prefixedBytes, prefix.length()+plainTextBytes.length+prefixBytesNeeded, suffix.length());
			
			//fetch encrypted bytes of the prefixed bytes
			byte[] encryptBytes = mockEncryptingBox(prefixedBytes);
			
			//fetch cipher text of prefix & suffix bytes
			byte[] fetchPrefixBytes = Arrays.copyOfRange(encryptBytes, 0, prefix.length());
			byte[] fetchSuffixBytes = Arrays.copyOfRange(encryptBytes, prefix.length()+plainTextBytes.length+prefixBytesNeeded, encryptBytes.length);
			
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
		byte[] plainTextBytes = "Test Text".getBytes();
		int mode = detectAESMode(plainTextBytes);
		System.out.println("Random pick was : " + ((mode == 0) ? "ECB mode" : "CBC mode"));
	}

}
