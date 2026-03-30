<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	version="2.0">

	<xsl:import href="legislation_schema_FO.xslt"/>

	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes" standalone="no"
		use-character-maps="FOcharacters"/>
	
	<xsl:variable name="version" select="(substring-before(string(current-date()), '+')[normalize-space()],'2019-11-26')[1]"/>
	
	<xsl:variable name="isRepealed" select="true()"/>

</xsl:stylesheet>
