package cryptopals.set2;

import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;

import cryptopals.utils.ArrayUtils;
import cryptopals.utils.CipherUtils;

public class Ch16_CBC_BitFlipping {
	
	public static byte[] randomKey = new byte[16];
	
	public static byte[] prependBytes = "comment1=cooking%20MCs;userdata=".getBytes();
	public static byte[] appendBytes = ";comment2=%20like%20a%20pound%20of%20bacon".getBytes();
	
	static {
		new Random().nextBytes(randomKey);
	}
	
	public static byte[] dressUpInput(byte[] input) {
		input = Ch13_ECB_CutPaste.sanitizedText(new String(input)).getBytes();
		byte[] temp = ArrayUtils.mergeBytes(prependBytes, input);
		temp = ArrayUtils.mergeBytes(temp, appendBytes);
		
		return temp;
	}
	
	public static byte[] encrypt(byte[] rawBytes) throws Exception {
		byte[] temp = dressUpInput(rawBytes);
		temp = Ch9_PKCS7_Padding.padString(temp, randomKey.length);
		Cipher cipher = CipherUtils.getCipher("AES", "AES/CBC/NoPadding", true, randomKey);
		return cipher.doFinal(temp);
	}
	
	public static byte[] decrypt(byte[] rawBytes) throws Exception {
		//byte[] temp = dressUpInput(rawBytes);
		Cipher cipher = CipherUtils.getCipher("AES", "AES/CBC/NoPadding", false, randomKey);
		return cipher.doFinal(rawBytes);
	}
	
	public static boolean crackCrypto() throws Exception {
		String arbitraryString = "XXXXXXXXXXXXXXXX:admin<true:XXXX";		
		
		boolean isFound = false;
		byte[] encBytes = encrypt(arbitraryString.getBytes());
		
		encBytes[32] = (byte) (encBytes[32] ^ 0x01); //XOR-ing cipher byte of first : gets you ; How & Why? in ASCII code table ; is next to : 
		encBytes[38] = (byte) (encBytes[38] ^ 0x01); //= is next to < in ASCII code table
		encBytes[43] = (byte) (encBytes[43] ^ 0x01);//; is next to : in ASCII code table
		byte[] tempResult1 = decrypt(encBytes);
		String decryptedText = new String(tempResult1);
		System.out.println(new String(tempResult1));
		return decryptedText.contains("admin=true");
		/*String expectedString = "=admin=true;";
		 byte[] expectedBytes = expectedString.getBytes();
		 byte[] tempCipherBytes = new byte[expectedString.length()];
			byte[] tempBytes = Arrays.copyOf(encBytes, encBytes.length);
			byte tempIvByte = 0x0;
		  for(int i = 0; i < expectedString.length(); i++) {
			for(byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
				//b = (byte)(tempIvByte ^ b);
				byte[] tempResult = decrypt(ArrayUtils.replaceFirstByte(tempBytes, prependBytes.length+i, b));
				if(new String(tempResult).contains("userdata="+expectedString.substring(0,i+1))) {
					//System.out.println("HURRAH : " + b);
					tempBytes = ArrayUtils.replaceFirstByte(tempBytes, prependBytes.length+i, b);
					byte tempByte = (byte)((byte)'m' ^ b);
					tempBytes[prependBytes.length+i+randomKey.length] = (byte)((byte)'m' ^ tempByte);
					//tempIvByte = b;
					break;
				}
			}
		}
		System.out.println(new String(decrypt(tempBytes)));*/
		//byte[] encBytesOfArbitraryString = ArrayUtils.extractBytes(encBytes, prependBytes.length, arbitraryString.length());
		
		//return isFound;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(Ch16_CBC_BitFlipping.crackCrypto());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
