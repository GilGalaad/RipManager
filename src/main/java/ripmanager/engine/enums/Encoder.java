package ripmanager.engine.enums;

public enum Encoder {
    AUTODETECT("Autodetect"),
    X264("x264"),
    X265("x265");

    private final String label;

    Encoder(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
