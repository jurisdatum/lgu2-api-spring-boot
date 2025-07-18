<?xml version="1.0" encoding="utf-8"?>

<!-- I don't believe this file is used any longer -->

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
<xsl:preserve-space elements="ukm:AffectedProvisions ukm:AffectingProvisions ukm:SectionRange Text Emphasis Strong Underline SmallCaps Superior Inferior Uppercase Underline Expanded Strike Definition Proviso Abbreviation Acronym Term Span Citation CitationSubRef InternalLink ExternalLink InlineAmendment Addition Substitution Repeal" />

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

<!-- metadata -->

<xsl:template match="ukm:Metadata">
    <meta>
        <xsl:call-template name="id" />
        <xsl:apply-templates select="ukm:*/ukm:DocumentClassification/ukm:DocumentMainType" />
        <xsl:apply-templates select="ukm:*/ukm:Year" />
        <xsl:call-template name="regnal-year" />
        <xsl:apply-templates select="ukm:*/ukm:Number" />
        <xsl:apply-templates select="ukm:*/ukm:AlternativeNumber" />
        <xsl:apply-templates select="ukm:*/ukm:EnactmentDate" />
        <xsl:apply-templates select="ukm:*/ukm:Made" />
        <xsl:apply-templates select="ukm:*/ukm:DocumentClassification/ukm:DocumentStatus" />
        <xsl:apply-templates select="dct:valid" />
        <xsl:apply-templates select="dc:title" />
        <xsl:call-template name="extent" />
        <xsl:apply-templates select="dc:language" />
        <xsl:apply-templates select="dc:publisher" />
        <xsl:apply-templates select="dc:modified" />
        <xsl:call-template name="versions" />
        <xsl:call-template name="schedules" />
        <xsl:call-template name="formats" />
        <xsl:if test="$is-fragment">
            <xsl:call-template name="fragment-info" />
        </xsl:if>
        <xsl:apply-templates select="ukm:*/ukm:UnappliedEffects" mode="copy" />
    </meta>
</xsl:template>

<xsl:variable name="dc-identifier" as="xs:string" select="/Legislation/ukm:Metadata/dc:identifier[1]" />

<xsl:key name="document-uri" match="*[not(self::InternalLink)]" use="@DocumentURI"/>

<xsl:variable name="target" as="element()?" select="key('document-uri', $dc-identifier)" />

<xsl:variable name="id-components" as="xs:string+">
    <xsl:variable name="id" as="xs:string" select="substring-after($dc-identifier, 'http://www.legislation.gov.uk/')" />
    <xsl:variable name="components" as="xs:string+" select="tokenize($id, '/')" />
    <xsl:choose>
        <xsl:when test="$components[2] castable as xs:integer">
            <xsl:sequence select="subsequence($components, 1, 3)" />
        </xsl:when>
        <xsl:otherwise>
            <xsl:sequence select="subsequence($components, 1, 4)" />
        </xsl:otherwise>
    </xsl:choose>
</xsl:variable>

<xsl:template name="id">
    <id>
        <xsl:value-of select="string-join($id-components, '/')" />
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

<xsl:template name="regnal-year">
    <xsl:if test="count($id-components) = 4">
        <xsl:variable name="components" select="subsequence($id-components, 2, 2)" />
        <regnalYear>
            <xsl:value-of select="string-join($components, '/')" />
        </regnalYear>
    </xsl:if>
</xsl:template>

<xsl:template match="ukm:Number">
    <number>
        <xsl:value-of select="@Value" />
    </number>
</xsl:template>

<xsl:template match="ukm:AlternativeNumber">
    <altNumber category="{ @Category }" value="{ @Value }" />
</xsl:template>

<xsl:template match="ukm:EnactmentDate | ukm:Made">
    <date>
        <xsl:value-of select="@Date" />
    </date>
</xsl:template>

<!-- extent -->

<xsl:template name="extent">
    <extent>
        <xsl:value-of select="$target/ancestor-or-self::*[exists(@RestrictExtent)][1]/@RestrictExtent" />
    </extent>
</xsl:template>

<!-- versions -->

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

<xsl:template name="schedules">
    <schedules>
        <xsl:value-of select="atom:link[@rel='http://www.legislation.gov.uk/def/navigation/schedules']/@href" />
    </schedules>
</xsl:template>

<xsl:template name="formats">
    <formats>
        <xsl:if test="exists(following-sibling::*)">
            <format name="xml" />
        </xsl:if>
        <xsl:variable name="alternative" as="element()?" select="ukm:Alternatives/ukm:Alternative[ends-with(@URI, '.pdf')][not(@Language=('Welsh','Mixed'))]" /> <!-- ToDo -->
        <xsl:if test="exists($alternative)">
            <format name="pdf" uri="{ $alternative/@URI }" />
        </xsl:if>
    </formats>
</xsl:template>

<!-- fragments -->

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
    <ancestors>
        <xsl:for-each select="$target/ancestor-or-self::*[not(self::Schedules)][exists(@id)]">
            <xsl:call-template name="ancestor-or-descendant">
                <xsl:with-param name="name" select="'ancestor'" />
            </xsl:call-template>
        </xsl:for-each>
    </ancestors>
    <descendants>
        <xsl:for-each select="$target/descendant-or-self::*[exists(@DocumentURI)]">
            <xsl:call-template name="ancestor-or-descendant">
                <xsl:with-param name="name" select="'descendant'" />
            </xsl:call-template>
        </xsl:for-each>
    </descendants>
</xsl:template>

<xsl:template name="ancestor-or-descendant">
    <xsl:param name="name" as="xs:string" />
    <xsl:element name="{ $name }">
        <xsl:attribute name="name" select="local-name(.)" />
        <xsl:copy-of select="@id" />
        <xsl:copy-of select="@DocumentURI" />
        <xsl:apply-templates select="Number | Pnumber" mode="simplify-number" />
        <xsl:apply-templates select="child::Title | self::P1/parent::P1group/Title" mode="simplify-title" />
    </xsl:element>
</xsl:template>

<xsl:template match="*" mode="simplify-number">
    <number>
        <xsl:value-of select="normalize-space(.)" />
    </number>
</xsl:template>

<xsl:template match="*" mode="simplify-title">
    <title>
        <xsl:value-of select="normalize-space(.)" />
    </title>
</xsl:template>

<!-- effects -->

<xsl:template match="ukm:UnappliedEffect" mode="copy">
    <xsl:copy>
        <xsl:apply-templates select="@*" mode="copy" />
        <xsl:apply-templates mode="copy" />
    </xsl:copy>
</xsl:template>

<xsl:template match="@*" mode="copy">
    <xsl:copy>
        <xsl:value-of select="." />
    </xsl:copy>
</xsl:template>

<xsl:template match="@AffectedProvisions" mode="copy">
    <xsl:attribute name="AffectedProvisionsText">
        <xsl:value-of select="." />
    </xsl:attribute>
</xsl:template>
<xsl:template match="@AffectingProvisions" mode="copy">
    <xsl:attribute name="AffectingProvisionsText">
        <xsl:value-of select="." />
    </xsl:attribute>
</xsl:template>

<xsl:template match="ukm:AffectedProvisions//text()" mode="copy" > <!-- can be child of SectionRange -->
    <node type="text" text="{.}" />
</xsl:template>
<xsl:template match="ukm:AffectingProvisions//text()" mode="copy" > <!-- can be child of SectionRange -->
    <node type="text" text="{.}" />
</xsl:template>

<xsl:template match="ukm:SectionRange" mode="copy">
    <xsl:apply-templates mode="copy" />
</xsl:template>

<xsl:template match="ukm:Section" mode="copy">
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

<xsl:template match="*" mode="copy">
    <xsl:copy>
        <xsl:copy-of select="@*" />
        <xsl:apply-templates mode="copy" />
    </xsl:copy>
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
