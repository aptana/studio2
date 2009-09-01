<xsl:stylesheet
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     version="1.0">

<xsl:output method="text"/>

<!--
-
-   main
-
-->
<xsl:template match="/">
    <xsl:apply-templates select="//*">
        <xsl:sort select="name()"/>
    </xsl:apply-templates>
</xsl:template>

<!--
-
-   element
-
-->
<xsl:template match="*">
	<xsl:text>&lt;</xsl:text>
    <xsl:value-of select="name()"/>
	<xsl:apply-templates select="@*">
        <xsl:sort select="name()"/>
    </xsl:apply-templates>
    <xsl:text>/>&#x0A;</xsl:text>
</xsl:template>

<!--
-
-   attribute
-
-->
<xsl:template match="@*">
	<xsl:text> </xsl:text>
    <xsl:value-of select="name()"/>
	<xsl:text>=""</xsl:text>
</xsl:template>

</xsl:stylesheet>
