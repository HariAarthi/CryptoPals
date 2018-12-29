package cryptopals.utils;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtils {
	
	public static byte[] randomIV = null;
	
	public static byte[] getRandomIV(int length) {
		if(randomIV != null) return randomIV;
		randomIV = new byte[length];
		new Random().nextBytes(randomIV);
		return randomIV;
	}
	
	public static Cipher getCipher(String algo, String mode, boolean isEncryption, byte[] key) throws Exception {
		Cipher cipher = null;
		SecretKeySpec skeySpec = new SecretKeySpec(key, algo);
		if(mode.contains("CBC")) {
			cipher = Cipher.getInstance(mode);
			key = (key == null) ? getRandomIV(16) : key;
			cipher.init(isEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, new SecretKeySpec(skeySpec.getEncoded(), algo),new IvParameterSpec(getRandomIV(16)));
		} else if(mode.contains("ECB")) {
			key = (key == null) ? getRandomIV(16) : key;
			cipher = Cipher.getInstance(mode);
			cipher.init(isEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, new SecretKeySpec(skeySpec.getEncoded(), algo));
		}		
		return cipher;
	}

}
