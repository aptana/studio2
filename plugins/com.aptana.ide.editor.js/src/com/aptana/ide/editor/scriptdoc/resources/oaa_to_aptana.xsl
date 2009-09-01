<xsl:stylesheet
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	 xmlns:oaa="http://openajax.org/metadata"
     version="1.0"
	 exclude-result-prefixes="oaa">

<xsl:output method="xml" indent="yes"/>

<!-- main -->
<xsl:template match="/">
    <xsl:apply-templates select="*"/>
</xsl:template>

<!-- api -->
<xsl:template match="oaa:api">
	<javascript>
		<xsl:if test="count(oaa:classes/oaa:class|oaa:class) > 0">
			<xsl:apply-templates select="oaa:classes/oaa:class|oaa:class"/>
		</xsl:if>
		<!--
		<xsl:if test="count(.//oaa:interface) > 0">
			<interfaces>
				<xsl:apply-templates select=".//oaa:interface"/>
			</interfaces>
		</xsl:if>
		<xsl:if test="count(.//oaa:mixin) > 0">
			<mixins>
				<xsl:apply-templates select=".//oaa:mixins"/>
			</mixins>
		</xsl:if>
		-->
	</javascript>
</xsl:template>

<!-- available -->
<xsl:template match="oaa:available">
	<availability>
		<specification name="{@type}">
			<xsl:if test="@version">
				<xsl:copy-of select="@version"/>
			</xsl:if>
		</specification>
	</availability>
</xsl:template>

<!-- class -->
<xsl:template match="oaa:class">
	<class type="{@name}">
		<!-- attributes -->
		<xsl:if test="@superclass">
			<xsl:copy-of select="@superclass"/>
		</xsl:if>
		<!--
		<xsl:if test="@visibility">
			<xsl:copy-of select="@visibility"/>
		</xsl:if>
		-->
		
		<!-- child elements -->
		<!--
		<xsl:if test="count(.//oaa:alias) > 0">
			<aliases>
				<xsl:apply-templates select=".//oaa:alias"/>
			</aliases>
		</xsl:if>
		<xsl:apply-templates select="oaa:available"/>
		-->
		<xsl:apply-templates select="oaa:deprecated"/>
		<xsl:apply-templates select="oaa:description"/>
		<xsl:if test="count(oaa:useragents/oaa:useragent|oaa:useragent) > 0">
			<browsers>
				<xsl:apply-templates select="oaa:useragents/oaa:useragent|oaa:useragent"/>
			</browsers>
		</xsl:if>
		<xsl:if test="count(oaa:constructors/oaa:constructor|oaa:constructor) > 0">
			<constructors>
				<xsl:apply-templates select="oaa:constructors/oaa:constructor|oaa:constructor"/>
			</constructors>
		</xsl:if>
		<xsl:if test="count(oaa:properties/oaa:property|oaa:property) > 0">
			<properties>
				<xsl:apply-templates select="oaa:properties/oaa:property|oaa:property"/>
			</properties>
		</xsl:if>
		<xsl:if test="count(oaa:methods/oaa:method|oaa:method) > 0">
			<methods>
				<xsl:apply-templates select="oaa:methods/oaa:method|oaa:method"/>
			</methods>
		</xsl:if>
		<xsl:apply-templates select="oaa:mixes/oaa:mix|oaa:mix"/>
		<xsl:if test="count(oaa:examples/oaa:example|oaa:example) > 0">
			<examples>
				<xsl:apply-templates select="oaa:examples/oaa:example|oaa:example"/>
			</examples>
		</xsl:if>
		<!-- references -->
		<xsl:apply-templates select="oaa:remarks"/>
		<!-- mixins -->
	</class>
</xsl:template>

<!-- constructor -->
<xsl:template match="oaa:constructor">
	<constructor scope="{@scope}">
		<xsl:call-template name="method-body"/>
	</constructor>
</xsl:template>

<!-- deprecated -->
<xsl:template match="oaa:deprecated">
	<deprecated>
		<xsl:copy-of select="text()"/>
	</deprecated>
</xsl:template>

<!-- description -->
<xsl:template match="oaa:description">
	<description>
		<xsl:copy-of select="text()"/>
	</description>
</xsl:template>

<!-- example -->
<xsl:template match="oaa:example">
	<example>
    	<xsl:if test="oaa:description">
        	&lt;p&gt;
			<xsl:value-of select="oaa:description"/>
            &lt;/p&gt;
        </xsl:if>
        <xsl:if test="oaa:code">
        	&lt;pre&gt;
            <xsl:value-of select="oaa:code" />
            &lt;/pre&gt;
        </xsl:if>
        <xsl:if test="oaa:html">
        	&lt;pre&gt;
            <xsl:value-of select="oaa:html" />
            &lt;/pre&gt;
        </xsl:if>
	</example>
</xsl:template>

<!-- exception -->
<xsl:template match="oaa:exception">
	<exception type="{@datatype}">
		<xsl:apply-templates select="oaa:description"/>
	</exception>
</xsl:template>

<!-- method -->
<xsl:template match="oaa:method">
	<method name="{@name}" scope="{@scope}">
		<xsl:call-template name="method-body"/>
	</method>
</xsl:template>

<!-- method-body -->
<xsl:template name="method-body">
	<xsl:if test="@visibility">
		<xsl:copy-of select="@visibility"/>
	</xsl:if>
	<xsl:apply-templates select="oaa:deprecated"/>
	<xsl:apply-templates select="oaa:description"/>
	<xsl:if test="count(oaa:useragents/oaa:useragent|oaa:useragent) > 0">
		<browsers>
			<xsl:apply-templates select="oaa:useragents/oaa:useragent|oaa:useragent"/>
		</browsers>
	</xsl:if>
	<xsl:if test="count(oaa:parameters/oaa:parameter|oaa:parameter)">
		<parameters>
			<xsl:apply-templates select="oaa:parameters/oaa:parameter|oaa:parameter"/>
		</parameters>
	</xsl:if>
	<xsl:if test="count(oaa:exceptions/oaa:exception|oaa:exception) > 0">
		<exceptions>
			<xsl:apply-templates select="oaa:exceptions/oaa:exception|oaa:exception"/>
		</exceptions>
	</xsl:if>
	<xsl:if test="count(oaa:references/oaa:reference|oaa:reference) > 0">
		<references>
			<xsl:apply-templates select="oaa:references/oaa:reference|oaa:reference"/>
		</references>
	</xsl:if>
	<!-- references -->
	<xsl:if test="count(oaa:returnType) > 0">
		<return-types>
			<xsl:apply-templates select="oaa:returnType"/>
		</return-types>
	</xsl:if>
	<xsl:if test="count(oaa:examples/oaa:example|oaa:example) > 0">
		<examples>
			<xsl:apply-templates select="oaa:examples/oaa:example|oaa:example"/>
		</examples>
	</xsl:if>
	<xsl:apply-templates select="oaa:remarks"/>
</xsl:template>

<!-- mix -->
<xsl:template match="oaa:mix">
	<mixins scope="{@fromScope}">
		<mixin scope="{@toScope}" type="{@datatype}"/>
	</mixins>
</xsl:template>

<!-- parameter -->
<xsl:template match="oaa:parameter">
	<parameter name="{@name}" usage="{@usage}" type="{@datatype}">
		<xsl:apply-templates select="oaa:description"/>
	</parameter>
</xsl:template>

<!-- property -->
<xsl:template match="oaa:property">
	<property name="{@name}" type="{@datatype}" scope="{@scope}">
		<xsl:if test="@readonly and @readonly/text() = 'true'">
			<xsl:attribute name="access">
				<xsl:text>read-only</xsl:text>
			</xsl:attribute>
		</xsl:if>
		<!--
		<xsl:if test="@visibility">
			<xsl:copy-of select="@visibility"/>
		</xsl:if>
		-->
		<xsl:apply-templates select="oaa:deprecated"/>
		<xsl:apply-templates select="oaa:description"/>
		<xsl:if test="count(oaa:useragents/oaa:useragent|oaa:useragent) > 0">
			<browsers>
				<xsl:apply-templates select="oaa:useragents/oaa:useragent|oaa:useragent"/>
			</browsers>
		</xsl:if>
		<xsl:if test="count(oaa:examples/oaa:example|oaa:example) > 0">
			<examples>
				<xsl:apply-templates select="oaa:examples/oaa:example|oaa:example"/>
			</examples>
		</xsl:if>
		<xsl:if test="count(oaa:references/oaa:reference|oaa:reference) > 0">
			<references>
				<xsl:apply-templates select="oaa:references/oaa:reference|oaa:reference"/>
			</references>
		</xsl:if>
		<xsl:apply-templates select="oaa:remarks"/>
	</property>
</xsl:template>

<!-- reference -->
<xsl:template match="oaa:reference">
	<reference>
		<xsl:attribute name="name">
			<xsl:value-of select="text()"/>
		</xsl:attribute>
	</reference>	
</xsl:template>

<!-- remarks -->
<xsl:template match="oaa:remarks">
	<remarks>
		<xsl:copy-of select="text()"/>
	</remarks>
</xsl:template>

<!-- returns -->
<xsl:template match="oaa:returnType">
	<return-type type="{@datatype}">
		<xsl:apply-templates select="oaa:description"/>
	</return-type>
</xsl:template>

<!-- useragent -->
<xsl:template match="oaa:useragent">
	<browser platform="{@platform}">
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
	</browser>
</xsl:template>

</xsl:stylesheet>
