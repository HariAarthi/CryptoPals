package cryptopals.set2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import org.apache.shiro.codec.Base64;

import cryptopals.utils.ArrayUtils;
import cryptopals.utils.CipherUtils;

public class Ch12_ECB_Decryption {

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
	
	public static int trailAndError(byte[] input, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
		cipher.doFinal(input);
		return cipher.getBlockSize();
	}
	
	public static byte[] encryptionOracle(byte[] input,byte[] suffix) throws IllegalBlockSizeException, BadPaddingException {
		/*byte[] temp = new byte[input.length+decodedUnknownString.length];
		temp = ArrayUtils.mergeBytes(input, decodedUnknownString);*/
		byte[] temp = (suffix == null) ? Ch9_PKCS7_Padding.padString(input, blockSize) : ArrayUtils.mergeBytes(input, suffix);//
		temp = cipher.doFinal(temp);
		return temp;
	}
	
	public static int detectBlockSize(byte[] input, byte[] randomKey) throws Exception {
		int cipherBlockSize = 0;
		StringBuilder yourString = new StringBuilder();
		boolean blockSizeFound = false;
		cipher = CipherUtils.getCipher("AES","AES/ECB/NoPadding",true,randomKey);
		while(!blockSizeFound) {
			try {
				yourString.append("A");
				byte[] mergedBytes = new byte[yourString.length()+input.length];
				System.arraycopy(yourString.toString().getBytes(), 0, mergedBytes, 0, yourString.length());
				System.arraycopy(input, 0, mergedBytes, yourString.length(), input.length);
				//System.out.println(input.length + " -- " + yourString.length());
				cipherBlockSize = trailAndError(mergedBytes,cipher);
				fillInBytes = yourString.length();
				System.out.println("fillInBytes : "+fillInBytes);
				blockSizeFound = true;
			} catch (IllegalBlockSizeException e) {} catch (BadPaddingException e) {}
		}
		return cipherBlockSize;
	}
	
	public static byte[] get1ByteShortString(int forSize) {
		byte[] temp = new byte[forSize];
		Arrays.fill(temp, (byte)0);
		return temp;
	}
	
	public static Map<String,Integer> dictionary = new HashMap<String,Integer>();
	
	/*public static void buildDictionary(String baseString) throws Exception {
		//StringBuilder build = new StringBuilder(baseString);
		cipher = CipherUtils.getCipher("AES","AES/ECB/NoPadding",false,randomKey);
		byte[] encryptedText = null;
		for(char alphabet = 'A'; alphabet <= 'Z';alphabet++) {
		    encryptedText = cipher.doFinal((baseString+alphabet).getBytes());
		    dictionary.put(alphabet+"", encryptedText);
		}
		for(char alphabet = 'a'; alphabet <= 'z';alphabet++) {
		    encryptedText = cipher.doFinal((baseString+alphabet).getBytes());
		    dictionary.put(alphabet+"", encryptedText);
		}
		for(int number =0; number < 10;number++) {
		    encryptedText = cipher.doFinal((baseString+number).getBytes());
		    dictionary.put(number+"", encryptedText);
		}
	}
	*/
	
	public static byte findNextByte(byte[] knownBytes, byte[] knownSuffix, int count) throws IllegalBlockSizeException, BadPaddingException, Exception {
		byte[] oneByteShort = get1ByteShortString(Ch9_PKCS7_Padding.missingBytes(knownBytes.length, blockSize)+count+1/*1+(knownSuffix.length-1)*/);
		oneByteShort = ArrayUtils.mergeBytes(oneByteShort, decodedUnknownString);
		byte[] encryptedText = encryptionOracle(oneByteShort,null);
		byte[] expectedCipherText = ArrayUtils.extractBytes(encryptedText, encryptedText.length - blockSize, blockSize);
		
		oneByteShort = get1ByteShortString(Ch9_PKCS7_Padding.missingBytes(knownSuffix.length, blockSize)+1);
		//knownSuffix = ArrayUtils.mergeBytes(oneByteShort, knownSuffix);
		//if(knownSuffix.length < 16)
			//knownSuffix = Ch9_PKCS7_Padding.padString(knownSuffix, blockSize);
		
		for(int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {	
			//byte[] temp = ArrayUtils.mergeBytes( new byte[]{(byte)i},knownSuffix );
			byte[] temp = ArrayUtils.replaceByteAtPosition(knownSuffix,0,(byte)i);
			byte[] tempEncText = encryptionOracle(temp,null);
			tempEncText = ArrayUtils.extractBytes(tempEncText, tempEncText.length - blockSize, blockSize);
			//tempEncText = ArrayUtils.extractBytes(tempEncText, tempEncText.length - blockSize, blockSize);
			if(Arrays.equals(expectedCipherText,tempEncText)) {
				return (byte)i;//System.out.println("matches : " + i);
			}
			//dictionary.put(new String(tempEncText),i);
		}
		throw new Exception("Unable to find the match. Breaking here..!!");
		/*char ch = (char)dictionary.get(new String(encryptedText)).byteValue();
		System.out.println(ch);*/
		//return 0;
	}
	
	public static void aes128ECB(byte[] input, byte[] randomKey) throws Exception {
		blockSize = detectBlockSize(input,randomKey);
	//	System.out.println("Cipher block size detected : " + cipherBlockSize);
		int returnMode = Ch11_ECB_CBC_DetectionOracle.detectAESMode(input);
		System.out.println("Mode used : " + ((returnMode == 0) ? "ECB mode" : "CBC mode"));
		
		byte[] decodedBytes = Base64.decode(unknownString);
		blockSize = detectBlockSize(decodedBytes,randomKey);
		System.out.println(blockSize);
		//decodedBytes = Ch9_PKCS7_Padding.padString(decodedBytes, blockSize);
		//byte[] padd = new byte[]{0,0,0,0,0};
		//System.out.println(new String(decodedBytes));
		//char ch = Character.
		byte[] returnByte = new byte[]{0};//findNextByte(decodedBytes,new byte[]{0});
		byte[] tempBytes = null;
		int count = 0;
		try {
			while(returnByte != null) {
				byte[] extractedBytes = returnByte;//(returnByte.length > 16) ? ArrayUtils.extractBytes(returnByte, 0, blockSize - 1) : returnByte;
				byte tempByte = findNextByte(decodedBytes,extractedBytes,count);
				tempBytes = (count == 0) ? new byte[]{tempByte} : ArrayUtils.mergeBytes(new byte[]{tempByte},tempBytes);
				returnByte = ArrayUtils.mergeBytes(new byte[]{0},tempBytes);
				count++;
				System.out.println();
			}			
		}catch(Exception e) {}
		System.out.println(new String(returnByte));
		//buildDictionary(get1ByteShortString("A",blockSize));
	}
	
	public static byte[] getDictionaryEntry(byte[] knownSuffix, int blockSize) {
	    byte[] dictionaryentry = new byte[blockSize];
	    System.arraycopy(knownSuffix, 0, dictionaryentry, 1, Math.min(knownSuffix.length, dictionaryentry.length - 1));

	    if (knownSuffix.length > blockSize)
	      return dictionaryentry;

	    int from = Math.min(knownSuffix.length+1, blockSize);
	    Arrays.fill(dictionaryentry, from, blockSize, (byte) (blockSize - knownSuffix.length - 1));
	    return dictionaryentry;
	  }
	
	public static byte guessKeyByte(byte[] dictionaryEntry, byte[] expectedCipherText, Cipher cipher) throws Exception {
		for(byte guess = Byte.MIN_VALUE; guess <= Byte.MAX_VALUE; guess++) {
			dictionaryEntry = ArrayUtils.replaceByteAtPosition(dictionaryEntry, 0, guess);
			byte[] cipherBytes = cipher.doFinal(dictionaryEntry);
			if(Arrays.equals(cipherBytes, expectedCipherText)) return guess;
		}
		throw new Exception("No Match found");
	}
	
	public static byte[] crackAES128(byte[] decodedBytes) throws Exception {
		int prefixSize = Ch9_PKCS7_Padding.missingBytes(decodedBytes.length, randomKey.length);
		byte[] input = new byte[prefixSize+1];
		int extractFrom = 144;//YES...a hack
		byte[] knownSuffix = new byte[0];
		while(knownSuffix.length < 138) {
			byte[] tempInput = ArrayUtils.mergeBytes(input, decodedBytes);
			byte[] encryptedBytes = cipher.doFinal(Ch9_PKCS7_Padding.padString(tempInput, blockSize));
			byte[] expectedCipherText = ArrayUtils.extractBytes(encryptedBytes, extractFrom, randomKey.length);
			
			byte[] dictionaryEntry = getDictionaryEntry(knownSuffix,blockSize);
			byte keyByte = guessKeyByte(dictionaryEntry, expectedCipherText, cipher);
			knownSuffix = ArrayUtils.mergeBytes(new byte[]{keyByte}, knownSuffix);
			input = new byte[input.length+1];
		}
		return knownSuffix;
	}
	
	public static void main(String[] args) {
		try {
			//Ch12_ECB_Decryption.aes128ECB(unknownString.getBytes(), randomKey);
			byte[] decodedBytes = Base64.decode(unknownString);
			blockSize = detectBlockSize(decodedBytes,randomKey);
			byte[] crackedText = Ch12_ECB_Decryption.crackAES128(decodedBytes);
			System.out.println(new String(crackedText));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
