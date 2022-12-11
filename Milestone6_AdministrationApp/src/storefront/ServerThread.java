package storefront;

import java.io.IOException;

public class ServerThread extends Thread
{
	Server server = new Server();
	
	public void run()
	{

		try {
			server.start(6666);
			server.cleanup();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	public boolean getConnectedStatus()
	{
		return server.isConnected;
	}
}
