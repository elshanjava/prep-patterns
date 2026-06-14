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
import behavioral.chainofresponsibility.bad.BadDemo;
import behavioral.chainofresponsibility.good.ChainOfResponsibilityDemo;
import behavioral.command.bad.BadCommandDemo;
import behavioral.command.good.CommandDemo;
import behavioral.interpreter.bad.BadInterpreterDemo;
import behavioral.interpreter.good.InterpreterDemo;
import behavioral.iterator.bad.BadIteratorDemo;
import behavioral.iterator.good.IteratorDemo;
import behavioral.mediator.bad.BadMediatorDemo;
import behavioral.mediator.good.MediatorDemo;
import behavioral.memento.bad.BadMementoDemo;
import behavioral.memento.good.MementoDemo;
import behavioral.observer.bad.BadObserverDemo;
import behavioral.observer.good.ObserverDemo;
import behavioral.state.bad.BadStateDemo;
import behavioral.state.good.StateDemo;
import behavioral.strategy.bad.BadStrategyDemo;
import behavioral.strategy.good.StrategyDemo;
import behavioral.templatemethod.bad.BadTemplateMethodDemo;
import behavioral.templatemethod.good.TemplateMethodDemo;
import behavioral.visitor.bad.BadVisitorDemo;
import behavioral.visitor.good.VisitorDemo;
import concurrent.threadpool.bad.BadThreadPoolDemo;
import concurrent.threadpool.good.ThreadPoolDemo;
import concurrent.producerconsumer.bad.BadProducerConsumerDemo;
import concurrent.producerconsumer.good.ProducerConsumerDemo;
import concurrent.completablefuture.bad.BadCompletableFutureDemo;
import concurrent.completablefuture.good.CompletableFutureDemo;
import concurrent.circuitbreaker.bad.BadCircuitBreakerDemo;
import concurrent.circuitbreaker.good.CircuitBreakerDemo;
import concurrent.retry.bad.BadRetryDemo;
import concurrent.retry.good.RetryDemo;
import concurrent.readwritelock.bad.BadReadWriteLockDemo;
import concurrent.readwritelock.good.ReadWriteLockDemo;

// Прогоняет демонстрацию каждого паттерна (bad + good).
// Каждый *Demo также запускается отдельно — у него свой main().
public class Main {
    public static void main(String[] args) throws Exception {
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

        // --- Behavioral ---
        BadDemo.main(args);               ChainOfResponsibilityDemo.main(args);
        BadCommandDemo.main(args);        CommandDemo.main(args);
        BadInterpreterDemo.main(args);    InterpreterDemo.main(args);
        BadIteratorDemo.main(args);       IteratorDemo.main(args);
        BadMediatorDemo.main(args);       MediatorDemo.main(args);
        BadMementoDemo.main(args);        MementoDemo.main(args);
        BadObserverDemo.main(args);       ObserverDemo.main(args);
        BadStateDemo.main(args);          StateDemo.main(args);
        BadStrategyDemo.main(args);       StrategyDemo.main(args);
        BadTemplateMethodDemo.main(args); TemplateMethodDemo.main(args);
        BadVisitorDemo.main(args);        VisitorDemo.main(args);

        // --- Concurrent ---
        BadThreadPoolDemo.main(args);         ThreadPoolDemo.main(args);
        BadProducerConsumerDemo.main(args);   ProducerConsumerDemo.main(args);
        BadCompletableFutureDemo.main(args);  CompletableFutureDemo.main(args);
        BadCircuitBreakerDemo.main(args);     CircuitBreakerDemo.main(args);
        BadRetryDemo.main(args);              RetryDemo.main(args);
        BadReadWriteLockDemo.main(args);      ReadWriteLockDemo.main(args);
    }
}
