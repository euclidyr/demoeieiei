package cloud.zeen.zeenlineoaloyalty.helper;

import java.util.Base64;

public class Base64Helper {
    public static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
}
