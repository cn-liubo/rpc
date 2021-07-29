import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.liu.rpc.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosUtilTest {
    public static void main(String[] args) throws NacosException {

        String serviceName1 = "HelloService1";
        String serviceName2 = "HelloService2";
        InetSocketAddress address1 = new InetSocketAddress("127.0.0.1", 9000);
        InetSocketAddress address2 = new InetSocketAddress("127.0.0.1", 9001);

        NamingService namingService = NacosUtil.getNacosNamingService();
        System.out.println(namingService);

        NacosUtil.registerService(serviceName1, address1);
        NacosUtil.registerService(serviceName1, address2);

        List<Instance> allInstance = NacosUtil.getAllInstance(serviceName1);
        System.out.println(allInstance);

        System.out.println("======");
        for (Instance instance : allInstance) {
            System.out.println(instance);
        }

        System.out.println();
    }
}
