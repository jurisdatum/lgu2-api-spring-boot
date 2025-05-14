package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

import static uk.gov.legislation.data.virtuoso.defra.DefraLex.FACET_QUERY;

public class ChapterFacets {

    public record ChapterCount(String chapter, String label, int count) {}

    private static List<ChapterCount> parseChapterCounts(ArrayNode bindings) {
        return StreamSupport.stream(bindings.spliterator(), false)
            .map(binding -> {
                String chapter = binding.get("chapter").get("value").textValue();
                chapter = chapter.substring(chapter.lastIndexOf('/') + 1);
                return new ChapterCount(
                    chapter,
                    binding.get("label").get("value").textValue(),
                    Integer.parseInt(binding.get("cnt").get("value").textValue())
                );
            })
            .sorted(Comparator.comparingInt(ChapterCount::count).reversed())
            .toList();
    }

    static CompletableFuture<List<ChapterCount>> fetchChapterCounts(DefraLex defra, String baseWhere) {
        String where = baseWhere +
            " ?item <http://defra-lex.legislation.gov.uk/def/chapter> ?chapter ." +
            " ?chapter  rdfs:label  ?label .";
        String query = FACET_QUERY.formatted(
            "?chapter ?label",  // facet variable(s)
            where,
            "?chapter ?label"  // group-by variable(s)
        );
        return defra.getSparqlBindings(query)
            .thenApply(ChapterFacets::parseChapterCounts);
    }

}
