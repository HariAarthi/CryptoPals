package cryptopals.set1;

import java.util.Base64;

public class Ch1_HexToBase64 {
	
	public static String hexDecoding(String hexString) {
		StringBuilder strBuilder = new StringBuilder();
		String temp = null;
		for (int i = 0; i < hexString.length(); i += 2) {
			temp = (hexString.charAt(i) + "" + hexString.charAt(i + 1)); //pick hex value
			//System.out.println(temp);
			strBuilder.append((char)(Integer.parseInt(temp,16))); //convert it to integer & append the respective character to the string
		}
		return strBuilder.toString();
	}
	
	public static String convertToBase64(String hexString) {
		String decodedHex = hexDecoding(hexString);
		
		System.out.println(decodedHex);
		/*System.out.println(Base64.getEncoder().encode(strBuilder.toString().getBytes()));
		System.out.println(Base64.getEncoder().encodeToString(strBuilder.toString().getBytes()));*/
		return Base64.getEncoder().encodeToString(decodedHex.toString().getBytes()); // return encoded text of the string built above
	}
	
	public static void main(String[] args) {
		String hexString= "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";
		System.out.println("Using Shiro(for test) : " + new String(org.apache.shiro.codec.Base64.encode(hexDecoding(hexString).getBytes())));
		System.out.println(convertToBase64(hexString));		
	}

}
