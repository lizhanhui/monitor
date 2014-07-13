public class Test {

    private static final int MAX = 10;

    public static void main(String[] args) {

        for (int i = 0; i < MAX; i++) {
            try {
                test(i);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void test(int i) {
        System.out.println("Test " + i);
    }

}
