package hellojpa;

public class ValueMain {

    public static void main(String[] args) {
        int a = 10;
        
        int b = a; // b로 a의값만 복사해서 넘어간다. 같은 주소를 공유하지않음
        a = 20;

        System.out.println("a = " + a);
        System.out.println("b = " + b);

        Integer c = new Integer(10);
        Integer d = c;

        System.out.println("c = " + c);
        System.out.println("d = " + d);
    }
}
