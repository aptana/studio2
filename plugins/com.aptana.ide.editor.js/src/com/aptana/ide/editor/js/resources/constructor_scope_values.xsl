<xsl:stylesheet
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     version="1.0">

<xsl:output method="text"/>

<!-- main -->
<xsl:template match="/">
    <xsl:apply-templates select="//constructor/@scope"/>
</xsl:template>

<!-- attribute -->
<xsl:template match="@*">
    <xsl:value-of select="."/>
	<xsl:text>&#x0A;</xsl:text>
</xsl:template>

</xsl:stylesheet>
