package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.api.responses.meta.MetaCore;

public abstract class AnyDocument<T extends MetaCore> {

    @Schema
    public T meta;

}
