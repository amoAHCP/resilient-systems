import ch.trivadis.model.Product;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import java.util.List;

/**
 * Created by Andy Moncsek on 15.10.15.
 */

public class ConnectionTest {

    @Test
    public void simpleConnectionTest() {
        Client client = ClientBuilder.newClient();
        List<Product> res = client.
                target("http://192.168.193.129:9090/WarehouseService/rest/products").
                request("application/json").
                get(new GenericType<List<Product>>() {
                });
        System.out.println(res);
    }
}
