<xsl:transform version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:ukm="http://www.legislation.gov.uk/namespaces/metadata"
               exclude-result-prefixes="ukm">

<xsl:template match="@AffectedProvisions">
    <xsl:attribute name="AffectedProvisionsText">
        <xsl:value-of select="." />
    </xsl:attribute>
</xsl:template>

<xsl:template match="@AffectingProvisions">
    <xsl:attribute name="AffectingProvisionsText">
        <xsl:value-of select="." />
    </xsl:attribute>
</xsl:template>

<xsl:template match="ukm:AffectedProvisions//text()" > <!-- can be child of SectionRange -->
    <node type="text" text="{.}" />
</xsl:template>

<xsl:template match="ukm:AffectingProvisions//text()" > <!-- can be child of SectionRange -->
    <node type="text" text="{.}" />
</xsl:template>

<xsl:template match="ukm:SectionRange">
    <node type="range" start="{ @Start }" end="{ @End }" uri="{ @URI }" upTo="{ @UpTo }">
        <xsl:apply-templates />
    </node>
</xsl:template>

<xsl:template match="ukm:Section">
    <node type="section" ref="{ @Ref }" uri="{ @URI }" text="{ . }">
        <xsl:if test="exists(@Missing)">
            <xsl:attribute name="missing">
                <xsl:value-of select="@Missing" />
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="exists(@err:Ref)" xmlns:err="http://www.legislation.gov.uk/namespaces/error">
            <xsl:attribute name="error">
                <xsl:value-of select="@err:Ref" />
            </xsl:attribute>
        </xsl:if>
    </node>
</xsl:template>

</xsl:transform>
