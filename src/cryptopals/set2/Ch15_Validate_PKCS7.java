package cryptopals.set2;

import cryptopals.utils.ArrayUtils;

public class Ch15_Validate_PKCS7 {

	public static boolean isValidPKCS7Padding(byte[] raw) throws Exception {
		boolean isValid = false;
		byte lastByte = raw[raw.length - 1];
		byte[] temp = ArrayUtils.extractBytes(raw, raw.length - (int)lastByte, (int)lastByte);
		for(byte b : temp) {
			if(b != lastByte) throw new Exception("Invalid Padding");
		}
		isValid = true;
		return isValid;
	}
	
	public static void main(String[] args) {
		byte[] raw = Ch9_PKCS7_Padding.padString("YELLOW SUBMARINE".getBytes(), 9);
		boolean isValidPadding;
		try {
			isValidPadding = Ch15_Validate_PKCS7.isValidPKCS7Padding(raw);
			System.out.println("isValidPadding :: " + isValidPadding);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("isValidPadding :: false");
		}
		
	}
}
