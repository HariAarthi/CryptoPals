package cryptopals.subsystems.echo;

import java.math.BigInteger;

import cryptopals.protocols.KeyPair;
import cryptopals.set5.Ch33_DiffieHellman;

public interface IUser {
	public String getName();
	public void connectTo(IUser user);
	public void recieveHandShake(IConnection connection);
	public IConnection getConnection();
	public byte[] getPublicKey();
	public byte[] getPrivateKey();
	public void recieveMessage(IMessage message);
	public void sendMessageTo(IUser user, byte[] message);
	public boolean finalizeConnection(Token token);
	public byte[] getSessionKey(HandShakeInfo handShakeInfo);
	public boolean isConnected();
	public void generateKeyPair(HandShakeInfo handShakeInfo);
	public byte[] getSessionKey();
	public void setConnected(boolean isConnected);
	public void nullifyConnection();
	public void setConnection(IConnection connection);
}
