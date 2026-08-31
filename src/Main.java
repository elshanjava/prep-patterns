import pattern.creational.abstractfactory.bad.BadAbstractFactoryDemo;
import pattern.creational.abstractfactory.good.AbstractFactoryDemo;
import pattern.creational.builder.bad.BadBuilderDemo;
import pattern.creational.builder.good.BuilderDemo;
import pattern.creational.factory.bad.BadFactoryDemo;
import pattern.creational.factory.good.FactoryDemo;
import pattern.creational.prototype.bad.BadPrototypeDemo;
import pattern.creational.prototype.good.PrototypeDemo;
import pattern.creational.singleton.bad.BadSingletonDemo;
import pattern.creational.singleton.good.SingletonDemo;
import pattern.structural.adapter.bad.BadAdapterDemo;
import pattern.structural.adapter.good.AdapterDemo;
import pattern.structural.bridge.bad.BadBridgeDemo;
import pattern.structural.bridge.good.BridgeDemo;
import pattern.structural.composite.bad.BadCompositeDemo;
import pattern.structural.composite.good.CompositeDemo;
import pattern.structural.decorator.bad.BadDecoratorDemo;
import pattern.structural.decorator.good.DecoratorDemo;
import pattern.structural.facade.bad.BadFacadeDemo;
import pattern.structural.facade.good.FacadeDemo;
import pattern.structural.flyweight.bad.BadFlyweightDemo;
import pattern.structural.flyweight.good.FlyweightDemo;
import pattern.structural.proxy.bad.BadProxyDemo;
import pattern.structural.proxy.good.ProxyDemo;
import pattern.behavioral.chainofresponsibility.bad.BadDemo;
import pattern.behavioral.chainofresponsibility.good.ChainOfResponsibilityDemo;
import pattern.behavioral.command.bad.BadCommandDemo;
import pattern.behavioral.command.good.CommandDemo;
import pattern.behavioral.interpreter.bad.BadInterpreterDemo;
import pattern.behavioral.interpreter.good.InterpreterDemo;
import pattern.behavioral.iterator.bad.BadIteratorDemo;
import pattern.behavioral.iterator.good.IteratorDemo;
import pattern.behavioral.mediator.bad.BadMediatorDemo;
import pattern.behavioral.mediator.good.MediatorDemo;
import pattern.behavioral.memento.bad.BadMementoDemo;
import pattern.behavioral.memento.good.MementoDemo;
import pattern.behavioral.observer.bad.BadObserverDemo;
import pattern.behavioral.observer.good.ObserverDemo;
import pattern.behavioral.state.bad.BadStateDemo;
import pattern.behavioral.state.good.StateDemo;
import pattern.behavioral.strategy.bad.BadStrategyDemo;
import pattern.behavioral.strategy.good.StrategyDemo;
import pattern.behavioral.templatemethod.bad.BadTemplateMethodDemo;
import pattern.behavioral.templatemethod.good.TemplateMethodDemo;
import pattern.behavioral.visitor.bad.BadVisitorDemo;
import pattern.behavioral.visitor.good.VisitorDemo;
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
