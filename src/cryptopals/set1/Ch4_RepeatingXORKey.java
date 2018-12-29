package cryptopals.set1;

public class Ch4_RepeatingXORKey {

	public static String[] inputs = {"Burning 'em, if you ain't quick and nimble","I go crazy when I hear a cymbal"};
	
	public static String[] applyRepeatedXORKey(String[] inputString, byte[] xorKey) {
		String[] encryptedText = new String[inputString.length];
		for(int i = 0; i < inputString.length; i++) {
			byte[] temp = new byte[inputString[i].length()];
			int j = 0;
			for(byte b : inputString[i].getBytes()) {
				temp[j++] = (byte)(b ^ xorKey[i % 3]);
			}
			encryptedText[i] = new String(temp);
		}
		return encryptedText;
	}
	
	public static void main(String[] args) {
		String result[] = applyRepeatedXORKey(inputs,"ICE".getBytes());
		for(String temp : result)
			System.out.println(temp);
	}
}
