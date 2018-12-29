package cryptopals.subsystems.echo;

public interface IMessage {
	
	public void setMessageBytes(byte[] messageBytes);
	public byte[] getMessageBytes();	
}
