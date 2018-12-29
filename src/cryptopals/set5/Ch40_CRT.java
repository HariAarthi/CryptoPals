package cryptopals.set5;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//Refer : https://crypto.stackexchange.com/questions/6713/low-public-exponent-attack-for-rsa

class Sample {
	public BigInteger cipher;
	public PublicKey pubKey;
}

public class Ch40_CRT {
	
	private Sample[] samples =  new Sample[3];
	private BigInteger msg = BigInteger.valueOf(256);
	
	private void collectSamples() {
		for(int i = 0; i < 3; i++) {
			Sample sample = new Sample();
			Ch39_RSA_Implementation rsa = new Ch39_RSA_Implementation();
			rsa.generateKeyPair();
			sample.cipher = rsa.encrypt(msg);
			sample.pubKey = rsa.getPublicKey();
			samples[i] = sample;
		}
	}
	
	private BigDecimal initiateCRTAttack() {
		BigInteger t[] = new BigInteger[samples.length];
		BigInteger tempN = BigInteger.ONE;
		for(int i = 0; i < samples.length; i++) {
			t[i]= samples[i].cipher;
			tempN = BigInteger.ONE;
			for(int j = 0; j < samples.length; j++) {
				if(i == j) continue;
				tempN = tempN.multiply(samples[j].pubKey.n);
			}
			tempN = tempN.multiply( tempN.modInverse(samples[i].pubKey.n) );
			t[i] = t[i].multiply(tempN);
		}
		
		BigInteger finalT = BigInteger.ZERO, finalN = BigInteger.ONE;
		for(int i = 0; i < samples.length; i++) {
			finalT = finalT.add(t[i]);
			finalN = finalN.multiply(samples[i].pubKey.n);
		}
		BigInteger c = finalT.mod(finalN);
		double msgDouble = Math.cbrt(c.intValue());
		return BigDecimal.valueOf(msgDouble);
	}
	
	public void crackRSAUsingCRT() {
		collectSamples();
		BigDecimal potentialMsg = initiateCRTAttack();
		BigInteger finalMsg = potentialMsg.toBigInteger();
		System.out.println("Final msg : " + finalMsg);
		System.out.println("Is Success : " + (finalMsg.equals(msg) ? "Yes" : "No"));
	}
	
	public static void main(String[] args) {
		new Ch40_CRT().crackRSAUsingCRT();
		/*BigDecimal bigD = new BigDecimal(8000000000.500000000000000000000000000000);		
		BigInteger bi = bigD.toBigInteger();
		bi.intValueExact();
		bi.intValue();*/
	}

}
