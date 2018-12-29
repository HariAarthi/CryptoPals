package cryptopals.set1;

import java.util.StringTokenizer;

public class Ch5_RepeatingXORKey {

	public static String[] inputs = {"Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal"};
	
	public static String[] applyRepeatedXORKey(String[] inputString, byte[] xorKey) {
		String[] encryptedText = new String[inputString.length];
		int key = 0;
		for(int i = 0; i < inputString.length; i++) {
			byte[] temp = new byte[inputString[i].length()];
			int j = 0;
			for(byte b : inputString[i].getBytes()) {
				temp[j++] = (byte)(b ^ xorKey[key % 3]);
				key++;
			}
			encryptedText[i] = new String(temp);
		}
		return encryptedText;
	}
	
	public static void main(String[] args) {
		String[] result = applyRepeatedXORKey(inputs,"ICE".getBytes());
		StringTokenizer tokens = new StringTokenizer(result[0],"\n");
		while(tokens.hasMoreElements()) {
			System.out.println(Ch2_FixedXOR.stringToHex((String)tokens.nextElement()));
		}	
	}
}
