<?xml version="1.0" encoding="utf-8"?>

<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:ukm="http://www.legislation.gov.uk/namespaces/metadata"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dct="http://purl.org/dc/terms/"
   xmlns:atom="http://www.w3.org/2005/Atom"
   xpath-default-namespace="http://www.legislation.gov.uk/namespaces/legislation"
   exclude-result-prefixes="xs ukm dc dct atom">

<xsl:output method="xml" version="1.0" encoding="utf-8" omit-xml-declaration="yes" indent="no" />

<xsl:strip-space elements="*" />
<xsl:preserve-space elements="Text Emphasis Strong Underline SmallCaps Superior Inferior Uppercase Underline Expanded Strike Definition Proviso Abbreviation Acronym Term Span Citation CitationSubRef InternalLink ExternalLink InlineAmendment Addition Substitution Repeal" />

<xsl:template match="Legislation">
    <leg>
        <xsl:apply-templates select="ukm:Metadata | Contents" />
    </leg>
</xsl:template>

<!-- metadata -->

<xsl:template match="ukm:Metadata">
    <meta>
        <xsl:apply-templates select="dc:identifier" />
        <xsl:apply-templates select="ukm:*/ukm:DocumentClassification/ukm:DocumentMainType" />
        <xsl:apply-templates select="ukm:*/ukm:Year" />
        <xsl:apply-templates select="ukm:*/ukm:Number" />
        <xsl:apply-templates select="ukm:*/ukm:AlternativeNumber" />
        <xsl:apply-templates select="ukm:*/ukm:EnactmentDate" />
        <xsl:apply-templates select="ukm:*/ukm:DocumentClassification/ukm:DocumentStatus" />
        <xsl:apply-templates select="dct:valid" />
        <xsl:apply-templates select="dc:title" />
        <xsl:apply-templates select="dc:language" />
        <xsl:apply-templates select="dc:publisher" />
        <xsl:apply-templates select="dc:modified" />
        <xsl:call-template name="versions" />
        <xsl:call-template name="fragment-info" />
    </meta>
</xsl:template>

<xsl:template match="dc:identifier">
    <id>
        <xsl:variable name="id" select="string(.)" />
        <xsl:variable name="id" select="substring-after($id, 'http://www.legislation.gov.uk/')" />
        <xsl:variable name="parts" select="tokenize($id, '/')" />
        <xsl:variable name="parts" select="subsequence($parts, 1, 3)" />
        <xsl:variable name="id" select="string-join($parts, '/')" />
        <xsl:value-of select="$id" />
    </id>
</xsl:template>

<xsl:template match="dc:title">
    <title>
        <xsl:apply-templates />
    </title>
</xsl:template>

<xsl:template match="dc:language">
    <lang>
        <xsl:apply-templates />
    </lang>
</xsl:template>

<xsl:template match="dc:publisher">
    <publisher>
        <xsl:apply-templates />
    </publisher>
</xsl:template>

<xsl:template match="dc:modified">
    <modified>
        <xsl:apply-templates />
    </modified>
</xsl:template>

<xsl:template match="dct:valid">
    <valid>
        <xsl:apply-templates />
    </valid>
</xsl:template>

<xsl:template match="ukm:DocumentMainType">
    <longType>
        <xsl:value-of select="@Value" />
    </longType>
</xsl:template>

<xsl:template match="ukm:DocumentStatus">
    <status>
        <xsl:value-of select="@Value" />
    </status>
</xsl:template>

<xsl:template match="ukm:Year">
    <year>
        <xsl:value-of select="@Value" />
    </year>
</xsl:template>

<xsl:template match="ukm:Number">
    <number>
        <xsl:value-of select="@Value" />
    </number>
</xsl:template>

<xsl:template match="ukm:AlternativeNumber">
    <altNumber category="{ @Category }" value="{ @Value }" />
</xsl:template>

<xsl:template match="ukm:EnactmentDate">
    <date>
        <xsl:value-of select="@Date" />
    </date>
</xsl:template>

<xsl:template name="versions">
    <hasVersions>
        <xsl:apply-templates select="atom:link[@rel='http://purl.org/dc/terms/hasVersion']" />
    </hasVersions>
</xsl:template>

<xsl:template match="atom:link[@rel='http://purl.org/dc/terms/hasVersion']">
    <hasVersion>
        <xsl:value-of select="@title" />
    </hasVersion>
</xsl:template>

<xsl:template name="fragment-info">
    <fragment>
        <xsl:value-of select="dc:identifier" />
    </fragment>
    <prev>
        <xsl:value-of select="atom:link[@rel='prev']/@href" />
    </prev>
    <next>
        <xsl:value-of select="atom:link[@rel='next']/@href" />
    </next>
    <schedules>
        <xsl:value-of select="atom:link[@rel='http://www.legislation.gov.uk/def/navigation/schedules']/@href" />
    </schedules>
</xsl:template>

<!-- ToC -->

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
        <xsl:apply-templates />
    </item>
</xsl:template>

<xsl:template match="ContentsAppendix">
    <appendix name="appendix">
        <xsl:call-template name="ref" />
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
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template name="ref">
    <xsl:attribute name="ref">
        <xsl:sequence select="@ContentRef" />
    </xsl:attribute>
</xsl:template>

<xsl:template match="ContentsNumber">
    <number>
        <xsl:apply-templates />
    </number>
</xsl:template>

<xsl:template match="ContentsTitle">
    <title>
        <xsl:apply-templates />
    </title>
</xsl:template>

<xsl:template match="Emphasis">
    <xsl:text disable-output-escaping="yes">&amp;lt;i&amp;gt;</xsl:text>
    <xsl:apply-templates />
    <xsl:text disable-output-escaping="yes">&amp;lt;/i&amp;gt;</xsl:text>
</xsl:template>

</xsl:transform>
