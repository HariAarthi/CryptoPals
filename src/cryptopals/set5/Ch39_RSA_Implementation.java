package cryptopals.set5;

import java.math.BigInteger;
import java.security.SecureRandom;

//http://doctrina.org/How-RSA-Works-With-Examples.html

public class Ch39_RSA_Implementation {
	
	private BigInteger p, q, n, totient;
	private PublicKey pubKey = new PublicKey();
	private PrivateKey privateKey = new PrivateKey();
	
	private int primeBits = 1024;
	
	public PublicKey getPublicKey() {
		return pubKey;
	}
	
	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	private void generateN() {
		p = BigInteger.probablePrime(primeBits,new SecureRandom());
		p = p.abs();
		q = BigInteger.probablePrime(primeBits,new SecureRandom());
		q = q.abs();
		n = p.multiply(q);
	}
	
	private void calculateTotient() {
		BigInteger p1 = p.add(BigInteger.ONE.negate());//p.subtract(BigInteger.ONE); -- using subtract is NOT working! Dunno Y?!
		BigInteger q1 = q.add(BigInteger.ONE.negate());//q.subtract(BigInteger.ONE); -- using subtract is NOT working! Dunno Y?!
		totient = (p1).multiply(q1);
		totient = totient.abs();
	}
	
	private void calculatePublicKey() {
		pubKey.e = BigInteger.valueOf(3);
		pubKey.n = this.n;
	}
	
	public void calculatePrivateKey() {		
	    privateKey.d = pubKey.e.modInverse(totient);
	    privateKey.n = this.n;
	}
	
	public void generateKeyPair() {
		boolean shallExit = false;
		while(!shallExit) {
			try {
				generateN();
				calculateTotient();
				calculatePublicKey();
				calculatePrivateKey();
				shallExit = true;
			}catch(Exception e) {
				shallExit = false;
			}
		}
	}
	
	public BigInteger encrypt(BigInteger msg) {
		BigInteger cipher = msg.modPow(pubKey.e, n);
		return cipher;
	}
	
	public BigInteger decrypt(BigInteger cipher) {
		BigInteger decMsg = BigInteger.valueOf(0);
		decMsg = cipher.modPow(privateKey.d, n);
		return decMsg;
	}
	
	private void testRSA() {
		BigInteger msg = BigInteger.valueOf(42);
		System.out.println("Original Value : " + msg);
		BigInteger cipher = encrypt(msg);
		BigInteger decMsg = decrypt(cipher);
		System.out.println("Decrypted Value : " + decMsg);
		
		String textMsg = "This is test!";
		System.out.println("Original Text : " + textMsg);
		byte[] textBytes = textMsg.getBytes();
		msg = new BigInteger(textBytes);
		cipher = encrypt(msg);
		
		decMsg = decrypt(cipher);
		
		String deCryptedMsg = new String(decMsg.toByteArray());
		System.out.println("Decrypted Value : " + deCryptedMsg);	
		
	}
	
	public void performRSA() {
		generateKeyPair();		
		testRSA();
		
	}
	
	public static void main(String[] args) {
		new Ch39_RSA_Implementation().performRSA();
		//modInv();
	}

}

class PublicKey {
	public BigInteger e;
	public BigInteger n;
}

class PrivateKey {
	public BigInteger d;
	public BigInteger n;
}