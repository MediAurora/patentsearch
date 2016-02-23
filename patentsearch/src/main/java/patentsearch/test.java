/**
 * 
 */
package patentsearch;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author divyaprakash
 *
 */
public class test {

	/**
	 * 
	 */
	public test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties props = new Properties();
		try(InputStream resourceStream = 
				Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
		    props.load(resourceStream);
		    System.out.println(props.get("key"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
