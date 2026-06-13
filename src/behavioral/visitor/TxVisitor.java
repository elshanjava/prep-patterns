package behavioral.visitor;

interface TxVisitor<R> {
    R visit(Transfer t);
    R visit(Fee f);
    R visit(Refund r);
}
