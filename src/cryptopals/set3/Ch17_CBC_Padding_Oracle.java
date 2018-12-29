package cryptopals.set3;

import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import org.apache.shiro.codec.Base64;

import cryptopals.set2.Ch9_PKCS7_Padding;
import cryptopals.utils.CipherUtils;
import cryptopals.utils.ArrayUtils;

/*
 * This page was helpful in implementing this attack : http://robertheaton.com/2013/07/29/padding-oracle-attack/
 */

public class Ch17_CBC_Padding_Oracle {
	
	public static String[] encodedStrings = new String[]{
										"MDAwMDAwTm93IHRoYXQgdGhlIHBhcnR5IGlzIGp1bXBpbmc=",
										"MDAwMDAxV2l0aCB0aGUgYmFzcyBraWNrZWQgaW4gYW5kIHRoZSBWZWdhJ3MgYXJlIHB1bXBpbic=",
										"MDAwMDAyUXVpY2sgdG8gdGhlIHBvaW50LCB0byB0aGUgcG9pbnQsIG5vIGZha2luZw==",
										"MDAwMDAzQ29va2luZyBNQydzIGxpa2UgYSBwb3VuZCBvZiBiYWNvbg==",
										"MDAwMDA0QnVybmluZyAnZW0sIGlmIHlvdSBhaW4ndCBxdWljayBhbmQgbmltYmxl",
										"MDAwMDA1SSBnbyBjcmF6eSB3aGVuIEkgaGVhciBhIGN5bWJhbA==",
										"MDAwMDA2QW5kIGEgaGlnaCBoYXQgd2l0aCBhIHNvdXBlZCB1cCB0ZW1wbw==",
										"MDAwMDA3SSdtIG9uIGEgcm9sbCwgaXQncyB0aW1lIHRvIGdvIHNvbG8=",
										"MDAwMDA4b2xsaW4nIGluIG15IGZpdmUgcG9pbnQgb2g=",
										"MDAwMDA5aXRoIG15IHJhZy10b3AgZG93biBzbyBteSBoYWlyIGNhbiBibG93"
										};
	
	public static byte[] randomKey = new byte[16];
	
	public static String pickRandomString() {
		return encodedStrings[new Random().nextInt(10)];
	}
	
	static {
		new Random().nextBytes(randomKey);
	}
	
	public static Encrypted encrypt(byte[] raw) throws Exception {
		byte[] paddedString = Ch9_PKCS7_Padding.padString(raw,randomKey.length);
		Cipher cipher = CipherUtils.getCipher("AES", "AES/CBC/NoPadding", true, randomKey);
		byte[] encryptedBytes = cipher.doFinal(paddedString);
		Encrypted encrypted = new Encrypted();
		encrypted.encryptedText = Arrays.copyOf(encryptedBytes, encryptedBytes.length);
		encrypted.iv = cipher.getIV();
		return encrypted;
	}
	
	public static byte[] crackBlocks(byte[] twoBlocks, Cipher cipher) {
		byte[] potentialBytes = new byte[0];
		byte[] temp, decryptedByte = null;
		byte actualByte = 0;
		for(int j = 0; potentialBytes.length < randomKey.length; j++) {
			if(potentialBytes.length > 0) twoBlocks = ArrayUtils.imposeInReverseAgainstFirstBlock(twoBlocks,potentialBytes);//replace first block of 2 blocks with already found bytes
			for(byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
				temp = ArrayUtils.replaceByteAtPosition(twoBlocks, randomKey.length - (potentialBytes.length + 1), b);
				try {
					decryptedByte = cipher.doFinal(temp);
					byte intermediateByte = (byte) (b ^ decryptedByte[decryptedByte.length - (potentialBytes.length + 1)]);
					actualByte = (byte) (intermediateByte ^ twoBlocks[(twoBlocks.length / 2) - (potentialBytes.length + 1)]);
					potentialBytes = ArrayUtils.mergeBytes(new byte[]{actualByte}, potentialBytes);
					break;
				}catch(Exception e) {
					System.out.println(e.getMessage());
				}			
			}
		}
		return potentialBytes;
	}
	
	/*
	 * Key take away of this exercise : intermediateByte is directly related to respective byte of 2nd cipher block.
	 * Idea behind this cracking techinque is to find the intermediateByte. If you have that, then use the respective byte of previous block to get the plainText.
	 */
	//it always work with Byte.MIN_VALUE!!!! Need to try this program against Bounty Castle API some day.
	public static byte[] paddingOracleAttack(Encrypted encrypted) throws Exception {				
		Cipher cipher = CipherUtils.getCipher("AES", "AES/CBC/NoPadding", false, randomKey);
		int noOfBlocks = ArrayUtils.countBlocks(encrypted.encryptedText, randomKey.length);
		byte[] finalPotentialBytes = new byte[0];
		for(int i = noOfBlocks; i > 1; i--) { // i > 1, bcoz ignoring first block of cipher text. Will be handled later.
			int from = (i - 2) * randomKey.length;
			from = (from < 0) ? 0 : from;
			byte[] lastTwoBlocks = ArrayUtils.extractBytes(encrypted.encryptedText, from, (randomKey.length * 2));
			byte[] potentialBytes = crackBlocks(lastTwoBlocks,cipher);
			finalPotentialBytes = ArrayUtils.mergeBytes(potentialBytes, finalPotentialBytes);
		}
		byte[] ivAndFirstBlock = ArrayUtils.mergeBytes(encrypted.iv, ArrayUtils.extractBytes(encrypted.encryptedText, 0, randomKey.length)); //concatenate iv+firstBlock. Do the same process again.
		byte[] firstBlockDecrypted = crackBlocks(ivAndFirstBlock,cipher);
		finalPotentialBytes = ArrayUtils.mergeBytes(firstBlockDecrypted, finalPotentialBytes);
		return finalPotentialBytes;
	}
	
	public static void main(String[] args) {
		try {
			String randomString = new String(Base64.decode(Ch17_CBC_Padding_Oracle.pickRandomString()));
			System.out.println(randomString);
			Encrypted encrypted = Ch17_CBC_Padding_Oracle.encrypt(randomString.getBytes());
			byte[] finalOutput = Ch17_CBC_Padding_Oracle.paddingOracleAttack(encrypted);
			finalOutput = Ch9_PKCS7_Padding.removePadding(finalOutput);
			System.out.println("final string:" + new String(finalOutput));
			if(randomString.equals(new String(finalOutput))) System.out.println("Success...Yo!!");
			else System.out.println("Failed....");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class Encrypted {
	public byte[] encryptedText;
	public byte[] iv;
}