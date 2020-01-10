import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import util.DBUtils;
import util.HttpUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author sir
 * @Date 2020/1/9 10:06
 * @Description TODO
 **/
@Slf4j
public class TestDemo {

    @Test
    public void test() {
        String url = "http://192.168.1.2:9010/upload";
        Map<String, Object> params = new HashMap<>();
        params.put("file", new File("lib/ojdbc6.jar"));
        params.put("path", "");
        String post = HttpUtils.post(url, params, null, false);
        log.info("请求结果：[{}]", post);
    }
}
