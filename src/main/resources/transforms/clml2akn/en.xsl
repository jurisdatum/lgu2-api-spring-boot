<?xml version="1.0" encoding="utf-8"?>

<xsl:transform version="2.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:xs="http://www.w3.org/2001/XMLSchema"
               xpath-default-namespace="http://www.legislation.gov.uk/namespaces/legislation"
               xmlns="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
               exclude-result-prefixes="xs">

<xsl:template match="EN">
    <doc name="ExplanatoryNote">
        <xsl:apply-templates />
    </doc>
</xsl:template>

<xsl:template match="EN/ExplanatoryNotes">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="ExplanatoryNotes/Body">
    <mainBody>
        <xsl:apply-templates />
    </mainBody>
</xsl:template>

<xsl:template match="ENprelims">
    <preface>
        <xsl:apply-templates />
    </preface>
</xsl:template>

<xsl:template match="NumberedPara">
    <paragraph>
        <xsl:apply-templates />
    </paragraph>
</xsl:template>

</xsl:transform>
