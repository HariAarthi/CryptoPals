package cryptopals.subsystems.echo;

import java.math.BigInteger;

public class MITM_Connection extends EchoConnection {

	private IUser userA, userB, mitmUserB, mitmUserA;
	private EchoProtocol echoProtocol;
	//private MITM_User mitmUserA;
	private IConnection connectionWithA, connectionWithB;
	private HandShakeInfo handShakeInfo;	
	
	public MITM_Connection() {}
	
	public MITM_Connection(IUser user) {
		mitmUserA = new User("mitmUserA");
		mitmUserB = new User("mitmUserB");
		this.userA = user;
	}

	public void connectTo(IUser user) {
		this.userB = user;
		
		if(EchoConnection.isHacked) {
			EchoConnection.isHacked = false;
		
			establishConnection();
		
			System.out.println("userA isConnected: "+ userA.isConnected());
			System.out.println("mitmUserA isConnected: "+ mitmUserA.isConnected());
			System.out.println("userB isConnected: "+ userB.isConnected());
			System.out.println("mitmUserB isConnected: "+ mitmUserB.isConnected());
			System.out.println();
		} else {
			super.connectTo(user);
		}
	}
	
	private void establishConnection() {
		aToM();
		mToB();
	}
	
	private void aToM() {
		connectionWithA = new MITM_Connection();
		handShakeInfo = new HandShakeInfo();
		handShakeInfo.p = mitmParamP;
		handShakeInfo.g = mitmParamG;
		handShakeInfo.fromUser = this.userA;
		this.userA.generateKeyPair(handShakeInfo);		
		handShakeInfo.publicKey = new BigInteger(this.userA.getPublicKey());
		
		((MITM_Connection)connectionWithA).handShakeInfo = handShakeInfo;
		echoProtocol = new EchoProtocol(connectionWithA);
		((MITM_Connection)connectionWithA).echoProtocol = echoProtocol;
		echoProtocol.initiateHandShakeWith(mitmUserA);
		((MITM_Connection)connectionWithA).mitmUserA = this.mitmUserA;
	}
	
	private byte[] injectPublicKey() {
		return mitmUserA.getPublicKey();
	}
	
	private void mToB() {
		connectionWithB = new MITM_Connection();
		handShakeInfo = new HandShakeInfo();
		handShakeInfo.p = mitmParamP;
		handShakeInfo.g = mitmParamG;
		handShakeInfo.fromUser = this.mitmUserB;
		this.mitmUserB.generateKeyPair(handShakeInfo);		
		handShakeInfo.publicKey = new BigInteger(this.injectPublicKey()/*this.mitmUserB.getPublicKey()*/);

		mitmUserB.setConnection(connectionWithB);
		((MITM_Connection)connectionWithB).handShakeInfo = handShakeInfo;
		echoProtocol = new EchoProtocol(connectionWithB);
		((MITM_Connection)connectionWithB).echoProtocol = echoProtocol;
		echoProtocol.initiateHandShakeWith(userB);
		((MITM_Connection)connectionWithB).userA = this.userA;
		((MITM_Connection)connectionWithB).mitmUserB = this.mitmUserB;
		((MITM_Connection)connectionWithB).mitmUserA = this.mitmUserA;
	}

	public void sendMessageTo(IUser user, byte[] message) {
		if(user == userB) {
			this.echoProtocol.sendMessageTo(mitmUserA,message);		
			mitmUserB.sendMessageTo(userB, message);
		} else if(user == userA) {
			this.echoProtocol.sendMessageTo(mitmUserB,message);			
			mitmUserA.sendMessageTo(userA, message);
		} else {
			super.sendMessageTo(user, message);
		}
	}

	public void setHandShakeInfo(HandShakeInfo handShakeInfo) {
		this.handShakeInfo =handShakeInfo;
	}
	
	public HandShakeInfo getHandShakeInfo() {
		return handShakeInfo;
	}
	
	public IProtocol getProtocol() {
		return echoProtocol;
	}
}
