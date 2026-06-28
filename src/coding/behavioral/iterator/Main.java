package coding.behavioral.iterator;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        Playlist<String> playlist = new Playlist<>(5);
        playlist.add("Bohemian Rhapsody");
        playlist.add("Stairway to Heaven");
        playlist.add("Hotel California");

        // 1. for-each работает, потому что Playlist реализует Iterable.
        //    Клиент не знает, что внутри массив — просто ходит по элементам.
        System.out.println("-- for-each --");
        for (String song : playlist) {
            System.out.println("  " + song);
        }

        // 2. Два независимых итератора по одной коллекции:
        //    у каждого свой pos, они не мешают друг другу.
        System.out.println();
        System.out.println("-- два независимых обхода --");
        Iterator<String> a = playlist.iterator();
        Iterator<String> b = playlist.iterator();
        a.next();                       // a продвинулся на 1
        System.out.println("итератор a -> " + a.next());   // 2-й элемент
        System.out.println("итератор b -> " + b.next());   // b всё ещё с начала -> 1-й

        // 3. next() за пределами бросает NoSuchElementException
        System.out.println();
        System.out.println("-- обход до конца + защита --");
        Iterator<String> it = playlist.iterator();
        while (it.hasNext()) {
            System.out.println("  " + it.next());
        }
        try {
            it.next();                  // элементов больше нет
        } catch (java.util.NoSuchElementException e) {
            System.out.println("  next() после конца -> NoSuchElementException");
        }
    }
}
