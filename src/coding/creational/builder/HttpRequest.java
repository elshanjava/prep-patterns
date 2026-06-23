package coding.creational.builder;

public final class HttpRequest {

    private final String url;
    private final String method;
    private final String header;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.header = builder.header;
    }

    public String getUrl() {return url;}
    public String getMethod() {return method;}
    public String getHeader() {return header;}

    public static Builder builder(String url) {

        return new Builder(url);
    }

    public static final class Builder {
        private final String url;
        private String method;
        private String header;

        private Builder(String url) {
            if (url == null || url.isEmpty()) {
                throw new IllegalArgumentException("url cannot be null or empty: " + url);
            }
            this.url = url;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder header(String header) {
            this.header = header;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }

}
