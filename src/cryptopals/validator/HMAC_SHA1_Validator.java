package cryptopals.validator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cryptopals.utils.ArrayUtils;

public class HMAC_SHA1_Validator {
	
	private static byte[] keyBytes =  new ArrayUtils().createInitializedArray(16, (byte)1);

	public static byte[] calculateSignature(String fileName) {
		try {

            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(fileName.getBytes());
            //byte[] hexBytes = org.apache.shiro.codec.Base64.decode(rawHmac);

            return rawHmac;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	public static boolean validatorWithTimeLeak_Insecure(byte[] hmac1, byte[] hmac2) throws InterruptedException {
		boolean isMatch = true;
		int i = 0;
		//if(hmac1.length != hmac2.length) return false;
		for(; i < hmac1.length; i++) {
			if(hmac1[i] != hmac2[i]) 
				return false;
			Thread.sleep(50);
		}
		System.out.println("Total time slept : " + ((i+1) * 50));
		return isMatch;
	}

}
