package coding.behavioral.interpreter;

public record CountryIs(String country) implements Expr {

    @Override
    public boolean evaluate(Tx ctx) {
        return country.equals(ctx.country());
    }
}
