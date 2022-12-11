package admin;

import java.io.*;
import java.net.*;


public class Client 
{
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	
	public void start(String ip, int port) throws IOException, UnknownHostException
	{
		// Connect to the remote server on the specified IP Address and Port
		clientSocket = new Socket(ip, port);
		
		// create input and output network buffers to communicate back and forth with the server
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	public String sendMessage(String msg) throws IOException
	{
		// print a message to the server with a terminating line feed
		out.println(msg);
		
		// return a response from the server
		return in.readLine();
		
	}
	
	public void cleanup() throws IOException
	{
		in.close();
		out.close();
		clientSocket.close();
		System.out.println("Client connection closed.");
	}
	
}
