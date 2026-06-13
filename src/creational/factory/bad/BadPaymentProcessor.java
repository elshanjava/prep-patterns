package creational.factory.bad;

// Клиент сам решает что создавать — все if/else сидят здесь.
// Добавить CRYPTO: найди все места в коде где есть такой switch и добавь ветку.
// Логика обработки перемешана с логикой выбора типа — нарушение SRP.
final class BadPaymentProcessor {

    void handle(String method, long amount) {
        if ("CARD".equals(method)) {
            System.out.println("[card] validating CVV and expiry");
            System.out.println("[card] running 3DS authentication");
            System.out.println("[card] charging " + amount + " via card network");
            System.out.println("[card] notifying issuer bank");

        } else if ("SEPA".equals(method)) {
            System.out.println("[sepa] validating IBAN checksum");
            System.out.println("[sepa] checking SEPA mandate");
            System.out.println("[sepa] initiating T+1 bank transfer of " + amount);

        } else if ("CRYPTO".equals(method)) {
            System.out.println("[crypto] validating wallet address");
            System.out.println("[crypto] broadcasting transaction of " + amount);
            System.out.println("[crypto] waiting for 3 confirmations");

        } else {
            throw new IllegalArgumentException("unknown method: " + method);
        }
        // Добавить Apple Pay? Добавить ещё один else-if везде, где есть этот блок.
        // Нельзя тестировать CARD-логику в изоляции от SEPA-логики.
        // Нельзя подменить алгоритм без правки этого класса.
    }
}
