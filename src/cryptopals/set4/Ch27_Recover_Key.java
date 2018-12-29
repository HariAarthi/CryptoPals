package cryptopals.set4;

import cryptopals.set2.Ch10_CBC_Encryption;
import cryptopals.utils.ArrayUtils;

public class Ch27_Recover_Key {
	
	public static byte[] KEY = "YELLOW SUBMARINE".getBytes();
	public static int blockLength = KEY.length;
	
	public byte[] recoverKey(byte[] rawBytes) throws Exception {
		byte[] encBytes = Ch10_CBC_Encryption.perform_AES_CBC_Java_Encryption(rawBytes, KEY, KEY).getBytes();
		
		//replace second cipher block with 0
		int i = blockLength;
		int endPos = 2 * blockLength;
		for(; i < endPos; i++)
			encBytes = ArrayUtils.replaceByteAtPosition(encBytes, i, (byte)0);
		
		//replace third cipher block with first cipher block
		i = endPos;int j = 0;
		endPos = 3 * blockLength;
		for(; i < endPos; i++)
			encBytes = ArrayUtils.replaceByteAtPosition(encBytes, i, encBytes[j++]);
		
		byte[] decBytes = Ch10_CBC_Encryption.perform_AES_CBC_Java_Decryption(encBytes, KEY, KEY).getBytes();
		
		/*
		 * from the doctored plaintext XOR first & third block to get the key.
		 * How does this works?
		 * As second cipher block is full of zero & third cipher block is replaced with first cipherblock,
		 *  third plaintext block will be nothing but (0 XOR C1)
		 *  
		 *  first plaintext block will be (C1 XOR IV)
		 *  
		 *  so (C1 XOR IV) XOR (0 XOR C1) = IV = KEY
		 */
		byte[] p1 = ArrayUtils.extractBytes(decBytes, 0, blockLength);
		byte[] p2 = ArrayUtils.extractBytes(decBytes, 2 * blockLength, blockLength);
		byte[] key = ArrayUtils.xorBlocks(p1, p2);
		
		return key;
	}
	
	public static void main(String[] args) {
		try {
			Ch27_Recover_Key recoverKey = new Ch27_Recover_Key();
			String plainText="Asathoma Satgamaya Tamasoma Jyothirgamaya Mrid..";
			byte[] rawBytes = plainText.getBytes();
			byte[] recoveredKey = recoverKey.recoverKey(rawBytes);
			System.out.println("KEY : " + new String(recoveredKey));		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}