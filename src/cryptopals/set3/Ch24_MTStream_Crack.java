package cryptopals.set3;

import java.security.SecureRandom;
import java.util.Date;

import cryptopals.utils.ArrayUtils;

public class Ch24_MTStream_Crack {
	
	Ch21_MersenneTwister_PRNG prng = null;
	private byte[] key = null;
	private String signature = "password token";
	
	public Ch24_MTStream_Crack() {}
	
	public void setMT(Ch21_MersenneTwister_PRNG prng) {
		this.prng = prng;
	}
	
	private void seedMT(int seed) {
		prng = new Ch21_MersenneTwister_PRNG(seed);
	}
	
	/*public byte[] getKey(int requiredLength) {
		byte tempRandomNos;
		key = new byte[requiredLength];
		for(int i = 0; i <  requiredLength; i++) {
			tempRandomNos = (byte)prng.getRandomInt();
			key[i] = tempRandomNos;
		}
		return key;
	}*/
	
	public byte[] getKey(int length, int seed) {
		seedMT(seed);
		byte[] key = new byte[length];
		for(int i = 0; i < length; i++)
			key[i] = (byte)prng.getRandomInt();
		
		return key;
	}
	
	public byte[] encrypt(byte[] rawBytes, int seed) {
		byte[] key = getKey(rawBytes.length,seed);
		
		byte[] encryptedBytes = new byte[rawBytes.length];
		for(int i = 0; i < key.length; i++) {
			encryptedBytes[i] = (byte)(rawBytes[i] ^ key[i]);
		}
		return encryptedBytes;
	}
	
	public byte[] decrypt(byte[] encryptedBytes, int seed) {
		byte[] key = getKey(encryptedBytes.length,seed);
		byte[] decryptedBytes = new byte[encryptedBytes.length];
		for(int i = 0; i < key.length; i++) {
			decryptedBytes[i] = (byte)(encryptedBytes[i] ^ key[i]);
		}
		return decryptedBytes;
	}
	
	public int get16BitSeed() {
		int RELEVANT_LAST_16_BITS = 0xffff;
		int seed = new SecureRandom().nextInt();
		return (seed & RELEVANT_LAST_16_BITS);
	}
	
	private byte[] getRandomBytes() {
	    SecureRandom realRandomGenerator = new SecureRandom();
	    byte[] result = new byte[Math.abs(realRandomGenerator.nextInt()) % 256];
	    realRandomGenerator.nextBytes(result);

	    return result;
	}
	
	private byte[] generatePasswordToken() {
		ArrayUtils utils = new ArrayUtils();
		byte[] temp = utils.mergeBytes(getRandomBytes(), signature.getBytes());
		int seed  = get16BitSeed();
		System.out.println("Seed used for token generation : " + seed);
		byte[] token = encrypt(temp,seed);
		return token;
	}
	
	private boolean isPasswordToken(byte[] token) {
		int i = 0;
		boolean notToken = true;
		byte[] temp;
		String tempString;
		for(; i < 65535; i++) {
			temp = decrypt(token,i);
			tempString = new String(temp);
			if(new String(temp).contains(signature)) {
				notToken = true;
				System.out.println("Seed used for token : " + i);
				break;
			}
		}
		return notToken;
	}
	
	public void startTest() {
		
		//part 1
		System.out.println("PART 1");
		String text = "BAAAAC";
		//byte[] key = this.getKey(text.length());
		int seed = get16BitSeed();
		System.out.println("Actual seed used : " + seed);
		byte[] encBytes = this.encrypt(/*Base64.decode(text)*/text.getBytes(),seed);
		byte[] decBytes = this.decrypt(encBytes, seed);
		System.out.println("Decrypted : " + new String(decBytes));
		
		//part 2
		System.out.println("PART 2");
		int i = 0;
		for(i = 0; i <  65535; i++) {
			decBytes = this.decrypt(encBytes, i);
			if(text.equals(new String(decBytes))) {
				System.out.println("decrypted text : " + new String(decBytes));
				break;				
			}
		}
		System.out.println("Seed identified : " + i);
		
		//part 3
		System.out.println("PART 3");
		isPasswordToken(generatePasswordToken());
	}
	
	public static void main(String[] args) {
		Ch24_MTStream_Crack crack = new Ch24_MTStream_Crack();
		crack.startTest();
	}

}
