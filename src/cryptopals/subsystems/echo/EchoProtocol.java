package cryptopals.subsystems.echo;

import java.math.BigInteger;
import java.util.Arrays;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cryptopals.protocols.KeyPair;
import cryptopals.set2.Ch10_CBC_Encryption;
import cryptopals.set4.Ch28_SHA1;
import cryptopals.set5.Ch33_DiffieHellman;
import cryptopals.utils.ArrayUtils;
import cryptopals.utils.CipherUtils;

public class EchoProtocol implements IProtocol {
	
	private IMessage message;
	private IUser fromUser, toUser;
	private BigInteger p, g;
	private Ch33_DiffieHellman dh;
	private IConnection echoConnection;
	private KeyPair keyPair;
	public KeyExchangeInfo keyExInfo;
	private Token token;

	public byte[] IV = CipherUtils.getRandomIV(16);
	
	public IConnection getConnection() {
		return echoConnection;
	}
	
	public EchoProtocol(IConnection echoConnection) {
		this.echoConnection = echoConnection;
	}
	
	public void setToken(Token token) {
		this.token = token;
	}
	
	public void initiateHandShakeWith(IUser user) {
		user.recieveHandShake(echoConnection);
	}
	
	public KeyExchangeInfo initiateKeyExchange(IUser fromUser, KeyExchangeInfo keyEx) {
		p = keyEx.p; g = keyEx.g;
		dh = new Ch33_DiffieHellman(p,g);
		keyPair = dh.generateKeyPair(fromUser);
		
		keyExInfo = new KeyExchangeInfo();
		keyExInfo.p = p;keyExInfo.g = g;
		keyExInfo.publicKey = keyPair.getPublicKey();
		return keyExInfo;
	}
	
	public Token generateSessionKey(KeyExchangeInfo key) {
		byte[] sessionKey = this.dh.sessionKey(keyPair, key.publicKey.toByteArray());
		token = new Token();
		token.setSessionKey(sessionKey);
		return token;
	}
	
	public Token getToken() {
		return token;
	}
	
	public void sendMessageTo(IUser user, byte[] message) {		
		try {
			IV = cipherTextBlock;//IV is 16 length			
			String cipher = perform_AES_CBC_Java_Encryption(message,Arrays.copyOf(new Ch28_SHA1().encode(user.getSessionKey()), 16),IV);
			byte[] cipherBytes = ArrayUtils.mergeBytes(IV, cipher.getBytes());
			IMessage messageObj = new Message();
			messageObj.setMessageBytes(cipherBytes);
			System.out.println("Sending to user " + user.toString() + " message : " + new String(cipherBytes)+"\n");
			user.recieveMessage(messageObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] recieveMessage(IUser user, IMessage message) {
		String cipher = null;
		try {
			IV = cipherTextBlock;//IV is 16 length
			int length = message.getMessageBytes().length;
			byte[] choppedMessage = ArrayUtils.extractBytes(message.getMessageBytes(), IV.length, length - IV.length);
			cipher = perform_AES_CBC_Java_Decryption(choppedMessage,Arrays.copyOf(new Ch28_SHA1().encode(user.getSessionKey()), 16),IV);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (cipher == null ? null : cipher.getBytes());
	}
	
	public String perform_AES_CBC_Java_Encryption(byte[] tempByteArray, byte[] keyBytes, byte[] cipherTextBlock) throws Exception {
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
		cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(cipherTextBlock));
		byte[] encrypted = cipher.doFinal(tempByteArray);
		return new String(encrypted);
    }
	
	public String perform_AES_CBC_Java_Decryption(byte[] tempByteArray, byte[] keyBytes, byte[] cipherTextBlock) throws Exception {
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(cipherTextBlock));
		byte[] decrypted = cipher.doFinal(tempByteArray);
		return new String(decrypted);
    }
	
	public static byte[] cipherTextBlock = {(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
			(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
			(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0,
			(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0};

}
