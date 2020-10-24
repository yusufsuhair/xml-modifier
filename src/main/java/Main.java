import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
  public static void main(String[] args) {

    try (InputStream input = new FileInputStream("config/test.properties")) {

      Properties prop = new Properties();

      prop.load(input);

      System.out.println(prop.getProperty("key1"));
      System.out.println(prop.getProperty("key2"));

    } catch (IOException io) {
      io.printStackTrace();
    }
  }
}

