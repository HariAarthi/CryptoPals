package cryptopals.set1;

import java.util.Arrays;
import java.util.List;

import cryptopals.utils.ArrayUtils;
import cryptopals.utils.ReadFile;

public class Ch8_DetectEncryptedText {
	
	public static int countRepetitions(byte[] tempByteArray, int blockLength) {
		int matches = 0, from = 0, arrayLength = tempByteArray.length;
		int blockCount = tempByteArray.length / blockLength;
		for(int currentBlock = 0; currentBlock < blockCount; currentBlock++) {
			byte[] initialArray = ArrayUtils.extractBytes(tempByteArray, (currentBlock * blockLength), blockLength);
			byte[] tempArray = null;
			from = (currentBlock * blockLength) + blockLength;
			while(from < arrayLength) {
				tempArray = ArrayUtils.extractBytes(tempByteArray, from, blockLength);
				if(Arrays.equals(initialArray, tempArray)) matches++;
				from += blockLength;
			}
		}		
		return matches;
	}
	
	public static String findECBCipher(List<String> cipherList, int blockLength) {
		String result = null;
		byte[] tempByteArray = null;
		int maxMatches = -1, matches = 0, lineNumber = 0, count = 0;
		for(String cipher : cipherList) {
			tempByteArray = Ch1_HexToBase64.hexDecoding(cipher).getBytes();
			count++;
			matches = countRepetitions(tempByteArray, blockLength);
			if(matches > maxMatches) {
				maxMatches = matches;
				result = cipher;
				lineNumber = count;
			}
		}
		//System.out.println("line number : " + lineNumber);
		return result;
	}

	public static void main(String[] args) {
		List<String> stringList = ReadFile.getFile("8.txt");
		String temp = findECBCipher(stringList, 16);
		System.out.println("Possible ECB Cipher : ");
		System.out.println(temp);
	}
}
