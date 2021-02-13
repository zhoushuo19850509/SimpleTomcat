package test;

import java.util.HashMap;
import java.util.Map;

public class MapTest {
    public static void main(String[] args) {
        Map<String, String > a = new HashMap<String, String>();
        a.put("1","zhoushuo");
        a.put("1","zs");
        System.out.println(a.get("1"));

    }
}
