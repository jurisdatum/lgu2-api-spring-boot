package uk.gov.legislation.api.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.util.Type;

@Schema(name = "DocumentType")
public class Wrapper {

    private final Type type;

    public Wrapper(Type type) {
        this.type = type;
    }

    @JsonProperty(index = 1)
    @Schema(allowableValues = { "ukpga", "uksi", "wsi", "nisi" })
    public String shortName() {
        return type.shortName();
    }

    @JsonProperty(index = 2)
    @Schema(ref = "#/components/schemas/LongType")
//    @Schema(allowableValues = { "UnitedKingdomPublicGeneralAct", "UnitedKingdomStatutoryInstrument", "WelshStatutoryInstrument", "NorthernIrelandOrderInCouncil" })
    public String longName() {
        return type.longName();
    }

    @JsonProperty(index = 3)
    @Schema(allowableValues = { "primary", "secondary" })
    public String category() {
        return type.category().name().toLowerCase();
    }

}
