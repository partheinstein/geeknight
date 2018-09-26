import java.util.Random;
import java.io.FileOutputStream;

public class GenerateFile {

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) throws Exception {
	final int len = 10 * 1024 * 1024;
	Random random = new Random();

	try(FileOutputStream fis = new FileOutputStream("file.txt")) {
	    for (int i = 0; i < len; i++) {
		fis.write(AB.charAt(random.nextInt(AB.length())));
	    }
	}
    }
}
