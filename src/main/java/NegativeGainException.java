class NegativeGainException extends RuntimeException {
    public NegativeGainException() {
        super("The negative gain constraint have been violated");
    }

    public NegativeGainException(String message) {
        super(message);
    }

}