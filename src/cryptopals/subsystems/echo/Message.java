package cryptopals.subsystems.echo;

import cryptopals.utils.ArrayUtils;

public class Message implements IMessage {
	
	private byte[] messageBytes = null;	
	
	public Message() {
		//this.iFormatter = iFormatter;
	}
	
	public void setMessageBytes(byte[] messageBytes) {
		this.messageBytes = messageBytes;
	}
	
	public byte[] getMessageBytes() {
		return this.messageBytes;
	}

}
