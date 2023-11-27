package file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
  public static void write(String path, String input) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    writer.write(input);
    writer.close();
  }
}
