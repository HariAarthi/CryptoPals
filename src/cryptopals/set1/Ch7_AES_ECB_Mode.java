package cryptopals.set1;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import cryptopals.utils.CipherUtils;
import cryptopals.utils.ReadFile;

public class Ch7_AES_ECB_Mode {
	
	public static byte[] perform_AES_ECB(Cipher cipher, byte[] fileBytes) {
		byte[] decryptedBytes = null;
		        		
        try {
			decryptedBytes = cipher.doFinal(fileBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}        
		return decryptedBytes;
	}
	
	public static void main(String[] args) {
		String key = "YELLOW SUBMARINE";
		byte[] decodedBytes = ReadFile.readFile("7.txt");
		
		byte[] decryptedBytes;
		try {
			decryptedBytes = Ch7_AES_ECB_Mode.perform_AES_ECB(CipherUtils.getCipher("AES","AES/ECB/NoPadding",false,key.getBytes()), decodedBytes);
			System.out.println(new String(decryptedBytes));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
