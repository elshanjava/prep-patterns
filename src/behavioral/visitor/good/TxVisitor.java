package behavioral.visitor.good;

interface TxVisitor<R> {
    R visit(Transfer t);
    R visit(Fee f);
    R visit(Refund r);
    R visit(Settlement s);  // новый тип: добавить метод сюда → компилятор укажет всем реализациям
}
