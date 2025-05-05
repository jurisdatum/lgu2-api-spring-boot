<xsl:transform version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:mode on-no-match="shallow-copy" />

<xsl:strip-space elements="atom:feed atom:entry" xmlns:atom="http://www.w3.org/2005/Atom" />

<xsl:output indent="yes" />

<xsl:import href="effects.xsl" />

<xsl:template match="comment()" />

</xsl:transform>
