package com.kpi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Main {
    volatile static int variable = 0;
    volatile static List<Thread> threads = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введіть початкову кількість потоків:");
        int initialNumOfThreads = validateNonNegativeInput(scanner);

        System.out.println("Оберіть тип доступу: 1 - синхронізований, 2 - асинхронізований");
        int accessType = scanner.nextInt();
        createThreads(initialNumOfThreads, accessType);

        while (variable < 1000) {
            System.out.println("Меню:");
            System.out.println("1. Змінити кількість потоків");
            System.out.println("2. Змінити пріоритет потоків");
            System.out.println("3. Показати поточну інформацію про програму");
            System.out.println("4. Вийти");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    changeNumOfThreads(accessType);
                    break;
                case 2:
                    changeThreadPriority();
                    break;
                case 3:
                    showCurrentState();
                    break;
                case 4:
                    showCurrentState();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Невірний вибір!");
                    break;
            }
        }
        System.out.println("Значення змінної стало рівним 0. Програма завершує роботу.");
        System.exit(0);
    }

    static void createThreads(int numOfThreads, int accessType) {
        for (int i = 0; i < numOfThreads; i++) {
            Thread thread = new Thread(new MyRunnable(accessType));
            threads.add(thread);
            thread.start();
        }
    }

    static void removeThread() {
        if (!threads.isEmpty()) {
            Thread thread = threads.remove(0);
            thread.interrupt();
        } else {
            System.out.println("Немає потоків для видалення.");
        }
    }

    static void changeNumOfThreads(int accessType) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введіть нову кількість потоків:");
        int newNumOfThreads = validateNonNegativeInput(scanner);

        if (newNumOfThreads > threads.size()) {
            int diff = newNumOfThreads - threads.size();
            createThreads(diff, accessType);
            System.out.println(diff + " потоків додано.");
        } else if (newNumOfThreads < threads.size()) {
            int diff = threads.size() - newNumOfThreads;
            for (int i = 0; i < diff; i++) {
                removeThread();
            }
            System.out.println(diff + " потоків видалено.");
        } else {
            System.out.println("Кількість потоків залишена без змін.");
        }
    }

    static void changeThreadPriority() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Оберіть номер потоку для зміни пріоритету (від 0 до " + (threads.size() - 1) + "):");
        int threadIndex = validateNonNegativeInput(scanner);

        if (threadIndex >= 0 && threadIndex < threads.size()) {
            Thread thread = threads.get(threadIndex);
            System.out.println("Введіть новий пріоритет (від 1 до 10):");
            int newPriority = validateNonNegativeInput(scanner);

            thread.setPriority(newPriority);
            System.out.println("Пріоритет потоку " + threadIndex + " змінено на " + newPriority);
        } else {
            System.out.println("Невірний індекс потоку.");
        }
    }

    static void showCurrentState() {
        System.out.println("Значення змінної: " + variable);

        System.out.println("Інформація про потоки:");
        for (int i = 0; i < threads.size(); i++) {
            Thread thread = threads.get(i);
            System.out.println("Потік " + i + ": Пріоритет - " + thread.getPriority());
        }
    }


    static class MyRunnable implements Runnable {
        private volatile int accessType;
        private volatile boolean shouldStop = false;
        private volatile long startTime = System.currentTimeMillis();

        MyRunnable(int accessType) {
            this.accessType = accessType;
        }

        @Override
        public void run() {
            while (!shouldStop) {
                if (accessType == 1) { // Синхронізований доступ
                    syncIncrementVariable();
                } else if (accessType == 2) { // Асинхронізований доступ
                    incrementVariable();
                }
                try {
                    Thread.sleep(100); // Затримка для візуалізації зменшення змінної
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            showExecutionTime(startTime);
        }

        private void incrementVariable() {
            if (variable < 1000) {
                variable++;
            }
            else shouldStop = true;
        }
        private synchronized void syncIncrementVariable() {
            if (variable < 1000) {
                variable++;
            }
            else shouldStop = true;
        }
    }

    static void showExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Час виконання: " + executionTime + " мс");
    }

    static int validateNonNegativeInput(Scanner scanner) {
        int number;
        do {
            while (!scanner.hasNextInt()) {
                System.out.println("Це не число. Спробуйте знову:");
                scanner.next();
            }
            number = scanner.nextInt();
            if (number < 0) {
                System.out.println("Число має бути невід'ємним.");
            }
        } while (number < 0);
        return number;
    }

}


