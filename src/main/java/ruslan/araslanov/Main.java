package ruslan.araslanov;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 1000;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                int countR = charCount(route, 'R');

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(countR, 1, (oldValue, newValue) -> oldValue + newValue);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        printResult();
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

    public static void printResult() {
        if (sizeToFreq.isEmpty()) {
            System.out.println("Нет данных для обработки");
            return;
        }
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
}