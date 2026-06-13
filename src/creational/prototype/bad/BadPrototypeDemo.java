package creational.prototype.bad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BadPrototypeDemo {
    public static void main(String[] args) {
        System.out.println("== Prototype [BAD] — поверхностная копия, общее мутабельное состояние ==");
        System.out.println();

        var base = new BadPricingRule(
                new HashMap<>(Map.of(
                        "standard", new BigDecimal("0.029"),
                        "premium",  new BigDecimal("0.019")
                )),
                new ArrayList<>(List.of("KP", "IR"))  // заблокированные страны
        );
        System.out.println("base до копирования: " + base);

        // Воркер-1 получает «копию» для параллельной обработки
        var worker1Copy = base.shallowCopy();
        // Воркер-2 тоже получает «копию»
        var worker2Copy = base.shallowCopy();

        // Воркер-1 добавляет тир для своего клиента — думает, что не трогает оригинал
        worker1Copy.withTier("vip", new BigDecimal("0.009"));
        // Воркер-2 блокирует ещё одну страну — думает, что это локально
        worker2Copy.blockCountry("SY");

        System.out.println();
        System.out.println("worker1 (vip-тир):    " + worker1Copy);
        System.out.println("worker2 (+SY):        " + worker2Copy);
        System.out.println("base ПОСЛЕ:           " + base);  // ← оба изменения попали в base!

        System.out.println();
        System.out.println("Обе копии — тот же объект коллекций:");
        System.out.println("  base.tiers == worker1Copy.tiers? " + (base.tiers == worker1Copy.tiers));
        System.out.println("  base.blockedCountries == worker2Copy.blockedCountries? "
                           + (base.blockedCountries == worker2Copy.blockedCountries));

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  Воркер-1 добавил vip-тир — оригинал тоже получил его (гонка данных)");
        System.out.println("  Воркер-2 заблокировал SY — все остальные «копии» тоже получили блок");
        System.out.println("  withTier() возвращает тот же объект вместо нового — API лжёт");
        System.out.println("  Параллельная обработка: ConcurrentModificationException или тихая порча данных");
    }
}
