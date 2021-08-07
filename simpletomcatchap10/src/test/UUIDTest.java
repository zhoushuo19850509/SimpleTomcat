package test;

import java.util.UUID;

public class UUIDTest {
    public static void main(String[] args) {
        String a = UUID.randomUUID().toString().replace("-","");
        System.out.println(a);
    }
}
