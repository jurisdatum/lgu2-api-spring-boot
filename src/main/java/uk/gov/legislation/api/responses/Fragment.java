package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;

public class Fragment {

    @Schema
    public FragmentMetadata meta;

    @Schema
    public String html;

    public Fragment(FragmentMetadata meta, String html) {
        this.meta = meta;
        this.html = html;
    }

}
