package cryptopals.set2;

import java.util.Arrays;

import cryptopals.utils.ArrayUtils;

public class Ch9_PKCS7_Padding {
	
	public static int missingBytes(int textLength, int blockLength) {
		if(textLength % blockLength == 0) return blockLength;
		int missingNoOfBytes = (blockLength >= textLength) ? blockLength - textLength : (blockLength - textLength % blockLength);
		return missingNoOfBytes % blockLength;
	}
	
	public static byte[] removePadding(byte[] raw) {
		int paddedByte = raw[raw.length - 1];
		return ArrayUtils.extractBytes(raw, 0, raw.length - paddedByte);
	}
	
	public static byte[] padString(byte[] text, int blockLength) {
		int missingNoOfBytes = missingBytes(text.length,blockLength);
		if(missingNoOfBytes <= 0) return text;
		byte[] result = Arrays.copyOf(text, text.length+missingNoOfBytes);
		Arrays.fill(result, text.length,result.length,(byte)missingNoOfBytes);
		/*paddedString.append(text);
		String hexString = Integer.toHexString(missingNoOfBytes);
		for(int i = 0; i < missingNoOfBytes; i++) {
			paddedString.append(hexString);
		}*/
		return result;
	}
	
	public static void main(String[] args) {
		String paddedString = new String(Ch9_PKCS7_Padding.padString("YELLOW SUBMARINE".getBytes(), 20));
		System.out.println(paddedString);//paddedString.equals("YELLOW SUBMARINE\u0004\u0004\u0004\u0004")
	}
}
