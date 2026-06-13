package behavioral.interpreter.good;

import behavioral.interpreter.model.Tx;

record CountryIs(String code) implements Rule {
    public boolean interpret(Tx tx) {
        return tx.country().equals(code);
    }
}
