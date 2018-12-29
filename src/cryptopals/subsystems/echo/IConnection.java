package cryptopals.subsystems.echo;

public interface IConnection {
	public void connectTo(IUser user);
	public void initProtocol();
	public void sendMessageTo(IUser user, byte[] message);
	public HandShakeInfo getHandShakeInfo();
	public IProtocol getProtocol();
	public boolean sendAcknowledgement(IUser fromUser, IUser toUser, AckInfo ackInfo);	
}
