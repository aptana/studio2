<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/TR/xhtml1/strict">
  <xsl:output method="html" indent="no"  encoding="utf-8"/>
  <xsl:param name="ReferenceName" select="/javascript/@fileName" />
  <xsl:param name="ReferenceDisplayName" select="/javascript/@fileDisplayName" />

  <xsl:template match="/javascript">
    <xsl:call-template name="index" />
  </xsl:template>

  <xsl:template name="index">
    <html xmlns="http://www.w3.org/1999/xhtml">
       <head>
          <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"></meta>
          <title>Visual ScriptDoc</title>
          <style type="text/css">
          	body {
			  background-color: #2B313C;
			  color: #fff;
			  font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;
			  font-size: 56.5%;
			  padding: 0;
			  margin: 0;
			  overflow: hidden;
			  height: 100%;
			  width: 100%;
			  position: relative;
			}
			
			#wrapper {
			  position: absolute;
			  overflow: auto;
			  top: 20px;
			  bottom: 0;
			  width: 100%;
			  height: expression((document.body.clientHeight - 20 + "px"));
			}
			
			#footer {
			  position: absolute;
			  top: 0px;
			  width: 100%;
			  background-color: #434D5E;
			  height: 20px;
			  overflow: hidden;
			}
			
			body * {
			  margin: 0;
			  padding: 0;
			}
			
			dt {
			  background-image: url(images/header.png);
			  width: 145px;
			  height: 25px;
			  line-height: 25px;
			  text-align: center;
			  font-size: 1.2em;
			  cursor: pointer;
			}
			
			dl {
			  position: absolute;
			  top: 10px;
			  left: 10px;
			}
			
			dl dd {
			  margin-left: 0;
			  display: none;
			}
			
			.absolute dt {
			  background-image: url(images/grey-button.png);
			}
			
			dt.direct {
			  background-image: url(images/blue-button.png);
			  overflow: hidden;
			}
			
			dl.absolute {
			  position: absolute;
			  margin-left: 0;
			}
			
			dd.text {
			  background-color: #48b;
			  padding: 0.5em;
			  font-size: 1.2em;
			  margin-right: 0.5em;
			  height: auto;
			  top: 10px;
			  position: absolute;
			}
			
			dd.text p, dd.text h1, dd.text h2, dd.text h3, dd.text ul, dd.text pre {
			  margin-bottom: 0.5em;
			}
			
			dd.text h1 {
			  font-weight: bold;
			  border-bottom: 1px solid #ccc;
			  font-size: 1em;
			}
			
			dd.text h2 {
			  padding: 0.5em;
			  background-color: #269;
			  text-align: center;
			  font-size: 1em;
			}
			
			dd.text h3 {
			  font-size: 1em;
			}
			
			dd.text ul {
			  list-style-type: none;
			  padding-left: 0;
			}
			
			dd.text li {
			  margin-bottom: 0.5em;
			}
			
			dd.text pre {
			  background-color: #269;
			  padding: 0.5em;
			  white-space: pre;
			  overflow: auto;
			  font-size: 1.2em;
			}
			
			.cheat {
			  float: right;
			}
			
			dt.active {
				-moz-opacity: 0.5;
				opacity: 0.5;
				filter: alpha(opacity=50);
			}
			
			#left-button, #right-button {
			  margin-top: 2px;
			}
			
			#left-button {
			  float: left;
			  margin-left: 1px;
			}
			
			#right-button {
			  float: right;
			  margin-right: 1px;
			}
			
			#footer p {
			  text-align: center;
			  line-height: 20px;
			  font-weight: bold;
			  font-size: 1.2em;
			  width: 40%;
			  float: left;
			}
			
			#footer a {
			  color: #8cf;
			}
			
			#footer a:hover {
			  color: #6ad;
			}
          </style>
       </head>
       <body>
          <div id="footer"><a id="left-button"><img src="images/arrow-back_16.gif"></img></a><a id="right-button"><img src="images/arrow-forward_16.gif"></img></a><p id="header"><xsl:value-of select="$ReferenceName" />
             </p>
             <p id="current-path"></p>
          </div>
          <div id="wrapper">
             <dl>
    		    <xsl:for-each select="property|function|object_literal">
					<xsl:sort select="@name"/>
    	   				<xsl:call-template name="class" />
    		    </xsl:for-each>
            </dl>
          </div><script type="text/javascript" src="jquery.pack.js"></script><script type="text/javascript" src="dimensions.js"></script><script type="text/javascript" src="new_vjq.js"></script></body>
    </html>
  </xsl:template>

  <xsl:template name="global">
        <dt xmlns="">Global</dt>
        <dd xmlns="">
           <dl>
    		    <xsl:for-each select="property[@ignored='false' and not(/function) and not(/property) and not(/object_literal)]">
    	   			<xsl:call-template name="item" />
    		    </xsl:for-each>
    		    <xsl:for-each select="function[@ignored='false' and not(/function) and not(/property) and not(/object_literal)]">
    	   			<xsl:call-template name="item" />
    		    </xsl:for-each>
    		    <xsl:for-each select="object_literal[@ignored='false' and not(/function) and not(/property) and not(/object_literal)]">
    	   			<xsl:call-template name="item" />
    		    </xsl:for-each>
    		</dl>
    	</dd>
  </xsl:template>

  <xsl:template name="class">
		<xsl:choose>
			<xsl:when test="descendant::*/@ignored">
				<xsl:call-template name="item-children" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="item" />
			</xsl:otherwise>
		</xsl:choose>
  </xsl:template>

  <xsl:template name="item-children">
        <dt><xsl:value-of select="@name" /></dt>
        <dd>
           <dl>
    		    <xsl:for-each select="property|function|object_literal">
    	   			<xsl:call-template name="class" />
    		    </xsl:for-each>
    		</dl>
    	</dd>
  </xsl:template>

  <xsl:template name="item">
		<xsl:apply-templates select="." />
  </xsl:template>

  <xsl:template match="property">
      <dt class="direct"><xsl:value-of select="@name" /></dt>
      <dd class="text">
         <p class="cheat"></p>
         <h1><xsl:value-of select="@name" /></h1>
         <p><xsl:apply-templates select="description" /></p>
         <h2>Returns</h2>
         <p><xsl:value-of select="@type" /></p>
    	 <xsl:if test="parameters/parameter">
         	<h2>Parameters</h2>
    		<xsl:apply-templates select="parameters" />
    	 </xsl:if>
	</dd>
  </xsl:template>

  <xsl:template match="function">
      <dt class="direct"><xsl:value-of select="@name" />(<xsl:call-template name="parameters-condensed" />)</dt>
      <dd class="text">
         <p class="cheat"></p>
         <h1><xsl:value-of select="@name" /></h1>
         <p><xsl:apply-templates select="description" /></p>
         <h2>Returns</h2>
         <p>
			<xsl:choose>
			<xsl:when test="return-types/return-type">
          		<xsl:apply-templates select="return-types" />
			</xsl:when>
			<xsl:otherwise>
				void
			</xsl:otherwise>
			</xsl:choose>
		</p>
    	 <xsl:if test="parameters/parameter">
         	<h2>Parameters</h2>
    		<xsl:apply-templates select="parameters" />
    	 </xsl:if>
	</dd>
  </xsl:template>

  
  <xsl:template name="method">
	<a name="#{../../@type}.{@name}"></a>
	<h3><xsl:call-template name="ancestors" /><xsl:value-of select="@name" />(<xsl:call-template name="parameters-condensed" />)&#160;:&#160;<xsl:if test="@scope != 'instance'"><xsl:value-of select="@scope" />&#160;</xsl:if><i></i></h3>
	<p class="padded"><xsl:apply-templates select="description" /> <xsl:if test="@description != @return-description">&#160;<xsl:apply-templates select="return-description" /></xsl:if></p>
	<xsl:if test="parameters/parameter">
	<p class="padded">
		<xsl:apply-templates select="parameters" />
	</p>
	</xsl:if>
    	<!--<dl class="details">
   		<xsl:if test="example/text()">		
    		<dd><xsl:apply-templates select="example" /></dd>
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
    	</dl>-->
  </xsl:template>

  <xsl:template name="ancestors">
	<xsl:for-each select="ancestor::*"><xsl:if test="@name"><xsl:value-of select="@name" />.</xsl:if></xsl:for-each>
  </xsl:template>
  
  <xsl:template match="example">
    <xsl:value-of select="." disable-output-escaping="yes"/>
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
  		<xsl:value-of select="@name" />
  		<xsl:if test="position() != last()">,</xsl:if>
	  	<xsl:if test="@usage = 'optional'">]</xsl:if>
  		<xsl:if test="position() != last()"></xsl:if>
  	</xsl:for-each>
  </xsl:template>
  
  <xsl:template match="parameters">
  	<ul>
  	<xsl:for-each select="parameter">
		<li><strong><xsl:value-of select="@name" /></strong> (<b><xsl:value-of select="@name" /></b>)
		  	<xsl:apply-templates select="description" />
		</li>
  	</xsl:for-each>
  	</ul>
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


</xsl:stylesheet>