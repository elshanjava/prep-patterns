package behavioral.command;

interface Command {
    void execute();
    default void undo() { throw new UnsupportedOperationException(); }
}
