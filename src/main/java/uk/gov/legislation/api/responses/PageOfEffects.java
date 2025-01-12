package uk.gov.legislation.api.responses;

import java.time.ZonedDateTime;
import java.util.List;

public class PageOfEffects {

    public PageMetadata meta;

    public List<Effect> effects;

    public static class PageMetadata {

        public int page;

        public int pageSize;

        public int totalPages;

        public ZonedDateTime updated;

    }

}
