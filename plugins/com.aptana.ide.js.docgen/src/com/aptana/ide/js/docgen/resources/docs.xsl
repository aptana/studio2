<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="no"  encoding="utf-8" standalone="no"/>

  <xsl:param name="ReferenceName" select="/javascript/@fileName" />
  <xsl:param name="ReferenceDisplayName" select="/javascript/@fileDisplayName" />
  <xsl:param name="browsers" select="//browser/@platform[not(.=preceding::browser/@platform) and (. != 'None')]" />

  <xsl:template match="/javascript">
    <xsl:call-template name="index" />
  </xsl:template>

  <xsl:template name="index">
    <xsl:result-document href="index.html" method="html">
    <html>
      <head>
        <title><xsl:value-of select="$ReferenceDisplayName" /> ScriptDoc Reference</title>
		<link rel="stylesheet" href="images/shared.css" type="text/css" />
	  </head>
      <body>
      			<h1><xsl:value-of select="$ReferenceDisplayName" /> ScriptDoc Reference</h1>
      			<div class="content">
      			
		    	<xsl:if test="//property[@ignored='false']">
	      			<h2>Properties</h2>
				    <div class="list">
					    <xsl:for-each select="//property[@ignored='false']">
				   			<xsl:call-template name="property" />
					    </xsl:for-each>
				    </div>
			    </xsl:if>
		
		    	<xsl:if test="//function[@ignored='false']">
	      			<h2>Functions</h2>
			    	<div class="list">
					    <xsl:for-each select="//function[@ignored='false']">
				   			<xsl:call-template name="method" />
					    </xsl:for-each>
			    	</div>
			    </xsl:if>
			    
		    	</div>
      </body>
    </html>
    </xsl:result-document>
  </xsl:template>

  <xsl:template name="class">
    <h1>
      <xsl:value-of select="@type"/><xsl:if test="normalize-space(@superclass)"> : <a href="{@superclass}.html"><xsl:apply-templates select="@superclass" /></a></xsl:if>
    </h1>
    <div class="content">
		<div class="classDescription"><xsl:apply-templates select="description" /></div>
		<!--
		<div class="navigator">
		<xsl:if test="constructors/constructor"><a href="#{@type}.Constructors" >Constructors</a></xsl:if>
		<xsl:if test="properties/property"> | <a href="#{@type}.Properties" >Properties</a></xsl:if>
		<xsl:if test="properties/method"> | <a href="#{@type}.Functions" >Functions</a></xsl:if>
		<xsl:if test="example"> | <a href="#{@type}.Examples" >Examples</a></xsl:if>
		<xsl:if test="remarks and remarks/text()"> | <a href="#{@type}.Remarks" >Remarks</a></xsl:if>
		<xsl:if test="references/reference"> | <a href="#{@type}.References" >References</a></xsl:if>
		<xsl:if test="availability/specification"> | <a href="#{@type}.Availability" >Availability</a></xsl:if>
		</div>
		-->
         
	 	<xsl:if test="browsers/browser">
	 	    <a name="{@type}.Platform Support" ></a><h2>Platform Support</h2>
	 	    <p>
                <xsl:call-template name="browser-table" />
			</p>
	 	</xsl:if>        

		<xsl:if test="constructors/constructor">
		<a name="{@type}.Constructors" ></a>
		<h2>Constructors</h2>
		<p><xsl:call-template name="constructors-summary" /></p>
		</xsl:if>
		    
		<xsl:if test="properties/property">
		<a name="{@type}.Properties" ></a>
		<h2>Properties</h2>
		<p><xsl:call-template name="properties-summary" /></p>
		</xsl:if>

		<xsl:if test="methods/method">
		<a name="{@type}.Functions" ></a>
		<h2>Functions</h2>
		<p><xsl:call-template name="methods-summary" /></p>
		</xsl:if>

    	<xsl:if test="examples/example">
		<a name="{@type}.Examples" ></a><h2>Examples</h2>
 	  	<xsl:for-each select="examples/example">
		<p><xsl:apply-templates select="." /></p>
	  	</xsl:for-each>	   		
		</xsl:if>
		
		<xsl:if test="remarks and remarks/text()">
		<a name="{@type}.Remarks" ></a><h2>Remarks</h2>
		<p><xsl:apply-templates select="remarks" /></p>
		</xsl:if>
		
	 	<xsl:if test="references/reference">
		<a name="{@type}.References" ></a><h2>References</h2>
		<p><xsl:apply-templates select="references" /></p>
		</xsl:if>
	    
	 	<xsl:if test="availability/specification">
	 	<a name="{@type}.Availability" ></a>
	 	<h2>Availability</h2>
	 	<p><xsl:apply-templates select="availability" /></p>
	 	</xsl:if>
			
		<xsl:if test="constructors/constructor">
		<a name="{@type}.ConstructorDetail" ></a><h2>Constructor Detail</h2>
		<p><xsl:apply-templates select="constructors" /></p>
		</xsl:if>

		<xsl:if test="properties/property">
		<a name="{@type}.PropertyDetail" ></a><h2>Property Detail</h2>
		<p><xsl:apply-templates select="properties" /></p>
		</xsl:if>
	    
   	 	<xsl:if test="methods/method">
		<a name="{@type}.MethodDetail" ></a><h2>Method Detail</h2>
		<p><xsl:apply-templates select="methods" /></p>
		</xsl:if>
				
	 </div>  
  </xsl:template>

  <xsl:template match="constructors">  
    <div class="list">
    	<xsl:apply-templates select="constructor" />
  </div>
  </xsl:template>

  <xsl:template match="properties">
    <div class="list">
    	<xsl:apply-templates select="property" />
    </div>
  </xsl:template>

  <xsl:template match="methods">
    <div class="list">
    	<xsl:apply-templates select="method" />
    </div>
  </xsl:template>
  
  <xsl:template name="constructors-summary">
    	<table cellspacing="3" width="90%">
			<tr class="compheader">
				<th align="left">Constructor</th>
		    	<xsl:for-each select="$browsers">
		    		<th><xsl:value-of select="." /></th>
				</xsl:for-each>
			</tr>
	 	  	<xsl:for-each select="constructors/constructor">
		  			<xsl:call-template name="item-summary">
		  				<xsl:with-param name="name"><xsl:value-of select="../../@type" /> Constructor</xsl:with-param>
		  				<xsl:with-param name="browsers" select="../../browsers" />
		  			</xsl:call-template>
		  	</xsl:for-each>
		</table>
  </xsl:template>

  <xsl:template match="constructor">
    <a name="#{../../@type}.{@name}"></a>
    <h3><xsl:if test="@scope != 'instance'"><xsl:value-of select="@scope" />&#160;</xsl:if> <i><xsl:apply-templates select="return-types" /></i>&#160;<xsl:value-of select="../../@type" />(<xsl:call-template name="parameters-condensed" />)</h3>
	<xsl:if test="description/text()">
	<p class="padded"><xsl:apply-templates select="description" /> <xsl:if test="description != return-description">&#160;<xsl:apply-templates select="return-description" /></xsl:if></p>
	</xsl:if>
	<xsl:if test="parameters/parameter">
	<p class="padded">
	<xsl:apply-templates select="parameters" />
	</p>
	</xsl:if>

<dl class="details">
<xsl:if test="example">		
<dd><xsl:apply-templates select="example" /></dd>
</xsl:if>
<xsl:if test="remarks">		
<dt>Remarks</dt>
<dd><xsl:apply-templates select="remarks" /></dd>
</xsl:if>     		
<xsl:if test="exceptions/exception">		
<dt>Throws</dt>
<dd><xsl:apply-templates select="exceptions" /></dd>
</xsl:if>
<xsl:if test="references/reference">		
<dt>See Also</dt>
<dd><p><xsl:apply-templates select="references" /></p></dd>
</xsl:if>
<xsl:if test="availability/specification">		
<dt>Availability</dt>
<dd><p><xsl:apply-templates select="availability" /></p></dd>
</xsl:if>
<!--
<xsl:if test="browsers/browser">
<a name="{@type}.Platform Support" ></a>
<dt>Platform Support</dt>
<dd><p><xsl:call-template name="browser-table" /></p></dd>
</xsl:if>	
-->		
<xsl:if test="@visibility != 'basic'">	
<dt>Visibility</dt>
<dd><xsl:value-of select="@visibility" /></dd>
</xsl:if>  		
</dl>
</xsl:template>

  <xsl:template name="properties-summary">
    	<table cellspacing="3" width="90%">
			<tr class="compheader">
				<th align="left">Property</th>
		    	<xsl:for-each select="$browsers">
		    		<th><xsl:value-of select="." /></th>
				</xsl:for-each>
			</tr>
	 	  	<xsl:for-each select="properties/property">
		  			<xsl:call-template name="item-summary" />
		  	</xsl:for-each>
		</table>
  </xsl:template>
  
  <xsl:template name="property">
    <a name="#{../../@type}.{@name}"></a>
    <h3><xsl:if test="@scope != 'instance'"><xsl:value-of select="@scope" />&#160;</xsl:if> <i><xsl:value-of select="@type" /></i>&#160;<xsl:call-template name="ancestors" /><xsl:value-of select="@name" /> <xsl:if test="@access != 'read-write'">&#160;-&#160;<xsl:value-of select="@access" />&#160;only</xsl:if></h3> 
    <p class="padded"><xsl:apply-templates select="description" /></p>

    <dl class="details">
	   		<xsl:if test="examples">
    		<dt>Examples</dt>
	 	  	<xsl:for-each select="examples/example">
    			<dd><xsl:apply-templates select="." /></dd>
		  	</xsl:for-each>	   		
    		</xsl:if>
	   		<xsl:if test="remarks">		
    		<dt>Remarks</dt>
    		<dd><xsl:apply-templates select="remarks" /></dd>
    		</xsl:if> 
  	   		<xsl:if test="exceptions/exception">	
  	   		<dt>Throws</dt>
    		<dd><xsl:apply-templates select="exceptions" /></dd>
    		</xsl:if>   		
	   		<xsl:if test="references/reference">		
    		<dt>See Also</dt>
    		<dd><p><xsl:apply-templates select="references" /></p></dd>
    		</xsl:if>
	   		<xsl:if test="availability/specification">		
    		<dt>Availability</dt>
    		<dd><p><xsl:apply-templates select="availability" /></p></dd>
    		</xsl:if>
    		<!--
		 	<xsl:if test="browsers/browser">
		 	<a name="{@type}.Platform Support" ></a>
		 	<dt>Platform Support</dt>
		 	<dd><p><xsl:call-template name="browser-table" /></p></dd>
		 	</xsl:if>	
		 	-->		
	   		<xsl:if test="@visibility != 'basic'">	
    		<dt>Visibility</dt>
    		<dd><xsl:value-of select="@visibility" /></dd>
    		</xsl:if>  
       	</dl>
  </xsl:template>

  <xsl:template name="methods-summary">
    	<table cellspacing="3" width="90%">
			<tr class="compheader">
				<th align="left">Method</th>
		    	<xsl:for-each select="$browsers">
		    		<th><xsl:value-of select="." /></th>
				</xsl:for-each>
			</tr>
	 	  	<xsl:for-each select="methods/method">
		  			<xsl:call-template name="item-summary" />
		  	</xsl:for-each>
		</table>
  </xsl:template>
  
  <xsl:template name="item-summary">
  		<xsl:param name="name" select="@name" />
  		<xsl:param name="browsers" select="browsers" />
	  	<tr>
	  		<td class="declaration" rowspan="2">
	
	  		<div class="name"><a name="#{../../@name}.{@name}"></a>
	  			<a href="#{../../@type}.{@name}" title="{description}"><xsl:value-of select="$name" /></a>
	  		</div>
	  		<div><xsl:apply-templates select="description" /></div>
	
			</td>
			<xsl:apply-templates select="$browsers" />
		</tr>
		<tr>
			<td colspan="5">
				<xsl:choose>
						<xsl:when test="$browsers/browser/description">
							<xsl:call-template name="browser-notes" />
						</xsl:when>
						<xsl:otherwise>
							&#160;
						</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>  
  </xsl:template>
  
  <xsl:template name="method">
	<a name="#{../../@type}.{@name}"></a>
	<h3><xsl:call-template name="ancestors" /><xsl:value-of select="@name" />(<xsl:call-template name="parameters-condensed" />)&#160;:&#160;<xsl:if test="@scope != 'instance'"><xsl:value-of select="@scope" />&#160;</xsl:if><i><xsl:apply-templates select="return-types" /></i></h3>
	<p class="padded"><xsl:apply-templates select="description" /> <xsl:if test="@description != @return-description">&#160;<xsl:apply-templates select="return-description" /></xsl:if></p>
	<xsl:if test="parameters/parameter">
	<p class="padded">
		<xsl:apply-templates select="parameters" />
	</p>
	</xsl:if>
    	<dl class="details">
    		<xsl:if test="examples">
    		<dt>Examples</dt>
	 	  	<xsl:for-each select="examples/example">
    			<dd><xsl:apply-templates select="." /></dd>
		  	</xsl:for-each>	   		
    		</xsl:if>
	   		<xsl:if test="remarks/text()">		
    		<dt>Remarks</dt>
    		<dd><xsl:apply-templates select="remarks" /></dd>
    		</xsl:if>
	   		<xsl:if test="exceptions/exception">		
    		<dt>Throws</dt>
    		<dd><xsl:apply-templates select="exceptions" /></dd>
    		</xsl:if>
	   		<xsl:if test="references/reference">		
    		<dt>See Also</dt>
    		<dd><p><xsl:apply-templates select="references" /></p></dd>
    		</xsl:if>
	   		<xsl:if test="availability/specification">		
    		<dt>Availability</dt>
    		<dd><p><xsl:apply-templates select="availability" /></p></dd>
    		</xsl:if>
    		<!--
		 	<xsl:if test="browsers/browser">
		 	<a name="{@type}.Platform Support" ></a>
		 	<dt>Platform Support</dt>
		 	<dd><p><xsl:call-template name="browser-table" /></p></dd>
		 	</xsl:if>
		 	-->		
    	</dl>
  </xsl:template>

  <xsl:template name="ancestors">
	<xsl:for-each select="ancestor::*"><xsl:if test="@name"><xsl:value-of select="@name" />.</xsl:if></xsl:for-each>
  </xsl:template>
  
  <xsl:template match="example">
    <p><xsl:value-of select="." disable-output-escaping="yes"/></p>
  </xsl:template>

  <xsl:template match="description">
    <xsl:choose>
	<xsl:when test="not(normalize-space(.))">
		No description provided.
	</xsl:when>
	<xsl:otherwise>
	    <xsl:value-of select="." disable-output-escaping="yes"/>
	</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="return-description">
    <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>
    
  <xsl:template match="remarks">
    <xsl:value-of select="." disable-output-escaping="yes"/>
  </xsl:template>

  <xsl:template name="parameters-condensed">
  	<xsl:for-each select="parameters/parameter">
	  	<xsl:if test="@usage = 'optional'">[</xsl:if>
  		<i><xsl:value-of select="@type" /></i>&#160;<xsl:value-of select="@name" />
  		<xsl:if test="position() != last()">,</xsl:if>
	  	<xsl:if test="@usage = 'optional'">]</xsl:if>
  		<xsl:if test="position() != last()">&#160;</xsl:if>
  	</xsl:for-each>
  </xsl:template>
  
  <xsl:template match="parameters">
  	<table cellspacing="3" width="90%" class="parameter-table">
  	<xsl:for-each select="parameter">
		<tr>
		  	<td width="10%">
  				<i><xsl:value-of select="@type" /></i>
  			</td>
			<td width="10%">
  				<b><xsl:value-of select="@name" /></b>
  			</td>
		  	<td width="80%">
  				<xsl:apply-templates select="description" /> <xsl:if test="@usage != 'required'">&#160;<b>(<xsl:value-of select="@usage" />)</b></xsl:if>
  			</td>
  		</tr>
  	</xsl:for-each>
  	</table>
  </xsl:template>

  <xsl:template match="exceptions">
     <dl>
	    <xsl:apply-templates select="exception" />
	</dl>
  </xsl:template>

  <xsl:template match="exception">
	  <xsl:if test="@name">  
      	<dt><xsl:value-of select="@name" /></dt>
      </xsl:if>
	  <dd><xsl:apply-templates select="description" /></dd>
  </xsl:template>
  
  <xsl:template match="references">
	<xsl:for-each select="reference">
		<xsl:variable name="tokenizedSample" select="tokenize(@name,'\.')"/>
	  	<a>
	  		<xsl:choose>
				<xsl:when test="starts-with(@name,lower-case(substring(@name,1,1)))">
					<xsl:attribute name="href">#<xsl:value-of select="@name"/></xsl:attribute>
				</xsl:when>
	  			<xsl:when test="count($tokenizedSample) = 1">
			  		<xsl:attribute name="href"><xsl:value-of select="$tokenizedSample[1]"/>.html</xsl:attribute>
	  			</xsl:when>
	  			<xsl:otherwise>
			  		<xsl:attribute name="href"><xsl:value-of select="$tokenizedSample[1]"/>.html#<xsl:value-of select="@name"/></xsl:attribute>
	  			</xsl:otherwise>
	  		</xsl:choose>
	  		<xsl:value-of select="@name" />
	  	</a>
  		<xsl:if test="position() != last()">&#160;|&#160;</xsl:if>
  	</xsl:for-each>
  </xsl:template>

  <xsl:template match="availability">
	<xsl:for-each select="specification">
	  	<xsl:value-of select="@name" />
  		<xsl:if test="position() != last()">&#160;|&#160;</xsl:if>
  	</xsl:for-each>
  </xsl:template>

  <xsl:template match="return-types">
  		<xsl:for-each select="return-type">
		    <xsl:value-of select="@type" />
		</xsl:for-each>
  </xsl:template>
  
  <xsl:template name="browser-table">
	<table cellspacing="3" width="90%">
	<tr class="compheader">
		<xsl:call-template name="browser-headers" />
	</tr>
		<xsl:apply-templates select="browsers" />
	<xsl:if test="browsers/browser/description">
		<tr>
			<td colspan="5">
				<xsl:call-template name="browser-notes" />
			</td>
		</tr>			
	</xsl:if>
	</table>
  </xsl:template>

  <xsl:template name="browser-headers">
  		    	<xsl:for-each select="$browsers">
		    		<th><xsl:value-of select="." /></th>
				</xsl:for-each>
  </xsl:template>
  
  <xsl:template name="browser-notes">
	<ul>
	<xsl:for-each select="browsers/browser[description/text()]">
		<li><xsl:value-of select="@platform" />: <xsl:value-of select="description" /></li>
	</xsl:for-each>
	</ul>
  </xsl:template>
  
    <xsl:template match="browsers">
  	<xsl:variable name="elementBrowsers" select="." />
	<xsl:for-each select="$browsers">
		<td align="center">
	  	<xsl:variable name="currentBrowser" select="." />
		<xsl:attribute name="title" select="$currentBrowser" />
		<xsl:choose>
		<xsl:when test="$elementBrowsers/browser/@platform = .">	
		  	<xsl:variable name="currentBrowserDescription" select="$elementBrowsers/browser[@platform = $currentBrowser]/description" />
		  	<xsl:choose>
		  		<xsl:when test="contains($currentBrowserDescription, 'buggy')">
					<xsl:attribute name="class">comparison buggy</xsl:attribute>
				</xsl:when>
		  		<xsl:when test="string-length($currentBrowserDescription) > 0">
					<xsl:attribute name="class">comparison incomplete</xsl:attribute>
				</xsl:when>
		  		<xsl:otherwise>
					<xsl:attribute name="class">comparison yes</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:for-each select="$elementBrowsers/browser[@platform = $currentBrowser]"><xsl:value-of select="@version" /></xsl:for-each>
		</xsl:when>	
		<xsl:otherwise><xsl:attribute name="class">comparison no</xsl:attribute>no</xsl:otherwise>
		</xsl:choose>
		</td>
  	</xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
