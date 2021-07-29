import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liu.rpc.entity.RpcRequest;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class TestSerializer {

    private static class HelloService {
        private String name;
        private Integer age;
    }

    public Object buildObject() {
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(),
                "HelloService", "hello", new Object[]{"lll", 12},
                new Class<?>[]{String.class, Integer.class}, false);
        return rpcRequest;
    }

    @Test
    public void testJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(buildObject());//转为字符串
        System.out.println(s);
        RpcRequest rpcRequest1 = objectMapper.readValue(s, RpcRequest.class);
        System.out.println(rpcRequest1);

        System.out.println("======================");

        byte[] bytes = objectMapper.writeValueAsBytes(buildObject());//转为字节数组
        System.out.println(bytes.length);
        System.out.println(Arrays.toString(bytes));
        RpcRequest rpcRequest2 = objectMapper.readValue(bytes, RpcRequest.class);
        System.out.println(rpcRequest2);
    }

    @Test
    public void testHessian() {

    }

    @Test
    public void testKryo() {

    }

    @Test
    public void testProtobuf() {

    }

}
