package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.List;

public class PageOfEffects {

    @Schema
    public PageMetadata meta;

    @Schema
    public List<Effect> effects;

    public static class PageMetadata {

        @Schema(example = "1")
        public int page;

        @Schema(example = "20")
        public int pageSize;

        @Schema
        public int totalPages;

        @Schema
        public int startIndex;

        @Schema
        public int totalResults;

        @Schema
        public ZonedDateTime updated;

    }

}
