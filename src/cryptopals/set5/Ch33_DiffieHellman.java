package cryptopals.set5;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import cryptopals.protocols.KeyPair;
import cryptopals.set4.Ch28_SHA1;
import cryptopals.subsystems.echo.IUser;

public class Ch33_DiffieHellman {

	private BigInteger p;
	private BigInteger g;
	//private int a, b, A, B;
	
	public Ch33_DiffieHellman() {}
	
	public Ch33_DiffieHellman(int p,int g) {
		this.p = BigInteger.valueOf(p);
		this.g = BigInteger.valueOf(g);
	}
	
	public BigInteger getP() {
		return p;
	}
	
	public BigInteger getG() {
		return g;
	}
	
	public Ch33_DiffieHellman(BigInteger pInput, BigInteger gInput) {
		this.p = pInput;
		this.g = gInput;
	}
	
	public BigInteger getPrivateKey() {
		BigInteger randomNo = new BigInteger(p.bitLength(),new SecureRandom());
		return randomNo;
	}
	
	public BigInteger getPrivateKey(IUser user) {
		BigInteger randomNo = new BigInteger(user.getPrivateKey());
		return randomNo;
	}
	
	public KeyPair generateKeyPair(IUser user) {
		BigInteger randomNo = (user == null) ? getPrivateKey() : getPrivateKey(user);
		BigInteger privateKey = randomNo.mod(p);
		BigInteger publicKey = g.modPow(privateKey,p);
		return new KeyPair(privateKey,publicKey);
	}
	
	public byte[] sessionKey(KeyPair keyPair, byte[] publicKeyBytes) {
		BigInteger somePublicKey = new BigInteger(publicKeyBytes);
		BigInteger s = somePublicKey.modPow(keyPair.getPrivateKey(), p);
		return s.toByteArray();//hashTheKey(s);
	}
	
	private byte[] hashTheKey(BigInteger key) {
		return Arrays.copyOf(new Ch28_SHA1().encode(key.toByteArray()), 16);
	}
	
	public boolean validateSessionKey(byte[] sessionKey1, byte[] sessionKey2) {		
		return Arrays.equals(sessionKey1,sessionKey2);
	}
	
	private static void simpleCase() {
		//simple case
		Ch33_DiffieHellman dh = new Ch33_DiffieHellman(37,5);
		KeyPair keyPairA = dh.generateKeyPair(null);
		KeyPair keyPairB = dh.generateKeyPair(null);
		byte[] sessionKey1 = dh.sessionKey(keyPairA, BigInteger.valueOf(37).toByteArray()/*keyPairB.getPublicKey().toByteArray()*/);
		byte[] sessionKey2 = dh.sessionKey(keyPairB, BigInteger.valueOf(37).toByteArray()/*keyPairA.getPublicKey().toByteArray()*/);
		System.out.println(dh.validateSessionKey(sessionKey1,sessionKey2));				
	}
	
	private static void hardCase() {
		String EX_33_P_STR = "2410312426921032588552076022197566074856950548502459942654116941958108831682612228890093858261341614673227141477904012196503648957050582631942730706805009223062734745341073406696246014589361659774041027169249453200378729434170325843778659198143763193776859869524088940195577346119843545301547043747207749969763750084308926339295559968882457872412993810129130294592999947926365264059284647209730384947211681434464714438488520940127459844288859336526896320919633919";
		
		String EX_33_P_HEX = "ffffffffffffffffc90fdaa22168c234c4c6628b80dc1cd129024e088a67cc74020bbea63b139b22514a08798e3404ddef9519b3cd3a431b302b0a6df25f14374fe1356d6d51c245e485b576625e7ec6f44c42e9a637ed6b0bff5cb6f406b7edee386bfb5a899fa5ae9f24117c4b1fe649286651ece45b3dc2007cb8a163bf0598da48361c55d39a69163fa8fd24cf5f83655d23dca3ad961c62f356208552bb9ed529077096966d670c354e4abc9804f1746c08ca237327ffffffffffffffff";
		Ch33_DiffieHellman dh = new Ch33_DiffieHellman(new BigInteger(EX_33_P_STR),BigInteger.valueOf(2));
		KeyPair keyPairA = dh.generateKeyPair(null);
		KeyPair keyPairB = dh.generateKeyPair(null);
		byte[] sessionKey1 = dh.sessionKey(keyPairA, keyPairB.getPublicKey().toByteArray());
		byte[] sessionKey2 = dh.sessionKey(keyPairB, keyPairA.getPublicKey().toByteArray());
		System.out.println(dh.validateSessionKey(sessionKey1,sessionKey2));
				
	}
	
	public static void main(String[] args) {
		System.out.println("Simple case..");
		simpleCase();
		System.out.println("Hard case..");
		hardCase();
	}
}
