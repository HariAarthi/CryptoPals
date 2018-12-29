package cryptopals.set5;

import java.math.BigInteger;

import cryptopals.protocols.KeyPair;
import cryptopals.subsystems.echo.EchoConnection;
import cryptopals.subsystems.echo.EchoProtocol;
import cryptopals.subsystems.echo.IMessage;
import cryptopals.subsystems.echo.MITM_Connection;
import cryptopals.subsystems.echo.MITM_User;
import cryptopals.subsystems.echo.Message;
import cryptopals.subsystems.echo.User;

public class Ch34_DH_MITM_V2 {
	
	public User hereComesMiddleMan(User userA, User userB) {		
		MITM_User mitm = new MITM_User("MITM");
		
		EchoConnection.isHacked = true;
		
		userA.connectTo(userB);
		
		System.out.println("userA sending message to userB...");
		userA.sendMessageTo(userB, "TESTTESTTESTTESTTESTTESTTESTTEST".getBytes());
		
		System.out.println("userB sending message to userA...");
		userB.sendMessageTo(userA, "TESTTESTTESTTESTTESTTESTTEST1234".getBytes());
		return mitm;
	}
	
	public void launchMITM(User userA, User userB) {		
		hereComesMiddleMan(userA,userB);
	}
	
	public static void main(String[] args) {
		Ch34_DH_MITM_V2 mitmAttack = new Ch34_DH_MITM_V2();
		User userA = new User("userA");
		User userB = new User("userB");
		mitmAttack.launchMITM(userA,userB);
	}

}
