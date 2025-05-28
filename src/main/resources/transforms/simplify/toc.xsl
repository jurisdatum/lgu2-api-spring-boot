<xsl:transform version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:ukm="http://www.legislation.gov.uk/namespaces/metadata"
               xpath-default-namespace="http://www.legislation.gov.uk/namespaces/legislation"
               exclude-result-prefixes="ukm">

<xsl:template match="Contents">
    <contents>
        <xsl:apply-templates select="ContentsTitle" />
        <body>
            <xsl:apply-templates select="ContentsGroup | ContentsPart | ContentsChapter | ContentsPblock | ContentsPsubBlock | ContentsEUPart | ContentsEUTitle | ContentsEUChapter | ContentsEUSection | ContentsDivision | ContentsItem" />
        </body>
        <xsl:if test="exists(ContentsAppendix)">
            <appendices>
                <xsl:apply-templates select="ContentsAppendix" />
            </appendices>
        </xsl:if>
        <xsl:apply-templates select="ContentsAttachments[exists(following-sibling::ContentsSchedules)]" />
        <xsl:apply-templates select="ContentsSchedules" />
        <xsl:apply-templates select="ContentsAttachments[empty(following-sibling::ContentsSchedules)]" />
    </contents>
</xsl:template>

<xsl:template match="ContentsGroup | ContentsPart | ContentsChapter | ContentsPblock | ContentsPsubBlock | ContentsEUPart | ContentsEUTitle | ContentsEUChapter | ContentsEUSection | ContentsDivision | ContentsItem">
    <item>
        <xsl:attribute name="name">
            <xsl:variable name="name" select="local-name()" />
            <xsl:variable name="name" select="substring-after($name, 'Contents')" />
            <xsl:variable name="name" select="if (starts-with($name, 'EU')) then substring-after($name, 'EU') else $name" />
            <xsl:variable name="name" select="lower-case($name)" />
            <xsl:sequence select="$name" />
        </xsl:attribute>
        <xsl:call-template name="ref" />
        <xsl:call-template name="toc-extent" />
        <xsl:apply-templates />
    </item>
</xsl:template>

<xsl:template match="ContentsAppendix">
    <appendix name="appendix">
        <xsl:call-template name="ref" />
        <xsl:call-template name="toc-extent" />
        <xsl:apply-templates />
    </appendix>
</xsl:template>

<xsl:template match="ContentsSchedules">
    <schedules>
        <xsl:apply-templates />
    </schedules>
</xsl:template>
<xsl:template match="ContentsSchedules/ContentsTitle" />

<xsl:template match="ContentsSchedule">
    <schedule name="schedule">
        <xsl:call-template name="ref" />
        <xsl:call-template name="toc-extent" />
        <xsl:apply-templates />
    </schedule>
</xsl:template>

<xsl:template match="ContentsAttachments">
    <xsl:element name="{ if (exists(following-sibling::ContentsSchedules)) then 'attachments1' else 'attachments' }">
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="ContentsAttachments">
    <xsl:element name="{ if (exists(following-sibling::ContentsSchedules)) then 'attachments1' else 'attachments' }">
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="ContentsAttachment">
    <xsl:element name="{ if (exists(../following-sibling::ContentsSchedules)) then 'attachment1' else 'attachment' }">
        <xsl:attribute name="name">attachment</xsl:attribute>
        <xsl:call-template name="ref" />
        <xsl:call-template name="toc-extent" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template name="ref">
    <xsl:attribute name="ref">
        <xsl:sequence select="@ContentRef" />
    </xsl:attribute>
</xsl:template>

<xsl:template name="toc-extent">
    <xsl:if test="exists(@RestrictExtent)">
        <xsl:attribute name="extent">
            <xsl:sequence select="@RestrictExtent" />
        </xsl:attribute>
    </xsl:if>
</xsl:template>

<xsl:template match="ContentsNumber">
    <number>
        <xsl:apply-templates mode="contents-heading" />
    </number>
</xsl:template>

<xsl:template match="ContentsTitle">
    <title>
        <xsl:apply-templates mode="contents-heading" />
    </title>
</xsl:template>

<xsl:template match="Emphasis" mode="contents-heading">
    <xsl:text disable-output-escaping="yes">&amp;lt;i&amp;gt;</xsl:text>
    <xsl:apply-templates />
    <xsl:text disable-output-escaping="yes">&amp;lt;/i&amp;gt;</xsl:text>
</xsl:template>

</xsl:transform>
