package storefront;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StoreFront
{

	static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		
		// --------------- START SERVER ON SEPARATE THREAD ------------------
		ServerThread server = new ServerThread();
		server.start();
		// ------------------------------------------------------------------
		
		// check to see if connected, then start application:
		Thread.sleep(500);
		while(true)
		{
			if(server.getConnectedStatus() == true)
			{
				break;
			}
			System.out.print(".");
			Thread.sleep(500);
		}
		
		// user loop : Starting user application:
		int input = 0;
		while(input != 5)
		{
			// Fetch inventory from JSON file (update)
			InventoryManager inventory = new InventoryManager();
			ShoppingCart cart = new ShoppingCart();
			sortInventory(inventory, cart);
			printInventory(inventory.getInventory(), cart.getShoppingCart());
			System.out.println("(1) Add item to cart, \n(2) Remove Item From Cart, \n(3) Purchase Items in Cart,\n(4) Refresh Inventory \n(5) Quit program");
			input = scan.nextInt();
			switch(input)
			{
			case 1:		// add to cart
				System.out.println("Choose an item: ");
				int result1 = scan.nextInt();
				addToCart(result1, inventory, cart);
				break;
			case 2:		// remove from cart
				System.out.println("Choose an item to delete from shopping cart");
				int result2 = scan.nextInt();
				removeFromCart(result2, inventory, cart);
				break;
			case 3:		// Purchase items in cart
				printInventory(inventory.getInventory(), cart.getShoppingCart());
				System.out.println("Would you like to purchase these items?");
				System.out.println("#0 - YES");
				System.out.println("#1 - NO");
				int result3 = scan.nextInt();
				purchaseItems(result3, cart);
				break;
			case 4:		// Refresh inventory
				break;
			default:
				break;
			}
			System.out.println("Application closed.");
		}
		
		
	}
	
	private static void purchaseItems(int result, ShoppingCart oldCart) 
	{
		ArrayList<Product> cart = oldCart.getShoppingCart();
		if(result == 0) 
		{
			// purchase items
			cart.clear();
			System.out.println("You have purchased the items!");
		}
		else {}
		
		// update JSON
		updateJson("shoppingCart.txt", cart);
	}




	private static void removeFromCart(int result, InventoryManager inventory, ShoppingCart oldCart) 
	{
		ArrayList<Product> cart = oldCart.getShoppingCart();
		
		cart.remove(result);
		
		// update JSON
		updateJson("shoppingCart.txt", cart);
	}



	private static void addToCart(int result, InventoryManager inventory, ShoppingCart oldCart) 
	{
		ArrayList<Product> cart = oldCart.getShoppingCart();
		
		// if the shopping cart is empty, add item to first slot in shopping cart array
		if(cart.size() == 0)
		{ 
			cart.add((inventory.getInventory().get(result)));
			cart.get(cart.size() - 1).qty++;
		}
		// check if items match each other. if yes, qty++. if not, add new item
		else
		{
			int x = 0;
			boolean flag = false;
			while(x < cart.size())
			{
				// if the inventory item matches the shopping cart item, qty++
				if(inventory.getInventory().get(result) == cart.get(x))
				{
					cart.get(x).qty++;
					flag = true;
					break;
				}
				x++;
			}
			if(flag == false)
			{
				cart.add((inventory.getInventory().get(result)));
				cart.get(cart.size() - 1).qty++;
			}
		}
		
		// Update JSON file
		updateJson("shoppingCart.txt", cart);
	}

	private static void updateJson(String filename, ArrayList<Product> list)
	{
		// delete contents of file
		try
		{
			File file = new File(filename);
			FileWriter fw = new FileWriter(file, false);
			fw.close();
		}
		catch(IOException e)
		{
			e.getStackTrace();
		}
			
		// add inventory to file
		for(int i = 0; i < list.size(); i++)
		{
			saveToFile(filename, list.get(i), true);
		}
	}
	
	private static void saveToFile(String filename, Product product, boolean append) 
	{
		PrintWriter pw;
		try
		{
			// create a file File to write
			File file = new File(filename);
			FileWriter fw = new FileWriter(file, append);
			pw = new PrintWriter(fw);
			
			//write product as json
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(product);
			pw.println(json);
			
			//cleanup
			pw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void printInventory(ArrayList<Product> list, ArrayList<Product> shop)
	{
		System.out.println();
		System.out.println("INVENTORY:--------------------------------------------------------------+---------------------------------------------------------------------------");
		System.out.println("#      ITEM:          DESCRIPTION:                         PRICE:       |  SHOPPING CART:");
		
		int extraSpacesName = 0;
		int extraSpacesDesc = 0;
		int extraSpacesNameCart = 0;
		int extraSpacesDescCart = 0;
		int cartNum = 0;
		int shopSize = shop.size();
		
		for(int x = 0; x < list.size(); x++)
		{
			extraSpacesName = 10 - list.get(x).name.length();
			extraSpacesDesc = 30 - list.get(x).description.length();

			if(cartNum < shopSize)
			{
				extraSpacesNameCart = 10 - shop.get(cartNum).name.length();
				extraSpacesDescCart = 30 - shop.get(cartNum).description.length();
				// inventory
				System.out.printf("#%d     ", x);
				System.out.printf("%s%" + extraSpacesName + "s     ", list.get(x).name, ' ');
				System.out.printf("(%s)%" + extraSpacesDesc + "s     ", list.get(x).description, ' ');
				System.out.printf("$%.2f        |  ", list.get(x).price, list.get(x).qty);
				
				// shopping cart
				System.out.printf("#%d     ", cartNum);
				System.out.printf("%s%" + extraSpacesNameCart + "s     ", shop.get(cartNum).name, ' ');
				System.out.printf("(%s)%" + extraSpacesDescCart + "s     ", shop.get(cartNum).description, ' ');
				System.out.printf("$%.2f     %d\n", shop.get(cartNum).price, shop.get(cartNum).qty);
				cartNum++;
			}
			else
			{
				// inventory
				System.out.printf("#%d     ", x);
				System.out.printf("%s%" + extraSpacesName + "s     ", list.get(x).name, ' ');
				System.out.printf("(%s)%" + extraSpacesDesc + "s     ", list.get(x).description, ' ');
				System.out.printf("$%.2f        |     \n", list.get(x).price, list.get(x).qty);
			}
			
		}
		float total = 0;
		for(int x = 0; x < shop.size(); x++)
		{
			total += shop.get(x).price * (float)shop.get(x).qty;
		}
		
		System.out.printf("                                                                           TOTAL: $%.2f", total);
		System.out.println();
		System.out.println();
	}
	
	private static void sortInventory(InventoryManager oldInventory, ShoppingCart oldShoppingCart)
	{
		ArrayList<Product> inventory = oldInventory.getInventory();
		ArrayList<Product> shoppingCart = oldShoppingCart.getShoppingCart();
		
		// sort by alphabetical order
		Collections.sort(inventory, (o1, o2) -> {
			return o1.getName().compareTo(o2.getName());
		});
		Collections.sort(shoppingCart, (o1, o2) -> {
			return o1.getName().compareTo(o2.getName());
		});
		// sort by price
		Collections.sort(inventory, (o1, o2) -> {
			return Float.compare(o1.getPrice(), o2.getPrice());
		});
		Collections.sort(shoppingCart, (o1, o2) -> {
			return Float.compare(o1.getPrice(), o2.getPrice());
		});
		
		updateJson("shoppingCart.txt", shoppingCart);
		updateJson("inventory.txt", inventory);
	}

}
