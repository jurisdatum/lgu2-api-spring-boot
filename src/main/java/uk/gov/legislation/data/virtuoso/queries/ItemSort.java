package uk.gov.legislation.data.virtuoso.queries;

import uk.gov.legislation.data.virtuoso.jsonld.Item;
import uk.gov.legislation.data.virtuoso.jsonld.ValueAndLanguage;

import java.util.Comparator;
import java.util.List;

public enum ItemSort {

    NUMBER_ASC("ASC(?number)"),
    NUMBER_DESC("DESC(?number)"),
    YEAR_ASC("ASC(?year) ASC(?number)"),
    YEAR_DESC("DESC(?year) ASC(?number)"),
    CITATION_ASC("ASC(?citation)"),
    CITATION_DESC("DESC(?citation)"),
    TITLE_ASC("ASC(?title)"),
    TITLE_DESC("DESC(?title)");

    private final String sparql;

    ItemSort(String s) {
        this.sparql = s;
    }

    public String sparql() {
        return sparql;
    }

    private static final Comparator<Item> YEAR_REVERSED = Comparator
        .comparingInt((Item item) -> item.year)
        .reversed();

    public static final Comparator<Item> NUMBER_ASC_COMP = Comparator
        .comparingInt((Item item) -> item.number)
        .thenComparing(YEAR_REVERSED);

    public static final Comparator<Item> NUMBER_DESC_COMP = Comparator
        .comparingInt((Item item) -> item.number)
        .reversed()
        .thenComparing(YEAR_REVERSED);

    public static final Comparator<Item> YEAR_ASC_COMP = Comparator
        .comparingInt((Item item) -> item.year)
        .thenComparingInt(item -> item.number);

    public static final Comparator<Item> YEAR_DESC_COMP = YEAR_REVERSED
        .thenComparingInt(item -> item.number);

    private static final Comparator<List<ValueAndLanguage>> ENGLISH_FIRST =
        Comparator.comparing(
            list -> {
                if (list.isEmpty()) return null;
                ValueAndLanguage max = list.stream()
                    .max(Comparator.comparing(v -> v.language))
                    .get();
                return max.value;
            },
            Comparator.nullsLast(Comparator.naturalOrder())
        );

    public static final Comparator<Item> CITATION_ASC_COMP = Comparator
        .comparing(item -> item.citation, ENGLISH_FIRST);

    public static final Comparator<Item> CITATION_DESC_COMP = CITATION_ASC_COMP
        .reversed();

    public static final Comparator<Item> TITLE_ASC_COMP = Comparator
        .comparing(item -> item.title, ENGLISH_FIRST);

    public static final Comparator<Item> TITLE_DESC_COMP = TITLE_ASC_COMP
        .reversed();

    public static Comparator<Item> comparator(ItemSort sort) {
        return switch (sort) {
            case null -> YEAR_DESC_COMP;
            case NUMBER_ASC -> NUMBER_ASC_COMP;
            case NUMBER_DESC -> NUMBER_DESC_COMP;
            case YEAR_ASC -> YEAR_ASC_COMP;
            case YEAR_DESC -> YEAR_DESC_COMP;
            case CITATION_ASC -> CITATION_ASC_COMP;
            case CITATION_DESC -> CITATION_DESC_COMP;
            case TITLE_ASC -> TITLE_ASC_COMP;
            case TITLE_DESC -> TITLE_DESC_COMP;
        };
    }

}
