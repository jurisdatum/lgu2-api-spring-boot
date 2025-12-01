package uk.gov.legislation.util;

public enum Stage {

    CONSULTATION("Consultation"),
    DEVELOPMENT("Development"),
    ENACTMENT("Enactment"),
    FINAL("Final"),
    IMPLEMENTATION("Implementation"),
    OPTIONS("Options"),
    POST_IMPLEMENTATION("Post Implementation");


    private final String value;

    Stage(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static String safeStage(Stage stage) {
        return stage == null ? null : stage.value();
    }
}
