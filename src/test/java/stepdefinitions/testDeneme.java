package stepdefinitions;

import java.util.ArrayList;
import java.util.List;

public class testDeneme {
    public static void main(String[] args) {

        String a = "b";
        String b = "a" + a;
        String c = "ab";

        System.out.println(b);
        System.out.println(b==c);
        System.out.println(b.equals(c));

    }
}
