package uk.gov.legislation.endpoints.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.util.Type;

@Schema(name = "DocumentType")
public class TypeWrapper {

    private final Type type;

    public TypeWrapper(Type type) {
        this.type = type;
    }

    @JsonProperty(index = 1)
    @Schema(allowableValues = {
            "ukpga", "ukla", "ukppa", "uksi", "ukmd", "ukmo", "uksro", "ukdsi",  // UK Acts
            "asc", "wsi", "anaw", "mwa",  // Wales Acts
            "asp", "aosp", "ssi", "sdsi",  // Scotland Acts
            "nia", "aip", "nisr", "nisi", "mnia", "apni", "nisro", "nidsr",  // Northern Ireland Acts
            "apgb", "gbla",  // Great Britain Acts
            "ukcm", "ukci", "aep"  // England Acts
    })
    public String shortName() {
        return type.shortName();
    }

    @JsonProperty(index = 2)
    @Schema(allowableValues = {
            "UnitedKingdomPublicGeneralAct", "UnitedKingdomLocalAct", "UnitedKingdomPrivateOrPersonalAct",
            "UnitedKingdomStatutoryInstrument", "UnitedKingdomMinisterialDirection",
            "UnitedKingdomMinisterialOrder", "UnitedKingdomStatutoryRuleOrOrder", "UnitedKingdomDraftStatutoryInstrument",
            "WelshParliamentAct", "WelshStatutoryInstrument", "WelshNationalAssemblyAct", "WelshAssemblyMeasure",
            "ScottishAct", "ScottishOldAct", "ScottishStatutoryInstrument", "ScottishDraftStatutoryInstrument",
            "NorthernIrelandAct", "IrelandAct", "NorthernIrelandStatutoryRule", "NorthernIrelandOrderInCouncil",
            "NorthernIrelandAssemblyMeasure", "NorthernIrelandParliamentAct", "NorthernIrelandStatutoryRuleOrOrder",
            "NorthernIrelandDraftStatutoryRule",
            "GreatBritainAct", "GreatBritainLocalAct",
            "UnitedKingdomChurchMeasure", "UnitedKingdomChurchInstrument", "EnglandAct"
    })
    public String longName() {
        return type.longName();
    }

    @JsonProperty(index = 3)
    @Schema(allowableValues = { "primary", "secondary" })
    public String category() {
        return type.category().name().toLowerCase();
    }

}
