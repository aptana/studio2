<xsl:stylesheet
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	 xmlns:oaa="http://openajax.org/metadata"
	 exclude-result-prefixes="oaa"
     version="1.0">

<xsl:output method="xml" indent="yes"/>

<!--
	NOTE: this stylesheet is far from complete and
	is not ready for prime time, but I don't want
	to lose this work :)
-->

<!-- main -->
<xsl:template match="/">
    <xsl:apply-templates select="*"/>
</xsl:template>

<!-- browser -->
<xsl:template match="browser">
	<oaa:useragent>
		<xsl:if test="@os">
			<xsl:copy-of select="@os"/>
		</xsl:if>
		<xsl:if test="@osVersion">
			<xsl:copy-of select="@osVersion"/>
		</xsl:if>
		<xsl:if test="@platform">
			<xsl:copy-of select="@platform"/>
		</xsl:if>
		<xsl:if test="@version">
			<xsl:copy-of select="@version"/>
		</xsl:if>
	</oaa:useragent>
</xsl:template>

<!-- class -->
<xsl:template match="class">
	<oaa:class name="{@type}">
		<!-- attributes -->
		<xsl:if test="@superclass">
			<xsl:copy-of select="@superclass"/>
		</xsl:if>
		
		<!-- child elements -->
		<xsl:apply-templates select="description"/>
		<xsl:apply-templates select="deprecated"/>
		<xsl:apply-templates select="browsers/browser"/>
		<xsl:apply-templates select="constructors/constructor"/>
		<xsl:apply-templates select="properties/property"/>
		<xsl:apply-templates select="methods/method"/>
		<xsl:apply-templates select="mixins"/>
		<xsl:apply-templates select="examples/example|example"/>
		<xsl:apply-templates select="remarks"/>
	</oaa:class>
</xsl:template>

<!-- constructor -->
<xsl:template match="constructor">
	<oaa:constructor scope="{@scope}">
		<xsl:call-template name="method-body"/>
	</oaa:constructor>
</xsl:template>

<!-- deprecated -->
<xsl:template match="deprecated">
	<oaa:deprecated>
		<xsl:copy-of select="text()"/>
	</oaa:deprecated>
</xsl:template>

<!-- description -->
<xsl:template match="description">
	<oaa:description>
		<xsl:copy-of select="text()"/>
	</oaa:description>
</xsl:template>

<!-- example -->
<xsl:template match="example">
	<oaa:example>
		<xsl:copy-of select="text()"/>
	</oaa:example>
</xsl:template>

<!-- exception -->
<xsl:template match="exception">
	<oaa:exception datatype="{@type}">
		<xsl:apply-templates select="description"/>
	</oaa:exception>
</xsl:template>

<!-- javascript -->
<xsl:template match="javascript">
	<api xmlns="http://openajax.org/metadata">
		<xsl:apply-templates select="class"/>
	</api>
</xsl:template>

<!-- method -->
<xsl:template match="method">
	<oaa:method name="{@name}" scope="{@scope}">
		<xsl:call-template name="method-body"/>
	</oaa:method>
</xsl:template>

<!-- method-body -->
<xsl:template name="method-body">	
	<!-- attributes -->
	<xsl:if test="@visibility">
		<xsl:copy-of select="@visibility"/>
	</xsl:if>
	
	<!-- child elements -->
	<xsl:apply-templates select="description"/>
	<xsl:apply-templates select="deprecated"/>
	<xsl:apply-templates select="browsers/browser"/>
	<xsl:apply-templates select="exceptions/exception"/>
	<xsl:apply-templates select="parameters/parameter"/>
	<xsl:apply-templates select="return-types/return-type"/>
	<xsl:apply-templates select="references/reference"/>
	<xsl:apply-templates select="examples/example|example"/>
	<xsl:apply-templates select="remarks"/>
</xsl:template>

<!-- mixins -->
<xsl:template match="mixins">
	<xsl:apply-templates select="mixin">
		<xsl:with-param name="fromScope" select="@scope"/>
	</xsl:apply-templates>
</xsl:template>

<!-- mixin -->
<xsl:template match="mixin">
	<xsl:param name="fromScope"/>
	<oaa:mix fromScope="{$fromScope}" toScope="{@scope}" datatype="{@type}"/>
</xsl:template>

<!-- parameter -->
<xsl:template match="parameter">
	<oaa:parameter name="{@name}" datatype="{@type}" usage="{@usage}">
		<xsl:apply-templates select="description"/>
	</oaa:parameter>
</xsl:template>

<!-- property -->
<xsl:template match="property">
	<oaa:property name="{@name}" datatype="{@type}" scope="{@scope}">
		<xsl:if test="@access and @access/text() = 'read-only'">
			<xsl:attribute name="readonly">true</xsl:attribute>
		</xsl:if>
		<xsl:apply-templates select="description"/>
		<xsl:apply-templates select="deprecated"/>
		<xsl:apply-templates select="browsers/browser"/>
		<xsl:apply-templates select="examples/example|example"/>
		<xsl:apply-templates select="references/reference"/>
		<xsl:apply-templates select="remarks"/>
	</oaa:property>
</xsl:template>

<!-- reference -->
<xsl:template match="reference">
	<oaa:reference>
		<xsl:value-of select="@name"/>
	</oaa:reference>
</xsl:template>

<!-- remarks -->
<xsl:template match="remarks">
	<oaa:remarks>
		<xsl:copy-of select="text()"/>
	</oaa:remarks>
</xsl:template>

<!-- return-type -->
<xsl:template match="return-type">
	<oaa:returns datatype="{@type}">
		<xsl:choose>
			<xsl:when test="count(description) > 0">
				<xsl:apply-templates select="description"/>
			</xsl:when>
			<xsl:when test="count(../../return-description) > 0">
				<xsl:if test="position() = 1">
					<oaa:description>
						<xsl:value-of select="../../return-description/text()"/>
					</oaa:description>
				</xsl:if>
			</xsl:when>
		</xsl:choose>
	</oaa:returns>
</xsl:template>

</xsl:stylesheet>
