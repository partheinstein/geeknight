import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Main {

    public static void main(String[] args) throws Exception {

        // Validate input
        if (args.length != 2)            throw new IllegalArgumentException("Needle (search term) and haystack (file path) not provided ");
        if (args[0].length() != 10)      throw new IllegalArgumentException("Needle must be 10 characters");
        if (!new File(args[1]).exists()) throw new FileNotFoundException("File containing Haystack cannot be found");

        final File file = new File(args[1]);
        final byte[] needle = args[0].getBytes("UTF-8");

        try(FileInputStream fis = new FileInputStream(file)) {
            FileChannel fc = fis.getChannel();

            final short bufferLen = 4000; // How much data do we read on each iteration
            int currentPosition = 0; // Current read position within file
            short bytesRead; // How much data has been read on current iteration

            do {
                ByteBuffer buffer = ByteBuffer.allocate(bufferLen);
                bytesRead = (short) fc.read(buffer, currentPosition); // Read data at current position from file

                if (find(buffer.array(), needle, bytesRead)) return;

                // In case the match is within the last 10 characters of the bytes  read them as part of the next pass
                currentPosition = currentPosition + bytesRead - 10;
            } while (bytesRead > 10);
        }

        System.out.println("NOT Found!");
    }

    private static boolean find(byte[] haystack, byte[] needle, short lastByte) {
        for(int i = 9; i < lastByte; i++) {
            if(haystack[i-9] == needle[0] && haystack[i] == needle[9]) { // Test head and tail characters
                if (haystack[i - 4] == needle[5] && haystack[i - 5] == needle[4]) { // Test middle two characters
                    if (haystack[i - 2] == needle[7] && haystack[i - 3] == needle[6] && haystack[i - 8] == needle[1]) { // test remainder
                        System.out.println("Found!");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
