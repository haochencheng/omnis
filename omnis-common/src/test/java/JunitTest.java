import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

}
