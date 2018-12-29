package cryptopals.subsystems.echo;

public class MITM_Connection_Ch35 extends MITM_Connection {
	
	public void connectTo(IUser user) {
		this.mitmParamG = this.mitmParamP;
	}

}
