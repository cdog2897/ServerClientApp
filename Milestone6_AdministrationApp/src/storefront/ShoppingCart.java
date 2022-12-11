package storefront;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ShoppingCart 
{
	private ArrayList<Product> shoppingCart = new ArrayList<>();
	
	public ShoppingCart()
	{
		try
		{
			File file = new File("shoppingCart.txt");
			Scanner s = new Scanner(file);
			
			while(s.hasNext())
			{
				// read a string of JSON and convert to a Car
				String json = s.nextLine();
				ObjectMapper objectMapper = new ObjectMapper();
				
				Product product = objectMapper.readValue(json, Product.class);
				shoppingCart.add(product);
			}
			s.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @return the shoppingCart
	 */
	public ArrayList<Product> getShoppingCart() {
		return shoppingCart;
	}

	/**
	 * @param shoppingCart the shoppingCart to set
	 */
	public void setShoppingCart(ArrayList<Product> shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	
}
