package coding.structural.decorator;

public class Main {

    public static void main(String[] args) {
        String raw = "  hello world  ";

        // 1. Голая база — ничего не делает, отдаёт как есть
        TextProcessor plain = new PlainText();
        System.out.println("plain:        [" + plain.process(raw) + "]");

        // 2. Один слой: Trim оборачивает PlainText
        TextProcessor trimmed = new Trim(new PlainText());
        System.out.println("trim:         [" + trimmed.process(raw) + "]");

        // 3. Два слоя: сначала upper, снаружи trim
        //    process идёт изнутри наружу: PlainText -> ToUpperCase -> Trim
        TextProcessor upperThenTrim = new Trim(new ToUpperCase(new PlainText()));
        System.out.println("upper+trim:   [" + upperThenTrim.process(raw) + "]");

        // 4. Порядок слоёв можно менять — тот же набор, другой результат-нюанс
        TextProcessor trimThenUpper = new ToUpperCase(new Trim(new PlainText()));
        System.out.println("trim+upper:   [" + trimThenUpper.process(raw) + "]");

        // 5. Глубокая вложенность руками — предела нет (матрёшка из многих слоёв)
        TextProcessor deep =
                new Trim(new ToUpperCase(new Trim(new ToUpperCase(
                new Trim(new ToUpperCase(new Trim(new ToUpperCase(
                new Trim(new ToUpperCase(new PlainText()))))))))));
        System.out.println("deep(10):     [" + deep.process(raw) + "]");

        // 6. То же самое, но собрано ЦИКЛОМ — так строят в реальном коде
        //    (руками 10 скобок никто не пишет — слои навешивают из списка)
        TextProcessor pipeline = new PlainText();          // старт: голая база
        for (int i = 0; i < 5; i++) {
            pipeline = new Trim(new ToUpperCase(pipeline)); // навешиваем слой за слоем
        }
        System.out.println("loop(10):     [" + pipeline.process(raw) + "]");
    }
}
