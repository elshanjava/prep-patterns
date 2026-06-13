import creational.abstractfactory.bad.BadAbstractFactoryDemo;
import creational.abstractfactory.good.AbstractFactoryDemo;
import creational.builder.bad.BadBuilderDemo;
import creational.builder.good.BuilderDemo;
import creational.factory.bad.BadFactoryDemo;
import creational.factory.good.FactoryDemo;
import creational.prototype.bad.BadPrototypeDemo;
import creational.prototype.good.PrototypeDemo;
import creational.singleton.bad.BadSingletonDemo;
import creational.singleton.good.SingletonDemo;
import structural.adapter.bad.BadAdapterDemo;
import structural.adapter.good.AdapterDemo;
import structural.bridge.bad.BadBridgeDemo;
import structural.bridge.good.BridgeDemo;
import structural.composite.bad.BadCompositeDemo;
import structural.composite.good.CompositeDemo;
import structural.decorator.bad.BadDecoratorDemo;
import structural.decorator.good.DecoratorDemo;
import structural.facade.bad.BadFacadeDemo;
import structural.facade.good.FacadeDemo;
import structural.flyweight.bad.BadFlyweightDemo;
import structural.flyweight.good.FlyweightDemo;
import structural.proxy.bad.BadProxyDemo;
import structural.proxy.good.ProxyDemo;

// Прогоняет демонстрацию каждого паттерна (bad + good).
// Каждый *Demo также запускается отдельно — у него свой main().
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // --- Creational ---
        BadSingletonDemo.main(args);       SingletonDemo.main(args);
        BadFactoryDemo.main(args);         FactoryDemo.main(args);
        BadBuilderDemo.main(args);         BuilderDemo.main(args);
        BadPrototypeDemo.main(args);       PrototypeDemo.main(args);
        BadAbstractFactoryDemo.main(args); AbstractFactoryDemo.main(args);

        // --- Structural ---
        BadAdapterDemo.main(args);    AdapterDemo.main(args);
        BadBridgeDemo.main(args);     BridgeDemo.main(args);
        BadCompositeDemo.main(args);  CompositeDemo.main(args);
        BadDecoratorDemo.main(args);  DecoratorDemo.main(args);
        BadFacadeDemo.main(args);     FacadeDemo.main(args);
        BadFlyweightDemo.main(args);  FlyweightDemo.main(args);
        BadProxyDemo.main(args);      ProxyDemo.main(args);
    }
}
