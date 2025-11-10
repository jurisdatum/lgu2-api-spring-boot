package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.meta.AssociatedDocument;
import uk.gov.legislation.transform.simple.Alternative;
import uk.gov.legislation.transform.simple.ImpactAssessment;
import uk.gov.legislation.transform.simple.Metadata;

import java.util.List;
import java.util.stream.Stream;

/**
 * Converts simplified metadata objects into clean API response objects for associated documents.
 * Handles the mapping from various document types (notes, correction slips, impact assessments, etc.)
 * to a unified AssociatedDocument representation.
 */
public class AssociatedDocumentConverter {

    static List<AssociatedDocument> convertAssociated(Metadata simple) {
        List<AssociatedDocument> notes = simple.notes == null ? List.of() : convert(simple.notes.alternatives, AssociatedDocument.Type.Note);
        return Stream.of(
            notes,
            convert(simple.policyEqualityStatements, AssociatedDocument.Type.PolicyEqualityStatement),
            convert(simple.correctionSlips, AssociatedDocument.Type.CorrectionSlip),
            convert(simple.codesOfPractice, AssociatedDocument.Type.CodeOfPractice),
            convert(simple.codesOfConduct, AssociatedDocument.Type.CodeOfConduct),
            convert(simple.tablesOfOrigins, AssociatedDocument.Type.TableOfOrigins),
            convert(simple.tablesOfDestinations, AssociatedDocument.Type.TableOfDestinations),
            convert(simple.ordersInCouncil, AssociatedDocument.Type.OrderInCouncil),
            convertIA(simple.impactAssessments, AssociatedDocument.Type.ImpactAssessment),
            convert(simple.otherDocuments, AssociatedDocument.Type.Other),
            convert(simple.explanatoryDocuments, AssociatedDocument.Type.ExplanatoryDocument),
            convert(simple.transpositionNotes, AssociatedDocument.Type.TranspositionNote),
            convertIA(simple.ukrpcOpinions, AssociatedDocument.Type.UKRPCOpinion)
        ).flatMap(List::stream).toList();
    }

    static List<AssociatedDocument> convert(List<Alternative> alts, AssociatedDocument.Type type) {
        if (alts == null)
            return List.of();
        return alts.stream().map(alt -> convert1(alt, type)).toList();
    }
    private static AssociatedDocument convert1(Alternative alt, AssociatedDocument.Type type) {
        AssociatedDocument doc = new AssociatedDocument(type, alt.uri);
        doc.name = alt.title;
        doc.date = alt.date;
        doc.size = alt.size;
        return doc;
    }

    private static List<AssociatedDocument> convertIA(List<ImpactAssessment> alts, AssociatedDocument.Type type) {
        if (alts == null)
            return List.of();

        return alts.stream()
            .map(ia -> {
                AssociatedDocument doc = new AssociatedDocument(type, ia.uri);
                doc.date = ia.date;
                doc.size = ia.size;
                doc.stage = ia.stage;
                doc.name = ia.title;
                doc.label = buildLabelFromStageAndType(doc.stage, type);
                return doc;
            })
            .toList();
    }

    private static String buildLabelFromStageAndType(String stage, AssociatedDocument.Type type) {
        if (stage == null || stage.isBlank()) {
            return type.toString();
        }
        String normalizedStage = stage.trim().replace("-", " ");
        var normalizedType = type.toString()
            .replace("_", " ")
            .replaceAll("(?<=[a-z])(?=[A-Z])", " ");
        return String.format("%s %s", normalizedStage, normalizedType).trim();
    }
}
