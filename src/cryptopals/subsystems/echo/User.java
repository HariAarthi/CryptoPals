package cryptopals.subsystems.echo;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import cryptopals.protocols.KeyPair;
import cryptopals.set2.Ch10_CBC_Encryption;
import cryptopals.set5.Ch33_DiffieHellman;
import cryptopals.utils.ArrayUtils;

public class User implements IUser {

	protected KeyPair keyPair;
	private IConnection connection;
	private BigInteger privateKey;
	private byte[] sessionKey;
	private boolean isConnected;
	private String name;
	
	
	public User(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public void connectTo(IUser user) {
		if(this.connection == null) connection = EchoConnection.getInstance(this);
		
		connection.connectTo(user);		
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public void recieveHandShake(IConnection connection) {
		this.connection = connection;
		HandShakeInfo handShakeInfo = connection.getHandShakeInfo();
		this.generateKeyPair(handShakeInfo);
				
		this.getSessionKey(handShakeInfo);
		
		AckInfo ackInfo = new AckInfo();
		ackInfo.publicKey = keyPair.getPublicKey();
		isConnected = connection.sendAcknowledgement(this, handShakeInfo.fromUser, ackInfo);
	}
	
	public void generateKeyPair(HandShakeInfo handShakeInfo) {
		Ch33_DiffieHellman dh = new Ch33_DiffieHellman(handShakeInfo.p, handShakeInfo.g);
		this.keyPair = dh.generateKeyPair(this);
	}
	
	public boolean finalizeConnection(Token token) {
		isConnected = Arrays.equals(sessionKey, token.getSessionKey());
		return isConnected;
	}
	
	public KeyPair getKeyPair() {		
		return keyPair;
	}
	
	public byte[] getSessionKey(HandShakeInfo handShakeInfo) {
		if(sessionKey == null) sessionKey = new Ch33_DiffieHellman(handShakeInfo.p,handShakeInfo.g).sessionKey(keyPair, handShakeInfo.publicKey.toByteArray());
		return sessionKey;
	}
	
	public byte[] getSessionKey() {
		return sessionKey;
	}
	
	public String toString( ) {
		return this.name;
	}
	
	public byte[] getPublicKey() {
		return this.keyPair.getPublicKey().toByteArray();
	}
	
	public void generatePublicKey(Ch33_DiffieHellman dh) {		
		this.keyPair = dh.generateKeyPair(this);
	}

	public byte[] getPrivateKey() {
		if(privateKey == null) privateKey = new BigInteger(32,new SecureRandom());
		return privateKey.toByteArray();
	}

	@Override
	public IConnection getConnection() {
		return connection;
	}
	
	public void nullifyConnection() {
		this.connection = null;
	}

	@Override
	public void recieveMessage(IMessage message) {
		byte[] msgBytes = this.connection.getProtocol().recieveMessage(this, message);
		System.out.println("Message recieved by " +this.toString() + " : "+ new String(msgBytes));		
	}

	@Override
	public void sendMessageTo(IUser user, byte[] message) {
		this.getConnection().sendMessageTo(user, message);
		
	}

	@Override
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	@Override
	public void setConnection(IConnection connection) {
		this.connection = connection;		
	}

}
