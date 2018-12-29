package cryptopals.set2;

import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;

import org.apache.shiro.codec.Base64;

import cryptopals.utils.ArrayUtils;
import cryptopals.utils.CipherUtils;

public class Ch14_ECB_Harder {

	public static String unknownString = "Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkg"+/*"\n"+*/
			 "aGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBq"+/*"\n"+*/
			 "dXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUg"+/*"\n"+*/
			 "YnkK";

	public static byte[] decodedUnknownString = Base64.decode(unknownString);

	public static byte[] randomKey = new byte[16];
	
	public static StringBuilder yourString = new StringBuilder();
	
	public static int fillInBytes = 0;
	
	public static int blockSize = 0;
	
	public static Cipher cipher = null;
	
	static{
		new Random().nextBytes(randomKey);
	}

	public static byte[] randomBytes() {
		byte[] randomBytes = new byte[new Random().nextInt(50)]; //to limit size to 50. sometimes leads to OOM exception.
		new Random().nextBytes(randomBytes);
		return randomBytes;
	}
	
	public static byte[] preparePrefix(int decodedBytesLength, int knownSuffixLength) {
		byte[] randomPrefix = randomBytes();
		blockSize = randomKey.length;
		int prefixSize = Ch9_PKCS7_Padding.missingBytes(randomPrefix.length + decodedBytesLength, randomKey.length); //enough to get the prefix bytes to fill the gap of randomBytes
		byte[] input = new byte[prefixSize+1+(knownSuffixLength % randomKey.length)];
		
		input = ArrayUtils.mergeBytes(randomPrefix, input); //merge randomPrefix & attacker controlled prefix
		
		return input;
	}
	
	public static byte[] crackAES128(byte[] decodedBytes) throws Exception {
		
		int extractFrom = 0;
		byte[] knownSuffix = new byte[0];
		try {
			while(knownSuffix.length < decodedBytes.length) {
				byte[] input = preparePrefix(decodedBytes.length,(knownSuffix.length));
				byte[] tempInput = ArrayUtils.mergeBytes(input, decodedBytes);
				extractFrom = (ArrayUtils.countBlocks(tempInput, blockSize) - ((knownSuffix.length / blockSize) + 1)) * blockSize;
				byte[] encryptedBytes = cipher.doFinal(Ch9_PKCS7_Padding.padString(tempInput, blockSize));
				byte[] expectedCipherText = ArrayUtils.extractBytes(encryptedBytes, extractFrom, randomKey.length);
				
				byte[] dictionaryEntry = Ch12_ECB_Decryption.getDictionaryEntry(knownSuffix,blockSize);
				byte keyByte = guessKeyByte(dictionaryEntry, expectedCipherText,cipher);
				knownSuffix = ArrayUtils.mergeBytes(new byte[]{keyByte}, knownSuffix);
			}			
		}catch(Exception e) {}
		
		return knownSuffix;
	}
	
	public static byte guessKeyByte(byte[] dictionaryEntry, byte[] expectedCipherText, Cipher cipher) throws Exception {
		for(byte guess = Byte.MIN_VALUE; guess < Byte.MAX_VALUE; guess++) {
			dictionaryEntry = ArrayUtils.replaceByteAtPosition(dictionaryEntry, 0, guess);
			byte[] cipherBytes = cipher.doFinal(dictionaryEntry);
			if(Arrays.equals(cipherBytes, expectedCipherText)) return guess;
		}
		throw new Exception("No Match found");
	}
	
	public static void main(String[] args) {
		try {
			cipher = CipherUtils.getCipher("AES","AES/ECB/NoPadding",true,randomKey);
			byte[] crackedText = crackAES128(decodedUnknownString);
			System.out.println(new String(crackedText));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
