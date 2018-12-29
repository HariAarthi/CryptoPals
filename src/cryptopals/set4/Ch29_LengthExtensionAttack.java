package cryptopals.set4;

import java.util.Arrays;

import cryptopals.utils.ArrayUtils;

/*
 * Good articles for reference : 
 * https://www.whitehatsec.com/blog/hash-length-extension-attacks/ - talks about SHA1
 * 
 * https://blog.skullsecurity.org/2012/everything-you-need-to-know-about-hash-length-extension-attacks - talks about MD5
 * 
 */
public class Ch29_LengthExtensionAttack {
	
	public static int convertByteToInt(byte[] bytes) {
		int number = 0, count = 0;
		for(byte b : bytes) {
			number = ((number << 8) + (b & 0xFF));
			count++;
		}
		return number;
	}
	
	private static byte[] simulateFullForgedMessage(int keyLength, byte[] originalMessage, byte[] suffix) {
	    int[] paddedMessage = new Ch28_SHA1().toPaddedIntegerArray(ArrayUtils.join(new byte[keyLength], originalMessage), 0);
	    int[] forgedMessageIncludingKey = ArrayUtils.join(paddedMessage, ArrayUtils.bitewiseToIntegers(suffix));
	    byte[] forgedMessageIncludingKeyInBytes = ArrayUtils.bitewiseToBytes(forgedMessageIncludingKey);
	    return forgedMessageIncludingKeyInBytes;
	  }
	
	public byte[] forgedMessage(byte[] message,byte[] suffix) {
		//ArrayUtils.createInitializedArray(22, (byte)0);
	    byte[] alteredsuffix = ArrayUtils.mergeBytes(new byte[] {0}, suffix);//no clue why that 0 is added
	    byte[] suffixMAC = null;
	    //length-extension attack
		Ch28_SHA1 sha1 = new Ch28_SHA1();
		byte[] originalMAC = sha1.encode(message);
		
		int intermediateA = Ch29_LengthExtensionAttack.convertByteToInt(ArrayUtils.extractBytes(originalMAC, 0, 4));
		int intermediateB = Ch29_LengthExtensionAttack.convertByteToInt(ArrayUtils.extractBytes(originalMAC, 4, 4));
		int intermediateC = Ch29_LengthExtensionAttack.convertByteToInt(ArrayUtils.extractBytes(originalMAC, 8, 4));
		int intermediateD = Ch29_LengthExtensionAttack.convertByteToInt(ArrayUtils.extractBytes(originalMAC, 12, 4));
		int intermediateE = Ch29_LengthExtensionAttack.convertByteToInt(ArrayUtils.extractBytes(originalMAC, 16, 4));
		
		for(int i = 1; i < 1024; i++) {
			byte[] fmIncludingKey = simulateFullForgedMessage(i, message, alteredsuffix);
			int addToLength = (fmIncludingKey.length * 8) - (alteredsuffix.length * 8);
			suffixMAC = sha1.encode(alteredsuffix, intermediateA, intermediateB, intermediateC, intermediateD, intermediateE,addToLength);
   
			byte[] fmWithoutKey = ArrayUtils.extractBytes(fmIncludingKey, i, fmIncludingKey.length - i);
			byte[] macFromAuthenticator = sha1.encode(fmWithoutKey);
			boolean isSame = Arrays.equals(suffixMAC, macFromAuthenticator);
			if(isSame) {
				System.out.println("Key length : " + i);//successfully forged message
				break;
			}
		}
		return suffixMAC;
	}
	
	public static void main(String args[]) {
		byte[] message = "comment1=cooking%20MCs;userdata=foo;comment2=%20like%20a%20pound%20of%20bacon".getBytes();
		byte[] suffix = ";admin=true".getBytes();
	    byte[] forgedMAC = new Ch29_LengthExtensionAttack().forgedMessage(message, suffix);
	    if(forgedMAC != null) System.out.println("Success");
	    else System.out.println("Failure");
	}

}
