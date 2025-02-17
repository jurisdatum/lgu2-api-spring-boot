<xsl:transform version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:xs="http://www.w3.org/2001/XMLSchema"
               xmlns:ukm="http://www.legislation.gov.uk/namespaces/metadata"
               xpath-default-namespace="http://www.legislation.gov.uk/namespaces/legislation"
               exclude-result-prefixes="xs ukm">

<xsl:mode on-no-match="shallow-copy" />

<xsl:strip-space elements="*" />
<xsl:preserve-space elements="ukm:AffectedProvisions ukm:AffectingProvisions ukm:SectionRange Text Emphasis Strong Underline SmallCaps Superior Inferior Uppercase Underline Expanded Strike Definition Proviso Abbreviation Acronym Term Span Citation CitationSubRef InternalLink ExternalLink InlineAmendment Addition Substitution Repeal" />

<xsl:output indent="no" />

<xsl:import href="metadata.xsl" />
<xsl:import href="toc.xsl" />
<xsl:import href="effects.xsl" />

<xsl:param name="is-fragment" as="xs:boolean" select="false()" />
<xsl:param name="include-contents" as="xs:boolean" select="true()" />

<xsl:template match="Legislation">
    <leg>
        <xsl:apply-templates select="ukm:Metadata" />
        <xsl:if test="$include-contents">
            <xsl:apply-templates select="Contents" />
        </xsl:if>
    </leg>
</xsl:template>

<xsl:template match="comment()" />

</xsl:transform>
