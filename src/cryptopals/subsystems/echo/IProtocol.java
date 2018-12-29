package cryptopals.subsystems.echo;

import cryptopals.protocols.KeyPair;

public interface IProtocol {
	public void initiateHandShakeWith(IUser user);
	public void sendMessageTo(IUser user, byte[] message);
	public byte[] recieveMessage(IUser user, IMessage message);
	public void setToken(Token token);
	//public void recieveAck(AckInfo ackInfo);
	//public boolean finalizeConnection();
	//public byte[] fetchMessage(IMessage message);
}
