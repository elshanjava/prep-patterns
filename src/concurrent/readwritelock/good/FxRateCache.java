package concurrent.readwritelock.good;

import concurrent.readwritelock.model.FxRate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// ReentrantReadWriteLock: несколько читателей одновременно, один writer эксклюзивно.
// FX-кэш читается тысячи раз в секунду, обновляется раз в секунду — идеальный кейс.
// ReadLock не мешает другим ReadLock; WriteLock ждёт завершения всех ReadLock.
final class FxRateCache {
    private final Map<String, FxRate>  cache = new HashMap<>();
    private final ReentrantReadWriteLock rwl  = new ReentrantReadWriteLock();
    static final AtomicInteger concurrentReads = new AtomicInteger();

    // AtomicInteger, а НЕ volatile int: "прочитал -> посчитал max -> записал" — составная
    // операция, и volatile её не защищает. Под read lock, который РАЗДЕЛЯЕМЫЙ, сюда
    // одновременно заходят все читатели, и часть обновлений терялась бы.
    static final AtomicInteger maxConcurrentReads = new AtomicInteger();

    static final long T0 = System.currentTimeMillis();
    static void log(String m) {
        System.out.printf("  t=%4dms  [%s] %s%n",
                System.currentTimeMillis() - T0, Thread.currentThread().getName(), m);
    }

    FxRate getRate(String pair) {
        rwl.readLock().lock();
        try {
            int n = concurrentReads.incrementAndGet();
            maxConcurrentReads.accumulateAndGet(n, Math::max);
            Thread.sleep(5); // симуляция чтения
            return cache.get(pair);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            concurrentReads.decrementAndGet();
            rwl.readLock().unlock();
        }
    }

    void updateRate(String pair, double rate) {
        rwl.writeLock().lock();
        try {
            cache.put(pair, new FxRate(pair, rate, Instant.now()));
            Thread.sleep(40);                       // симуляция записи
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwl.writeLock().unlock();
        }
    }

    // Версия с логами — для фазы, где видно ожидание на замке.
    void updateRateLogged(String pair, double rate) {
        log("хочу writeLock (активных читателей: " + concurrentReads.get() + ")");
        rwl.writeLock().lock();
        try {
            log("ВЗЯЛ writeLock — все читатели отпустили");
            cache.put(pair, new FxRate(pair, rate, Instant.now()));
            Thread.sleep(40);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwl.writeLock().unlock();
            log("отпустил writeLock");
        }
    }

    FxRate getRateLogged(String pair) {
        log("хочу readLock");
        rwl.readLock().lock();
        try {
            log("ВЗЯЛ readLock");
            int n = concurrentReads.incrementAndGet();
            maxConcurrentReads.accumulateAndGet(n, Math::max);
            Thread.sleep(5);
            return cache.get(pair);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            concurrentReads.decrementAndGet();
            rwl.readLock().unlock();
        }
    }

    // ГЛАВНАЯ ловушка ReentrantReadWriteLock: апгрейд read -> write НЕВОЗМОЖЕН.
    // writeLock ждёт, пока отпустят ВСЕ читатели — включая нас самих. С lock() это
    // вечное ожидание; tryLock показывает отказ, не подвешивая демо.
    boolean tryUpgradeWhileReading() throws InterruptedException {
        rwl.readLock().lock();
        try {
            return rwl.writeLock().tryLock(200, TimeUnit.MILLISECONDS);   // всегда false
        } finally {
            rwl.readLock().unlock();
        }
    }

    // А обратный ход — ДАУНГРЕЙД write -> read — разрешён и полезен:
    // изменили значение и, ни на миг не отпуская защиту, дочитали согласованное состояние.
    FxRate updateAndRead(String pair, double rate) {
        rwl.writeLock().lock();
        try {
            cache.put(pair, new FxRate(pair, rate, Instant.now()));
            rwl.readLock().lock();       // берём read, ЕЩЁ держа write — это законно
        } finally {
            rwl.writeLock().unlock();    // отпускаем write, read остаётся за нами
        }
        try {
            return cache.get(pair);
        } finally {
            rwl.readLock().unlock();
        }
    }
}
