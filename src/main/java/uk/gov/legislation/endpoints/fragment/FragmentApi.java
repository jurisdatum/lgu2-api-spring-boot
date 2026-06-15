package uk.gov.legislation.endpoints.fragment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Locale;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.legislation.api.parameters.Monarch;
import uk.gov.legislation.api.parameters.Number;
import uk.gov.legislation.api.parameters.Section;
import uk.gov.legislation.api.parameters.Type;
import uk.gov.legislation.api.parameters.Version;
import uk.gov.legislation.api.parameters.Year;
import uk.gov.legislation.api.parameters.Years;
import uk.gov.legislation.api.responses.Fragment;

/** API interface for accessing document fragments in various formats. */
@Tag(name = "Document fragments")
public interface FragmentApi {

    /* CLML */

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "application/xml")
    @Operation(
            summary = "get a document fragment, e.g., a section (calendar year)",
            parameters = {
                @Parameter(
                        name = "Accept-Language",
                        description = "language of the document",
                        in = ParameterIn.HEADER,
                        schema =
                                @Schema(
                                        type = "string",
                                        allowableValues = {"en", "cy"},
                                        examples = "en"))
            })
    ResponseEntity<StreamingResponseBody> getFragmentClml(
            @PathVariable @Type String type,
            @PathVariable @Year int year,
            @PathVariable @Number int number,
            @PathVariable @Section String section,
            @RequestParam @Version Optional<String> version,
            Locale locale);

    @GetMapping(
            value = "/fragment/{type}/{monarch}/{years}/{number}/{section}",
            produces = "application/xml")
    @Operation(
            summary = "get a document fragment, e.g., a section (regnal year)",
            parameters = {
                @Parameter(
                        name = "Accept-Language",
                        description = "language of the document",
                        in = ParameterIn.HEADER,
                        schema =
                                @Schema(
                                        type = "string",
                                        allowableValues = {"en", "cy"},
                                        examples = "en"))
            })
    ResponseEntity<StreamingResponseBody> getFragmentClml(
            @PathVariable @Type String type,
            @PathVariable @Monarch String monarch,
            @PathVariable @Years String years,
            @PathVariable @Number int number,
            @PathVariable @Section String section,
            @RequestParam @Version Optional<String> version,
            Locale locale);

    /* Akoma Ntoso */

    @GetMapping(
            value = "/fragment/{type}/{year}/{number}/{section}",
            produces = "application/akn+xml")
    ResponseEntity<StreamingResponseBody> getFragmentAkn(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version,
            Locale locale);

    @GetMapping(
            value = "/fragment/{type}/{monarch}/{years}/{number}/{section}",
            produces = "application/akn+xml")
    ResponseEntity<StreamingResponseBody> getFragmentAkn(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version,
            Locale locale);

    /* HTML5 */

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "text/html")
    ResponseEntity<StreamingResponseBody> getFragmentHtml(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version,
            Locale locale);

    @GetMapping(
            value = "/fragment/{type}/{monarch}/{years}/{number}/{section}",
            produces = "text/html")
    ResponseEntity<StreamingResponseBody> getFragmentHtml(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version,
            Locale locale);

    /* JSON */

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "application/json")
    ResponseEntity<Fragment> getFragmentJson(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version,
            Locale locale)
            throws Exception;

    @GetMapping(
            value = "/fragment/{type}/{monarch}/{years}/{number}/{section}",
            produces = "application/json")
    ResponseEntity<Fragment> getFragmentJson(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version,
            Locale locale)
            throws Exception;

    /* Word (.docx) */

    @GetMapping(
            value = "/fragment/{type}/{year}/{number}/{section}",
            produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    ResponseEntity<StreamingResponseBody> docx(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version,
            Locale locale);

    @GetMapping(
            value = "/fragment/{type}/{monarch}/{years}/{number}/{section}",
            produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    ResponseEntity<StreamingResponseBody> docx(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version,
            Locale locale);

    /* HEAD (existence check) */

    @RequestMapping(
            value = "/fragment/{type}/{year}/{number}/{section}",
            method = RequestMethod.HEAD)
    @Operation(summary = "check whether a document fragment exists (calendar year)")
    ResponseEntity<Void> headFragment(
            @PathVariable @Type String type,
            @PathVariable @Year int year,
            @PathVariable @Number int number,
            @PathVariable @Section String section,
            @RequestParam Optional<String> version,
            Locale locale);

    @RequestMapping(
            value = "/fragment/{type}/{monarch}/{years}/{number}/{section}",
            method = RequestMethod.HEAD)
    @Operation(summary = "check whether a document fragment exists (regnal year)")
    ResponseEntity<Void> headFragment(
            @PathVariable @Type String type,
            @PathVariable @Monarch String monarch,
            @PathVariable @Years String years,
            @PathVariable @Number int number,
            @PathVariable @Section String section,
            @RequestParam Optional<String> version,
            Locale locale);
}
