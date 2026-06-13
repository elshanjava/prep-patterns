package behavioral.command.good;

interface Command {
    void execute();
    default void undo() { throw new UnsupportedOperationException(); }
}
