package admin;

import java.io.*;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import storefront.Product;

public class AdminApp 
{
	static String ip = "172.24.108.38";
	static int port = 6666;
	
	static Scanner scan = new Scanner(System.in);

	public static void main(String[] args) throws IOException, InterruptedException
	{
		System.out.println("Welcome to the Administration Application.");
		
		System.out.println("Connecting to client...");
		Client client = new Client();
		client.start(ip, port);
		System.out.println("Successfully connected to client with IP " + ip + " and port " + port);
		
		
		// ================== SEND COMMANDS: ==================

		String response = "";
	while(!response.equals("QUIT"))
		{
			String message;
			System.out.printf("\nWhat would you like to do?\n");
			System.out.println("(Q) Quit the application\n(U) Update the products in the inventory\n(R) Return current inventory as JSON ");
			message = scan.nextLine();
			

			switch(message)
			{
				case "U":
					System.out.println("Updating the inventory from the JSON file \"updatedInventory.txt\"...");
					response = client.sendMessage("U");
					Thread.sleep(1000);
					System.out.println(response);
					Scanner s = new Scanner(new File("updatedInventory.txt"));
					while(s.hasNextLine())
					{
						response = client.sendMessage("!" + s.nextLine());
						Thread.sleep(500);
					}
					break;
				case "R":
					System.out.println("Recieving data from server...");
					response = client.sendMessage(message);
					break;
				case "Q":
					response = client.sendMessage(".");
					break;
				default:
					System.out.println("That is not an option!");
			}
			// print out the server response and if we get a QUIT response exit this program
			System.out.println(response);
		}
		client.cleanup();
	}
	
	public static String stringToJson(String filename)
	{
		String json = "";
		
		try
		{
			File file = new File(filename);
			Scanner s = new Scanner(file);
			
			while(s.hasNext())
			{
				json += s.next();
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