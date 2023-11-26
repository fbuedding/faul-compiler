package file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {
  public static String readFile(String path) throws FileNotFoundException {
    File myObj = new File(path);
    Scanner myReader = new Scanner(myObj);
    String data = "";
    while (myReader.hasNextLine()) {
      data = data.concat(myReader.nextLine() + "\n");
    }
    myReader.close();
    return data;
  }

}
