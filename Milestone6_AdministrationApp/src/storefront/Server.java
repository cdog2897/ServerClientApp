package storefront;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class Server 
{

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	
	public boolean isConnected = false;
	
	public void start(int port) throws IOException
	{
		// wait for a client connection
		System.out.println("Waiting for a client connection...");
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		 
		// create input and output network buffers if you get to this point
		System.out.println();
		System.out.println("Recieved a client connection on port " + clientSocket.getLocalPort());
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		isConnected = true;
		
		// wait for command
		String inputLine;
		while((inputLine = in.readLine()) != null)
		{
			// if period command then shut server down
			if(".".equals(inputLine))
			{
				System.out.println("Got a message to shut the server down.");
				out.println("QUIT");
				break;
			}
			else if("R".equals(inputLine))
			{
				out.println(stringToJson("inventory.txt"));
			}
			else if("U".equals(inputLine))
			{
				// delete contents of file
				try
				{
					File file = new File("inventory.txt");
					FileWriter fw = new FileWriter(file, false);
					fw.close();
				}
				catch(IOException e)
				{
					e.getStackTrace();
				}
				out.println("Updating contents (1/2)...");
			}
			else if(inputLine.charAt(0) == '!')
			{
				// replace inventory.txt with updatedInventory.txt
				String newJson = inputLine.substring(1);
				
				// add inventory to file
				PrintWriter pw;
				try
				{
					// create a file File to write
					File file = new File("inventory.txt");
					FileWriter fw = new FileWriter(file, true);
					pw = new PrintWriter(fw);
					pw.println(newJson);
					//cleanup
					pw.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				out.println("Updating contents (2/2)...");
			}
			else
			{
				// success message
				System.out.println("Got a message of: " + inputLine);
				out.println("OK");
			}
		}
	}
	
	
	public void cleanup() throws IOException
	{
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();
		// exit message that server is shut down
		System.out.println("Server is shut down, please shut down program");
	}
	
	public static String stringToJson(String filename)
	{
		String json = "";
		
		try
		{
			File file = new File(filename);
			Scanner s = new Scanner(file);
			
			while(s.hasNextLine())
			{
				json += s.nextLine();
			}
			s.close();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return json;
		
	}
	
}
