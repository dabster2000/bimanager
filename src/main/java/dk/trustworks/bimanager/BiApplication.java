package dk.trustworks.bimanager;

import dk.trustworks.bimanager.handler.ProjectBudgetHandler;
import dk.trustworks.bimanager.handler.ReportHandler;
import dk.trustworks.bimanager.handler.TaskBudgetHandler;
import dk.trustworks.framework.persistence.Helper;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.util.Headers;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.xnio.Options;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hans on 16/03/15.
 */
public class BiApplication {

    public static void main(String[] args) throws Exception {
        new BiApplication(Integer.parseInt(args[0]));
    }

    public BiApplication(int port) throws Exception {
        System.out.println("ClientManager on port " + port);
        Class.forName("org.mariadb.jdbc.Driver");
        Properties properties = new Properties();
        try (InputStream in = Helper.class.getResourceAsStream("server.properties")) {
            properties.load(in);
        }

        Undertow.builder()
                .addHttpListener(port, properties.getProperty("web.host"))
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setSocketOption(Options.BACKLOG, 10000)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false) //don't send a keep-alive header for HTTP/1.1 requests, as it is not required
                .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                .setHandler(Handlers.header(Handlers.path()
                        .addPrefixPath("/api/projectbudgets", new ProjectBudgetHandler())
                        .addPrefixPath("/api/taskbudgets", new TaskBudgetHandler())
                        .addPrefixPath("/api/reports", new ReportHandler())
                        , Headers.SERVER_STRING, "U-tow"))
                        .setWorkerThreads(200)
                        .build()
                        .start();

        registerInZookeeper(properties.getProperty("zookeeper.host"), port);
    }

    private static void registerInZookeeper(String zooHost, int port) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zooHost+":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address("localhost")
                .port(port)
                .name("biservice")
                .build();

        ServiceDiscoveryBuilder.builder(Object.class)
                .basePath("trustworks")
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .build()
                .start();
    }
}
