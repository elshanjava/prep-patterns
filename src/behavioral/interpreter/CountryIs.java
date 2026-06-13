package behavioral.interpreter;

record CountryIs(String code) implements Rule {
    public boolean interpret(Tx tx) {
        return tx.country().equals(code);
    }
}
