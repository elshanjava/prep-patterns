package behavioral.visitor.good;

interface TxVisitor<R> {
    R visit(Transfer t);
    R visit(Fee f);
    R visit(Refund r);
}
