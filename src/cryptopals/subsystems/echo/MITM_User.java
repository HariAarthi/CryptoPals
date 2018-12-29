package cryptopals.subsystems.echo;

import java.math.BigInteger;

import cryptopals.protocols.KeyPair;

public class MITM_User extends User {

	public MITM_User(String name) {
		super(name);		
	}
	
	public byte[] getPublicKey() {
		return BigInteger.valueOf(32).toByteArray();
	}

}
