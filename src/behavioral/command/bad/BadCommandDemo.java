package behavioral.command.bad;

public class BadCommandDemo {
    public static void main(String[] args) {
        System.out.println("== Command [BAD] ==");
        var service = new BadTransferService();
        service.transfer("acc-A", "acc-B", 500);
        // операцию нельзя: поставить в очередь, повторить, залогировать, откатить
    }
}
