import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestCompletableFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        new Thread(() -> {
            System.out.println("CompletableFuture可以监控这个任务的执行");
            future.complete("任务返回结果");
        }).start();
        System.out.println(future.get());
    }
}
