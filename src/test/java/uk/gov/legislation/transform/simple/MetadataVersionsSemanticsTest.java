package uk.gov.legislation.transform.simple;

import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.core.JacksonException;
import uk.gov.legislation.Application;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
class MetadataVersionsSemanticsTest {

    private final Simplify simplifier;

    @Autowired
    MetadataVersionsSemanticsTest(Simplify simplifier) {
        this.simplifier = simplifier;
    }

    @Test
    void versions_finalCurrentOnly_primary_addsEnactedAndProspective() throws SaxonApiException, JacksonException {
        Metadata metadata = simplifier.extractDocumentMetadata(FINAL_PRIMARY_CURRENT_ONLY);
        assertEquals("enacted", metadata.version());
        assertEquals(List.of("enacted", "prospective"), metadata.versions().stream().toList());
    }

    @Test
    void versions_finalCurrentOnly_secondary_addsMadeAndProspective() throws SaxonApiException, JacksonException {
        Metadata metadata = simplifier.extractDocumentMetadata(FINAL_SECONDARY_CURRENT_ONLY);
        assertEquals("made", metadata.version());
        assertEquals(List.of("made", "prospective"), metadata.versions().stream().toList());
    }

    @Test
    void versions_finalCurrentOnly_ministerialOrder_addsCreatedAndProspective() throws SaxonApiException, JacksonException {
        Metadata metadata = simplifier.extractDocumentMetadata(FINAL_MINISTERIAL_ORDER_CURRENT_ONLY);
        assertEquals("created", metadata.version());
        assertEquals(List.of("created", "prospective"), metadata.versions().stream().toList());
    }

    @Test
    void versions_finalCurrentOnly_euDirective_addsAdoptedAndProspective() throws SaxonApiException, JacksonException {
        Metadata metadata = simplifier.extractDocumentMetadata(FINAL_EU_DIRECTIVE_CURRENT_ONLY);
        assertEquals("adopted", metadata.version());
        assertEquals(List.of("adopted", "prospective"), metadata.versions().stream().toList());
    }

    @Test
    void versions_finalCurrentAlongsideDate_ignoresCurrentAndDoesNotAddProspective() throws SaxonApiException, JacksonException {
        Metadata metadata = simplifier.extractDocumentMetadata(FINAL_PRIMARY_CURRENT_AND_DATE);
        assertEquals("enacted", metadata.version());
        assertEquals(List.of("enacted", "2024-01-01"), metadata.versions().stream().toList());
    }

    @Test
    void versions_finalWithoutHasVersion_addsFirstVersionOnly() throws SaxonApiException, JacksonException {
        Metadata metadata = simplifier.extractDocumentMetadata(FINAL_PRIMARY_WITHOUT_HAS_VERSION);
        assertEquals("enacted", metadata.version());
        assertEquals(List.of("enacted"), metadata.versions().stream().toList());
    }

    @Test
    void version_revisedDocumentWhoseTargetIsNotProspective_usesDctValidDate() throws SaxonApiException, JacksonException, IOException {
        String clml = UnappliedEffectsHelper.read("/asp_2025_11/asp-2025-11-2025-08-07.xml");
        Metadata metadata = simplifier.extractDocumentMetadata(clml);
        assertEquals("2025-08-07", metadata.version());
        assertEquals(List.of("enacted", "2025-08-07"), metadata.versions().stream().toList());
    }

    @Test
    void version_revisedFragmentWithProspectiveProvisions_usesProspectiveLabel() throws SaxonApiException, JacksonException, IOException {
        String clml = UnappliedEffectsHelper.read("/asp_2025_11/asp-2025-11-part-1-crossheading-reviews-2025-08-07.xml");
        Metadata metadata = simplifier.extractFragmentMetadata(clml);
        assertEquals("prospective", metadata.version());
        assertEquals(List.of("enacted", "prospective"), metadata.versions().stream().toList());
    }

    @Test
    void versions_unversionedProspectiveRevised_withoutCurrent_addsProspectiveLabel() throws SaxonApiException, JacksonException {
        Metadata metadata = simplifier.extractDocumentMetadata(UNVERSIONED_PROSPECTIVE_REVISED_WITHOUT_CURRENT);
        assertEquals("prospective", metadata.version());
        assertEquals(List.of("enacted", "prospective"), metadata.versions().stream().toList());
    }

    @Test
    void versions_revisedWholeDocumentWithCurrent_addsDctValidAsDocumentVersion() {
        Metadata metadata = new Metadata();
        metadata.status = Metadata.REVISED;
        metadata.longType = "UnitedKingdomPublicGeneralAct";
        metadata.setValid("2024-11-22");
        metadata.setVersions(List.of(Metadata.HasVersionEntry.of(null, "current")));

        assertEquals("2024-11-22", metadata.version());
        assertEquals(List.of("2024-11-22"), metadata.versions().stream().toList());
    }

    @Test
    void versions_prospectiveRevisedWithCurrentAndEnacted_synthesisesProspectiveNotDctValid() {
        Metadata metadata = new Metadata();
        metadata.status = Metadata.REVISED;
        metadata.longType = "UnitedKingdomPublicGeneralAct";
        metadata.prospective = true;
        metadata.setValid("2024-11-22");
        metadata.setVersions(List.of(
            Metadata.HasVersionEntry.of(null, "enacted"),
            Metadata.HasVersionEntry.of(null, "current")));

        assertEquals("prospective", metadata.version());
        assertEquals(List.of("enacted", "prospective"), metadata.versions().stream().toList());
    }

    @Test
    void versions_versionedNonProspectiveFragment_withCurrent_doesNotAddDctValidAsMilestone() throws SaxonApiException, JacksonException {
        Metadata metadata = simplifier.extractFragmentMetadata(VERSIONED_NON_PROSPECTIVE_FRAGMENT_CURRENT_AND_OLDER_DATE);
        assertEquals("2024-10-01", metadata.version());
        assertEquals(List.of("2024-10-01"), metadata.versions().stream().toList());
    }

    @Test
    void version_unversionedFragment_usesLastFragmentMilestoneNotPayloadDate() throws SaxonApiException, JacksonException, IOException {
        String clml = UnappliedEffectsHelper.read("/ukpga_2000_8/ukpga-2000-8-section-91.xml");
        Metadata metadata = simplifier.extractFragmentMetadata(clml);
        assertEquals("2024-01-30", metadata.version());
        assertEquals("2024-01-30", metadata.versions().last());
    }

    @Test
    void versions_welshFragment_filtersToWelshHasVersionLinksOnly() throws SaxonApiException, JacksonException, IOException {
        Metadata metadata = simplifier.extractFragmentMetadata(BilingualFragmentFixtures.welsh());
        assertEquals("2021-02-27", metadata.version());
        assertEquals(
            List.of("made", "2020-12-20", "2020-12-24", "2021-01-09", "2021-01-15", "2021-01-22", "2021-01-29", "2021-02-27"),
            metadata.versions().stream().toList()
        );
    }

    @Test
    void versionedWelshFragment_pointInTimeAndDctValidDoNotOverrideFragmentMilestone() throws SaxonApiException, JacksonException, IOException {
        Metadata metadata = simplifier.extractFragmentMetadata(BilingualFragmentFixtures.welshPointInTime());
        assertEquals(LocalDate.of(2021, 5, 17), metadata.getPointInTime().orElseThrow());
        assertEquals("2021-02-27", metadata.version());
        assertEquals(
            List.of("made", "2020-12-20", "2020-12-24", "2021-01-09", "2021-01-15", "2021-01-22", "2021-01-29", "2021-02-27"),
            metadata.versions().stream().toList()
        );
    }

    @Test
    void versions_matchingLanguageRetainsUntaggedLinksAndFiltersOtherLanguages() {
        Metadata metadata = new Metadata();
        metadata.status = Metadata.REVISED;
        metadata.longType = "WelshStatutoryInstrument";
        metadata.lang = "cy";
        metadata.setVersions(List.of(
            Metadata.HasVersionEntry.of(null, "made"),
            Metadata.HasVersionEntry.of("cy", "2021-02-27"),
            Metadata.HasVersionEntry.of("en", "2022-03-28")));

        assertEquals(List.of("made", "2021-02-27"), metadata.versions().stream().toList());
    }

    @Test
    void versions_unknownLanguageRetainsAllLinks() {
        Metadata metadata = new Metadata();
        metadata.status = Metadata.REVISED;
        metadata.longType = "WelshStatutoryInstrument";
        metadata.setVersions(List.of(
            Metadata.HasVersionEntry.of(null, "made"),
            Metadata.HasVersionEntry.of("cy", "2021-02-27"),
            Metadata.HasVersionEntry.of("en", "2022-03-28")));

        assertEquals(List.of("made", "2021-02-27", "2022-03-28"), metadata.versions().stream().toList());
    }

    @Test
    void versions_stripsRepealedSuffixFromLatestLabel() {
        Metadata metadata = new Metadata();
        metadata.status = Metadata.REVISED;
        metadata.longType = "UnitedKingdomPublicGeneralAct";
        metadata.setVersions(List.of(
            Metadata.HasVersionEntry.of(null, "enacted"),
            Metadata.HasVersionEntry.of(null, "2001-01-01"),
            Metadata.HasVersionEntry.of(null, "2003-01-01 repealed")));
        assertEquals(List.of("enacted", "2001-01-01", "2003-01-01"), metadata.versions().stream().toList());
    }

    @Test
    void version_revisedNoEligibleMilestone_fallsBackToDctValid() {
        Metadata metadata = new Metadata();
        metadata.status = Metadata.REVISED;
        metadata.longType = "UnitedKingdomPublicGeneralAct";
        metadata.setValid("2024-01-01");
        metadata.setVersions(List.of(Metadata.HasVersionEntry.of(null, "prospective")));

        assertEquals("2024-01-01", metadata.version());
        assertEquals(List.of("prospective"), metadata.versions().stream().toList());
    }

    private static final String FINAL_PRIMARY_CURRENT_ONLY = """
        <Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation" DocumentURI="http://www.legislation.gov.uk/nia/2022/21/enacted">
            <Metadata xmlns="http://www.legislation.gov.uk/namespaces/metadata" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:atom="http://www.w3.org/2005/Atom">
                <dc:identifier>http://www.legislation.gov.uk/nia/2022/21/enacted</dc:identifier>
                <dc:title>Test Act</dc:title>
                <dc:publisher>Statute Law Database</dc:publisher>
                <dc:modified>2026-01-01</dc:modified>
                <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/nia/2022/21" title="current"/>
                <PrimaryMetadata>
                    <DocumentClassification>
                        <DocumentCategory Value="primary" />
                        <DocumentMainType Value="NorthernIrelandAct" />
                        <DocumentStatus Value="final" />
                    </DocumentClassification>
                    <Year Value="2022" />
                    <Number Value="21" />
                    <EnactmentDate Date="2022-01-01" />
                </PrimaryMetadata>
            </Metadata>
        </Legislation>
        """;

    private static final String FINAL_SECONDARY_CURRENT_ONLY = """
        <Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation" DocumentURI="http://www.legislation.gov.uk/uksi/2025/3/made">
            <Metadata xmlns="http://www.legislation.gov.uk/namespaces/metadata" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:atom="http://www.w3.org/2005/Atom">
                <dc:identifier>http://www.legislation.gov.uk/uksi/2025/3/made</dc:identifier>
                <dc:title>Test Regulations</dc:title>
                <dc:publisher>Statute Law Database</dc:publisher>
                <dc:modified>2026-01-01</dc:modified>
                <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/uksi/2025/3" title="current"/>
                <SecondaryMetadata>
                    <DocumentClassification>
                        <DocumentCategory Value="secondary" />
                        <DocumentMainType Value="UnitedKingdomStatutoryInstrument" />
                        <DocumentStatus Value="final" />
                    </DocumentClassification>
                    <Year Value="2025" />
                    <Number Value="3" />
                    <Made Date="2025-01-01" />
                </SecondaryMetadata>
            </Metadata>
        </Legislation>
        """;

    private static final String FINAL_PRIMARY_CURRENT_AND_DATE = """
        <Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation" DocumentURI="http://www.legislation.gov.uk/ukla/2017/1/enacted">
            <Metadata xmlns="http://www.legislation.gov.uk/namespaces/metadata" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:atom="http://www.w3.org/2005/Atom">
                <dc:identifier>http://www.legislation.gov.uk/ukla/2017/1/enacted</dc:identifier>
                <dc:title>Test Local Act</dc:title>
                <dc:publisher>Statute Law Database</dc:publisher>
                <dc:modified>2026-01-01</dc:modified>
                <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/ukla/2017/1/2024-01-01" title="2024-01-01"/>
                <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/ukla/2017/1" title="current"/>
                <PrimaryMetadata>
                    <DocumentClassification>
                        <DocumentCategory Value="primary" />
                        <DocumentMainType Value="UnitedKingdomLocalAct" />
                        <DocumentStatus Value="final" />
                    </DocumentClassification>
                    <Year Value="2017" />
                    <Number Value="1" />
                    <EnactmentDate Date="2017-01-01" />
                </PrimaryMetadata>
            </Metadata>
        </Legislation>
        """;

    private static final String FINAL_PRIMARY_WITHOUT_HAS_VERSION = """
        <Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation" DocumentURI="http://www.legislation.gov.uk/ukpga/2025/1/enacted">
            <Metadata xmlns="http://www.legislation.gov.uk/namespaces/metadata" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:atom="http://www.w3.org/2005/Atom">
                <dc:identifier>http://www.legislation.gov.uk/ukpga/2025/1/enacted</dc:identifier>
                <dc:title>Test Act Without HasVersion</dc:title>
                <dc:publisher>Statute Law Database</dc:publisher>
                <dc:modified>2026-01-01</dc:modified>
                <PrimaryMetadata>
                    <DocumentClassification>
                        <DocumentCategory Value="primary" />
                        <DocumentMainType Value="UnitedKingdomPublicGeneralAct" />
                        <DocumentStatus Value="final" />
                    </DocumentClassification>
                    <Year Value="2025" />
                    <Number Value="1" />
                    <EnactmentDate Date="2025-01-01" />
                </PrimaryMetadata>
            </Metadata>
        </Legislation>
        """;

    private static final String FINAL_MINISTERIAL_ORDER_CURRENT_ONLY = """
        <Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation" DocumentURI="http://www.legislation.gov.uk/ukmo/2024/5/made">
            <Metadata xmlns="http://www.legislation.gov.uk/namespaces/metadata" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:atom="http://www.w3.org/2005/Atom">
                <dc:identifier>http://www.legislation.gov.uk/ukmo/2024/5/made</dc:identifier>
                <dc:title>Test Ministerial Order</dc:title>
                <dc:publisher>Statute Law Database</dc:publisher>
                <dc:modified>2026-01-01</dc:modified>
                <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/ukmo/2024/5" title="current"/>
                <SecondaryMetadata>
                    <DocumentClassification>
                        <DocumentCategory Value="secondary" />
                        <DocumentMainType Value="UnitedKingdomMinisterialOrder" />
                        <DocumentStatus Value="final" />
                    </DocumentClassification>
                    <Year Value="2024" />
                    <Number Value="5" />
                    <Made Date="2024-01-01" />
                </SecondaryMetadata>
            </Metadata>
        </Legislation>
        """;

    private static final String FINAL_EU_DIRECTIVE_CURRENT_ONLY = """
        <Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation" DocumentURI="http://www.legislation.gov.uk/eudr/2019/7/adopted">
            <Metadata xmlns="http://www.legislation.gov.uk/namespaces/metadata" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:atom="http://www.w3.org/2005/Atom">
                <dc:identifier>http://www.legislation.gov.uk/eudr/2019/7/adopted</dc:identifier>
                <dc:title>Test EU Directive</dc:title>
                <dc:publisher>King's Printer of Acts of Parliament</dc:publisher>
                <dc:modified>2026-01-01</dc:modified>
                <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/eudr/2019/7" title="current"/>
                <EUMetadata>
                    <DocumentClassification>
                        <DocumentCategory Value="euretained" />
                        <DocumentMainType Value="EuropeanUnionDirective" />
                        <DocumentStatus Value="final" />
                    </DocumentClassification>
                    <Year Value="2019" />
                    <Number Value="7" />
                    <EnactmentDate Date="2019-01-01" />
                </EUMetadata>
            </Metadata>
        </Legislation>
        """;

    private static final String UNVERSIONED_PROSPECTIVE_REVISED_WITHOUT_CURRENT = """
        <Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation" DocumentURI="http://www.legislation.gov.uk/ukpga/2026/8" Status="Prospective">
            <Metadata xmlns="http://www.legislation.gov.uk/namespaces/metadata" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:atom="http://www.w3.org/2005/Atom">
                <dc:identifier>http://www.legislation.gov.uk/ukpga/2026/8</dc:identifier>
                <dc:title>Test Prospective Act</dc:title>
                <dc:publisher>Statute Law Database</dc:publisher>
                <dc:modified>2026-02-12</dc:modified>
                <dct:valid>2026-03-05</dct:valid>
                <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/ukpga/2026/8/enacted" title="enacted"/>
                <PrimaryMetadata>
                    <DocumentClassification>
                        <DocumentCategory Value="primary" />
                        <DocumentMainType Value="UnitedKingdomPublicGeneralAct" />
                        <DocumentStatus Value="revised" />
                    </DocumentClassification>
                    <Year Value="2026" />
                    <Number Value="8" />
                    <EnactmentDate Date="2026-02-12" />
                </PrimaryMetadata>
            </Metadata>
        </Legislation>
        """;

    private static final String VERSIONED_NON_PROSPECTIVE_FRAGMENT_CURRENT_AND_OLDER_DATE = """
        <Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation" DocumentURI="http://www.legislation.gov.uk/ukpga/2024/1/2024-11-01">
            <Metadata xmlns="http://www.legislation.gov.uk/namespaces/metadata" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:atom="http://www.w3.org/2005/Atom">
                <dc:identifier>http://www.legislation.gov.uk/ukpga/2024/1/section/2/2024-11-01</dc:identifier>
                <dc:title>Test Revised Section</dc:title>
                <dc:publisher>Statute Law Database</dc:publisher>
                <dc:modified>2024-11-02</dc:modified>
                <dct:valid>2024-11-01</dct:valid>
                <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/ukpga/2024/1/section/2/2024-10-01" title="2024-10-01"/>
                <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/ukpga/2024/1/section/2" title="current"/>
                <PrimaryMetadata>
                    <DocumentClassification>
                        <DocumentCategory Value="primary" />
                        <DocumentMainType Value="UnitedKingdomPublicGeneralAct" />
                        <DocumentStatus Value="revised" />
                    </DocumentClassification>
                    <Year Value="2024" />
                    <Number Value="1" />
                    <EnactmentDate Date="2024-01-01" />
                </PrimaryMetadata>
            </Metadata>
            <Primary>
                <Body DocumentURI="http://www.legislation.gov.uk/ukpga/2024/1/body/2024-11-01" IdURI="http://www.legislation.gov.uk/id/ukpga/2024/1/body" RestrictStartDate="2024-11-01">
                    <P1group RestrictStartDate="2024-10-01">
                        <P1 DocumentURI="http://www.legislation.gov.uk/ukpga/2024/1/section/2/2024-11-01" IdURI="http://www.legislation.gov.uk/id/ukpga/2024/1/section/2" id="section-2">
                            <Pnumber>2</Pnumber>
                            <P1para>
                                <Text>Test text.</Text>
                            </P1para>
                        </P1>
                    </P1group>
                </Body>
            </Primary>
        </Legislation>
        """;
}
