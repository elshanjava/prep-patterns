package behavioral.visitor;

interface TxNode {
    <R> R accept(TxVisitor<R> v);
}
