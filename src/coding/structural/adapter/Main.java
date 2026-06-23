package coding.structural.adapter;

import java.util.logging.Level;

public class Main {

    public static void main(String[] args) {
        // Чужой класс с неудобным API: writeLog(String, int) — мыслит числовой серьёзностью
        ThirdPartyLog legacy = new ThirdPartyLog();

        // Оборачиваем его в адаптер — снаружи это обычный Logger
        Logger logger = new ThirdPartyLogAdapter(legacy);

        // Клиент знает только про Logger (Level + message), про ThirdPartyLog не подозревает
        run(logger);
    }

    // Клиентский код зависит ТОЛЬКО от Target-интерфейса Logger.
    // Адаптер прозрачно переводит Level -> int severity под капотом.
    private static void run(Logger logger) {
        logger.log(Level.INFO, "service started");
        logger.log(Level.WARNING, "disk almost full");
        logger.log(Level.SEVERE, "out of memory");
    }
}
