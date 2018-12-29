package cryptopals.set1;

import java.util.Base64;
//import java.util.Base64.Encoder;

public class HexToBase64 {

	private static String hex = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";
	
	public static String convertHexToBase64() {
		/*for(byte bb : hex.getBytes() )
			System.out.println(bb);*/
		byte[] encodedBytes = Base64.getEncoder().encode(hex.getBytes());
		System.out.println(new String(encodedBytes));
		return "";
	}
	
	public static void main(String[] args) {
		convertHexToBase64();
	}
}
