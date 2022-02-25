package ripmanager.engine.enums;

public enum Language {
    ITALIAN("Italian", "it"),
    ENGLISH("English", "en"),
    JAPANESE("Japanese", "jp");

    private final String name;
    private final String code;

    Language(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static Language findByName(String name) {
        for (Language lang : values()) {
            if (lang.name.equalsIgnoreCase(name)) {
                return lang;
            }
        }
        return null;
    }

}
