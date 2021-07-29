import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class TestLog {
    public static void main(String[] args) {
        /*try {
            int i = 1/ 0;
        } catch (Exception e) {
//            log.error("除0异常", e);
            log.error("除0异常" + e);
        }
        int a = 2;
        log.info("斤斤计较：{}", a);

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();*/


        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 9999);
        System.out.println(inetSocketAddress.toString() + 2);
    }
}
