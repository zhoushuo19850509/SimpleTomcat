package test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapTest {
    public static void main(String[] args) {
        Map<String, String > a = new HashMap<String, String>();
        a.put("1","zhoushuo");
        a.put("1","zs");
        System.out.println(a.get("1"));

        /**
         * ���Ը���valueɾ��map��ĳ��Ԫ��
         */

        // �ȳ�ʼ��һ��map����
        Map<String, Customer> b = new HashMap<String, Customer>();
        Customer c1 = new Customer("Hob","ningbo");
        Customer c2 = new Customer("Helen","beijing");
        b.put("1",c1);
        b.put("2",c2);

        System.out.println(b);

        // ����valueɾ��map��ĳ��Ԫ��
        Collection<Customer> list = b.values();
        list.remove(c2);

        System.out.println(b);


    }
}

class Customer{
    private String name;
    private String location;

    public Customer(String name, String location) {
        this.name = name;
        this.location = location;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

