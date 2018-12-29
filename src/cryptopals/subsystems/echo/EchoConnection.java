package cryptopals.subsystems.echo;

import java.math.BigInteger;
import java.util.Arrays;

public class EchoConnection implements IConnection {
	
	private EchoProtocol echoProtocol;
	private Token token;
	protected IUser fromUser;
	public static boolean isHacked = false; //sort of a back-door for Challenge 34
	private HandShakeInfo handShakeInfo;
	protected BigInteger mitmParamP = BigInteger.valueOf(32), mitmParamG = BigInteger.valueOf(5);
	
	public static IConnection getInstance(IUser user) {
		if(isHacked) return new MITM_Connection(user);
		return new EchoConnection(user);
	}
	
	protected EchoConnection() {}
	
	private EchoConnection(IUser user) {
		this.fromUser = user;
		echoProtocol = new EchoProtocol(this);
	}
	
	public IProtocol getProtocol() {
		return echoProtocol;
	}

	public boolean finalizeIncoming(Token token) {
		byte[] sessionKeyIncoming = token.getSessionKey();
		return Arrays.equals(sessionKeyIncoming, this.echoProtocol.getToken().getSessionKey());
	}
	
	@Override
	public void connectTo(IUser user) {
		handShakeInfo = new HandShakeInfo();
		handShakeInfo.p = BigInteger.valueOf(32);
		handShakeInfo.g = BigInteger.valueOf(5);
		handShakeInfo.fromUser = this.fromUser;
		this.fromUser.generateKeyPair(handShakeInfo);		
		handShakeInfo.publicKey = new BigInteger(this.fromUser.getPublicKey());
		
		echoProtocol = new EchoProtocol(this);
		echoProtocol.initiateHandShakeWith(user);	
	}
	
	public KeyExchangeInfo acceptIncoming(IUser fromUser, KeyExchangeInfo keyExInfo) {
		KeyExchangeInfo toBeSentBack = this.echoProtocol.initiateKeyExchange(fromUser,keyExInfo);
		this.echoProtocol.generateSessionKey(keyExInfo);
		return toBeSentBack;
	}
	
	public boolean sendAcknowledgement(IUser fromUser, IUser toUser, AckInfo ackInfo) {
		getHandShakeInfo().publicKey = ackInfo.publicKey;
		
		byte[] sessionKey = toUser.getSessionKey(getHandShakeInfo());
		Token token = new Token();
		token.setSessionKey(sessionKey);
		getProtocol().setToken(token);
		
		boolean isConnected = fromUser.finalizeConnection(token);
		toUser.setConnected(isConnected);
		
		return isConnected;
	}

	@Override
	public void initProtocol() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessageTo(IUser user, byte[] message) {
		this.getProtocol().sendMessageTo(user,message);		
	}

	@Override
	public HandShakeInfo getHandShakeInfo() {
		return handShakeInfo;
	}

}
