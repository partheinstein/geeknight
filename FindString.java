import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.File;
import java.io.FileInputStream;

public class FindString {

    public static int find(byte[] toSearch, int start, int end, byte[] searchStr) {

        int j = 0;
        for (int i = start; i < end; i++) {
            if (toSearch[i] == searchStr[j]) {
                j++;
                if (j == searchStr.length) {
                    break;
                }
            } else {
                j = 0;
            }
        }
        return j;
    }

    public static void main(String[] args) throws Exception {

        final byte[] searchStr = args[0].getBytes("UTF-8");
        
        File file = new File("file.txt");
        final int readPerPass = 1000;
        int pos = 0;
        int readBytes = 1;
        int numMatches = 0;
        
        try(FileInputStream fis = new FileInputStream(file)) {
            FileChannel fileChannel = fis.getChannel();
            
            while(readBytes > 0) {
                ByteBuffer toSearch = ByteBuffer.allocate(readPerPass);
                readBytes = fileChannel.read(toSearch, pos);
                
                byte[] toSearchArr = toSearch.array();
                numMatches = find(toSearchArr, 0, toSearchArr.length, searchStr);

                if (numMatches == searchStr.length) {
                    System.out.println("found");
                    return; 
                }

                pos = pos + readBytes - numMatches;
            }
        }

        System.out.println("not found");
        return;

        
        // byte[] a = new byte[]{0, 1, 2, 3};
        // byte[] b = new byte[]{0, 1, 2};

        // System.out.println(find(a, 0, a.length, b));

        // b = new byte[]{0, 1, 0};
        // System.out.println(find(a, 0, a.length, b));

        // b = new byte[]{0, 1, 2, 3, 4};
        // System.out.println(find(a, 0, a.length, b));
        
    }
}
