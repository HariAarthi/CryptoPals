package cryptopals.set1;

import java.util.Base64;

public class Ch2_FixedXOR {
	
	public static String stringToHex(String string) {
		StringBuilder hexString = new StringBuilder();
		int ch;
		String temp;
		for(int i = 0; i < string.length(); i++) {
			ch = (int)string.charAt(i);
			//System.out.println(ch + " " + Integer.toHexString(ch));
			temp = Integer.toHexString(ch);
			hexString.append(temp.length() == 1 ? "0"+temp : temp);
		}		
		return hexString.toString();
	}
	
	public static String fixedXOR(String xorAgainst, String inputHex) {
		String result = null;
		String decodedHex = Ch1_HexToBase64.hexDecoding(inputHex);
		String decodeXorAgainst = Ch1_HexToBase64.hexDecoding(xorAgainst);
		byte tmp;int i = 0;
		byte[] decodedHexBytes = decodedHex.getBytes();
		byte[] resultBytes = new byte[decodedHexBytes.length];
		byte[] xorAgainstBytes = decodeXorAgainst.getBytes();
		for(byte b : decodedHexBytes) {
			resultBytes[i] = (byte)(b ^ xorAgainstBytes[i]);
			i++;
		}
		System.out.println(new String(resultBytes));
		return stringToHex(new String(resultBytes));
	}
	
	public static void main(String[] args) {
		String inputHex = "1c0111001f010100061a024b53535009181c";
	    String xorAgainst = "686974207468652062756c6c277320657965";
		System.out.println(fixedXOR(xorAgainst,inputHex));
	}

}
