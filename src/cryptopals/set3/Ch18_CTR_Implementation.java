package cryptopals.set3;

import javax.crypto.Cipher;

import org.apache.shiro.codec.Base64;

import cryptopals.set2.Ch10_CBC_Encryption;
import cryptopals.set2.Ch9_PKCS7_Padding;
import cryptopals.utils.ArrayUtils;
import cryptopals.utils.CipherUtils;
import cryptopals.utils.IncrementalNonce;

public class Ch18_CTR_Implementation {
	
	public static byte[] implementCTR(byte[] raw, byte[] key) {
		byte[] finalOutput = new byte[0];
		try {
			int nonceLength = 8;
			Cipher cipher = CipherUtils.getCipher("AES", "AES/ECB/NoPadding", true, key);
			int countBlocks = ArrayUtils.countBlocks(raw, key.length);
			IncrementalNonce nounce = new IncrementalNonce(key.length - 8,nonceLength);
			for(int i = 0; i < countBlocks; i++) {
				byte[] encNonce = cipher.doFinal(nounce.getNonceBytes()); //encrypt nonce bytes
				
				int noOfBytes = Math.min(key.length, raw.length - (i * key.length));
				byte[] aBlock = ArrayUtils.extractBytes(raw, (i * key.length), noOfBytes);//extract the respective block of bytes to be encrypted/decrypted
				
				byte[] cipherBlock = ArrayUtils.xorBlocks(encNonce, aBlock);//XOR encrypted nonce & block extracted above
				
				cipherBlock = ArrayUtils.extractBytes(cipherBlock, 0, key.length);
				
				finalOutput = ArrayUtils.mergeBytes(finalOutput, cipherBlock);//concatenate
				
				nounce.increment();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return finalOutput;
	}
	
	public static void main(String[] args) {
		try {
			String raw = "L77na/nrFsKvynd6HzOoG7GHTLXsTVu9qvY/2syLXzhPweyyMTJULu/6/kXX0KSvoOLSFQ==";			
			byte[] key = "YELLOW SUBMARINE".getBytes();
			byte[] finalOutput = Ch18_CTR_Implementation.implementCTR(Base64.decode(raw), key);
			System.out.println(new String(finalOutput));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
