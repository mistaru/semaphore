import Buffer.CircularQueue;

import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;

public class CQ extends CircularQueue {
    private Semaphore full;
    private Semaphore empty;
    private Semaphore mutex;

    private CQ(int size) {
        super(size);
        empty = new Semaphore(size);
        full = new Semaphore(0);
        mutex = new Semaphore(1);
    }

    private void putCQ(char ch) throws InterruptedException {
        empty.acquire();
        mutex.acquire();
        put(ch);
        mutex.release();
        full.release();
    }

    private Character getCQ(Predicate<String> p) throws InterruptedException {
        full.acquire();
        mutex.acquire();
        char c = end();
        if (p.test(String.valueOf(c))) {
            c = get();
            mutex.release();
            empty.release();

            return c;
        } else {
            mutex.release();
            full.release();

            return null;
        }
    }

    static class Producer implements Runnable {
        String name;
        CQ q;
        private Scanner in;

        Producer(String name, CQ q) {
            this.name = name;
            this.q = q;
            this.in = new Scanner(System.in);
            new Thread(this).start();
        }

        public void run() {

            while (true) {

                String input = in.nextLine().replaceAll("[ ]+", "");
                try {
                    for (Character c : input.toCharArray()) {
                        System.out.println(name + ": " + c);
                        q.putCQ(c);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer implements Runnable {

        Predicate<String> status;
        CQ q;
        String name;

        Consumer(String name, CQ q, Predicate<String> status) {
            this.q = q;
            this.name = name;
            this.status = status;
            new Thread(this).start();
        }

        public void run() {
            while (true) {
                try {
                    Character c = q.getCQ(status);
                    if (c != null) {
                        System.out.println(name + " eat " + c);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

        final Predicate<String> numberPred = str -> str.matches("[0-9]+");
        final Predicate<String> wordPred = str -> str.matches("[a-zA-Z]+");
        final Predicate<String> anythingPred = str -> !numberPred.test(str) && !wordPred.test(str);

        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextInt();

        CQ q = new CQ(size);
        new Producer("Producer: ", q);
        new Consumer("Number: ", q, numberPred);
        new Consumer("Letter: ", q, wordPred);
        new Consumer("Symbols: ", q, anythingPred);
    }
}
