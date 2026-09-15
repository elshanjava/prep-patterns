package tdd.zpractice.urlshortener;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String code) {
        super("no URL found for code: " + code);
    }
}
