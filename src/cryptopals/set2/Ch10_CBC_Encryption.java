package cryptopals.set2;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cryptopals.set1.Ch7_AES_ECB_Mode;
import cryptopals.utils.ArrayUtils;
import cryptopals.utils.CipherUtils;
import cryptopals.utils.ReadFile;

public class Ch10_CBC_Encryption {
	
	public static byte[] cipherTextBlock = {(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
											(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
											(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
											(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0};	
	
	public static byte[] perform_AES_CBC_Encryption(byte[] tempByteArray, byte[] keyBytes) throws Exception {
		int blockLength = keyBytes.length;
		int blockCount = tempByteArray.length / blockLength;
		byte[] combinedByteBlock = null, cipherTextBytes = null;
		byte[] localCipherTextBlock = cipherTextBlock; //using this as IV
		byte[] resultCipherText = new byte[tempByteArray.length];
		Cipher cipher = CipherUtils.getCipher("AES","AES/ECB/NoPadding",true,keyBytes);
		
		for(int currentBlock = 0; currentBlock < blockCount; currentBlock++) {
			byte[] blockBytes = ArrayUtils.extractBytes(tempByteArray, (currentBlock * blockLength), blockLength);
			combinedByteBlock = ArrayUtils.xorBlocks(blockBytes,localCipherTextBlock);
			cipherTextBytes = Ch7_AES_ECB_Mode.perform_AES_ECB(cipher, combinedByteBlock);
			localCipherTextBlock = cipherTextBytes;
			System.arraycopy(cipherTextBytes, 0, resultCipherText, (currentBlock * blockLength), blockLength);
		}		
		return resultCipherText;
	}
	
	public static byte[] perform_AES_CBC_Decryption(byte[] tempByteArray, byte[] keyBytes) throws Exception {
		int blockLength = keyBytes.length;
		int blockCount = tempByteArray.length / blockLength;
		byte[] intermediateCipher = null, plainTextBytes = null;
		byte[] localPlainTextBlock = cipherTextBlock; //using this as IV
		byte[] resultCipherText = new byte[tempByteArray.length];
		Cipher cipher = CipherUtils.getCipher("AES","AES/ECB/NoPadding",false,keyBytes);
		
		for(int currentBlock = 0; currentBlock < blockCount; currentBlock++) {
			byte[] blockBytes = ArrayUtils.extractBytes(tempByteArray, (currentBlock * blockLength), blockLength);//fetch a cipher block
			intermediateCipher = Ch7_AES_ECB_Mode.perform_AES_ECB(cipher, blockBytes); //get intermediate cipher
			plainTextBytes = ArrayUtils.xorBlocks(intermediateCipher,localPlainTextBlock);//XOR intermediateCipher with previous cipher block to get the plainText block
			localPlainTextBlock = blockBytes;
			System.arraycopy(plainTextBytes, 0, resultCipherText, (currentBlock * blockLength), blockLength);
		}		
		return resultCipherText;
	}
	
	public static byte[] decryptCBCEncryptedFile(String fileName, byte[] keyBytes) throws Exception {
		byte[] decodedBytes = ReadFile.readFile(fileName);
		byte[] decryptedBytes = perform_AES_CBC_Decryption(decodedBytes,keyBytes);
		return decryptedBytes;
	}
	
	public static String perform_AES_CBC_Java_Encryption(byte[] tempByteArray, byte[] keyBytes, byte[] cipherTextBlock) throws Exception {
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
		cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(cipherTextBlock));
		byte[] encrypted = cipher.doFinal(tempByteArray);
		return new String(encrypted);
    }
	
	public static String perform_AES_CBC_Java_Decryption(byte[] tempByteArray, byte[] keyBytes, byte[] cipherTextBlock) throws Exception {
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(cipherTextBlock));
		byte[] decrypted = cipher.doFinal(tempByteArray);
		return new String(decrypted);
    }
	
	public static void main(String[] args) {
		String plainText = "I'm killing your brain like a poisonous mushroom";
		byte[] plainTextBytes = plainText.getBytes();
		byte[] keyBytes = "YELLOW SUBMARINE".getBytes();
		try {
			//Test starts for my implementation of CBC enc/dec using EBC mode
			byte[] cipherText = perform_AES_CBC_Encryption(plainTextBytes,keyBytes);
			System.out.println(new String(cipherText));
			byte[] decryptedPlainText = perform_AES_CBC_Decryption(cipherText,keyBytes);
			System.out.println(new String(decryptedPlainText));
			//TEST ends
			
			/*
			 * My implementation of CBC decrypting using EBC mode. This is the actual problem given
			 */
			byte[] decryptedBytes = decryptCBCEncryptedFile("10.txt",keyBytes);
			System.out.println(new String(decryptedBytes));
			
			//Using Java's implementation of AES CBC
			//perform_AES_ECB_Java_Encryption(Ch9_PKCS7_Padding.padString("AAAA"+"QWE"+1,16).getBytes(),keyBytes);
			perform_AES_ECB_Java_Encryption("AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBB".getBytes(),keyBytes);
			perform_AES_ECB_Java_Encryption("CCCCCCCCCCCCCCCCBBBBBBBBBBBBBBBB".getBytes(),keyBytes);
			//System.out.println(perform_AES_CBC_Java_Decryption(ReadFile.readFile("10.txt"),keyBytes)); 
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String perform_AES_ECB_Java_Encryption(byte[] tempByteArray, byte[] keyBytes) throws Exception {
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
		cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, skeySpec/*, new IvParameterSpec(cipherTextBlock)*/);
		byte[] encrypted = cipher.doFinal(tempByteArray);
		return new String(encrypted);
    }
	
	public static String perform_AES_ECB_Padding_Java_Encryption(byte[] tempByteArray, byte[] keyBytes) throws Exception {
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
		cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, skeySpec/*, new IvParameterSpec(cipherTextBlock)*/);
		byte[] encrypted = cipher.doFinal(tempByteArray);
		return new String(encrypted);
    }

}
