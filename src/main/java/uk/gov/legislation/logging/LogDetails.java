package uk.gov.legislation.logging;

public record LogDetails(
    String methodName,
    long durationMillis,
    String message,
    String startTime,
    String endTime,
    String apiEndpoint
) {
    public static String createCustomLogMessage(String methodName, String endpoint, long duration) {
        return switch (methodName) {

            // Category 1: CLML To Other Format Transformation
            case "clml2document", "clml2akn", "clml2html", "clml2docx", "clml2toc" ->
                "Transformation from %s completed in %d ms."
                    .formatted(methodName, duration);

            // MarkLogic Database operation
            case "get" ->
                "MarkLogic GET request to endpoint [%s] completed in %d ms."
                    .formatted(endpoint, duration);

            // Virtuoso Database operation
            case "query" ->
                "Virtuoso Database operation for method [%s] completed in %d ms."
                    .formatted(methodName, duration);

            default -> "%s took %d ms".formatted(methodName, duration);
        };
    }
}



