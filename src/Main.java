import creational.abstractfactory.AbstractFactoryDemo;
import creational.builder.BuilderDemo;
import creational.factory.FactoryDemo;
import creational.prototype.PrototypeDemo;
import creational.singleton.SingletonDemo;
import structural.adapter.AdapterDemo;
import structural.bridge.BridgeDemo;
import structural.composite.CompositeDemo;
import structural.decorator.DecoratorDemo;
import structural.facade.FacadeDemo;
import structural.flyweight.FlyweightDemo;
import structural.proxy.ProxyDemo;

// Прогоняет демонстрацию каждого паттерна. Каждый Demo также запускается
// отдельно — у него свой main().
public class Main {
    public static void main(String[] args) {
        AbstractFactoryDemo.main(args);
        BuilderDemo.main(args);
        FactoryDemo.main(args);
        PrototypeDemo.main(args);
        SingletonDemo.main(args);
        AdapterDemo.main(args);
        BridgeDemo.main(args);
        CompositeDemo.main(args);
        DecoratorDemo.main(args);
        FacadeDemo.main(args);
        FlyweightDemo.main(args);
        ProxyDemo.main(args);
    }
}
