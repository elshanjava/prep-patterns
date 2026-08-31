package pattern.behavioral.interpreter.good;

import pattern.behavioral.interpreter.model.Tx;

record CountryIs(String code) implements Rule {
    public boolean interpret(Tx tx) {
        return tx.country().equals(code);
    }
}
