package behavioral.visitor.good;

interface TxNode {
    <R> R accept(TxVisitor<R> v);
}
