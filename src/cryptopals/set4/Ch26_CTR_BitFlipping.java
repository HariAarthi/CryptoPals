package cryptopals.set4;

import cryptopals.set2.Ch13_ECB_CutPaste;
import cryptopals.set3.Ch18_CTR_Implementation;
import cryptopals.utils.ArrayUtils;

public class Ch26_CTR_BitFlipping {
	
	public static byte[] randomKey = new byte[16];
	
	public static byte[] prependBytes = "comment1=cooking%20MCs;userdata=".getBytes();
	public static byte[] appendBytes = ";comment2=%20like%20a%20pound%20of%20bacon".getBytes();
	public static final String KEY = "YELLOW SUBMARINE"; 
	
	public byte[] dressUpInput(byte[] input) {
		input = Ch13_ECB_CutPaste.sanitizedText(new String(input)).getBytes();
		byte[] temp = ArrayUtils.mergeBytes(prependBytes, input);
		temp = ArrayUtils.mergeBytes(temp, appendBytes);
		
		return temp;
	}
	
	public String flipBits() {
		String adminString = "admin";
		String trueString = "true";
		String adminToken = ":"+adminString+">"+trueString+":";
		byte[] dressedInput = dressUpInput(adminToken.getBytes());
		System.out.println("Dressed Input : "  + new String(dressedInput));
		byte[] cipherBytes = Ch18_CTR_Implementation.implementCTR(dressedInput, KEY.getBytes());
		
		cipherBytes[prependBytes.length] ^= (byte)':' ^ (byte)';';
		cipherBytes[prependBytes.length + adminString.length() + 1] ^= (byte)'>' ^ (byte)'=';
		cipherBytes[prependBytes.length + adminToken.length() - 1] ^= (byte)':' ^ (byte)';';
		
		byte[] decBytes = Ch18_CTR_Implementation.implementCTR(cipherBytes, KEY.getBytes());
		return new String(decBytes);
	}
	
	public static void main(String[] args) {
		Ch26_CTR_BitFlipping ctrBF = new Ch26_CTR_BitFlipping();
		String alteredPlainText = ctrBF.flipBits();
		System.out.println("Bit flipped plain Text : " + alteredPlainText);
		System.out.println(alteredPlainText.contains("admin=true"));
	}

}
