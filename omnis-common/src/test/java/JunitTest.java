import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.InetUtil;
import util.Md5Util;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-09 08:18
 **/
public class JunitTest {

    @Test
    @DisplayName("测试md5")
    public void md5(){
        String str="123456";
        Assertions.assertEquals("E10ADC3949BA59ABBE56E057F20F883E", Md5Util.getMD5(str));
    }

    @Test
    @DisplayName("测试是否是ip")
    public void ip(){
        Assertions.assertEquals(InetUtil.isIP("127.0.0.1"), true);
        Assertions.assertEquals(InetUtil.isIP("localhost"), false);
        Assertions.assertEquals(InetUtil.isIP("192.168.0.1"), true);
    }


}
