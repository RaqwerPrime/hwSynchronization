package ruslan.araslanov;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static Thread threadPrint;

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 1000;
        Thread[] threads = new Thread[threadCount];

        threadPrint = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                        if (!sizeToFreq.isEmpty()) {
                            printMax();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });
        threadPrint.start();

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                int countR = charCount(route, 'R');

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(countR, 1, (oldValue, newValue) -> oldValue + newValue);
                    sizeToFreq.notify();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        threadPrint.interrupt();
        threadPrint.join();

        printResultFinal();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int charCount(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public static void printResultFinal() {
        if (sizeToFreq.isEmpty()) {
            System.out.println("Нет данных для обработки");
            return;
        }
        System.out.println();
        System.out.println("Финальные данные !!! ");
        Map.Entry<Integer, Integer> max = Collections.max(sizeToFreq.entrySet(),
                Map.Entry.comparingByValue());
        System.out.println("Самое частое количество повторений " +
                max.getKey() + " Встретилось " + max.getValue() + " раз ");

        System.out.println("Другие размеры : ");
        sizeToFreq.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (!entry.getKey().equals(max.getKey())) {
                        System.out.println(" - " + entry.getKey() +
                                " (" + entry.getValue() + ") Раз ");
                    }
                });

    }

    public static void printMax() {
        Map.Entry<Integer, Integer> max = Collections.max(sizeToFreq.entrySet(),
                Map.Entry.comparingByValue());
        System.out.println("Текущий промежуточный лидер  " +
                max.getKey() + " Встретилось " + max.getValue() + " раз ");

    }
}
