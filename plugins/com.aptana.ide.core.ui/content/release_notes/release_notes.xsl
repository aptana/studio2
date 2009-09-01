<?xml version="1.0"?>

<!-- Sample stylesheet which renders JIRA's RSS (XML) output in a HTML format similar to the {jiraissues} macro in Confluence. See navigator-rss.jsp for how to apply this automatically. -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:param name="url" select="'undefined.jira.url'"/>

	<xsl:param name="title_type" select="'T'"/>
	<xsl:param name="title_key" select="'Key'"/>
	<xsl:param name="title_summary" select="'Summary'"/>
	<xsl:param name="title_status" select="'Status'"/>

	<xsl:param name="title_resolution" select="'Res'"/>

	<xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz'" />
	<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ '" />

	<xsl:template match="/notes/channel">
		<html>
			<head>
				<link rel="stylesheet" href="http://confluence.atlassian.com/styles/main-action.css" type="text/css"/>
				<title>Release Notes - <xsl:value-of select="title"/></title>

			</head>
			<body>
				<table class="grid" cellspacing="0" width="100%">
					<caption style="text-align: left; font-weight: bold">
							<a href="{$url}"><xsl:value-of select="title"/></a>
							<span class="smalltext"> (<xsl:value-of select="count(item)"/> issues)</span>
						</caption>

					<tbody>
						<tr>
							<th style="text-align: left;"> <xsl:value-of select="$title_type"/> </th>
							<th style="text-align: left;"> <xsl:value-of select="$title_key"/> </th>
							<th style="text-align: left;"> <xsl:value-of select="$title_summary"/> </th> 
							<th style="text-align: left;"> <xsl:value-of select="$title_status"/> </th>

							<th style="text-align: left;"> <xsl:value-of select="$title_resolution"/> </th>
						</tr>
						<xsl:for-each select="item">
							<tr>
								<xsl:attribute name="class">
									<xsl:choose>
										<xsl:when test="position() mod 2 = 0">rowNormal</xsl:when>

										<xsl:otherwise>rowAlternate</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:apply-templates select="type"/>
								<xsl:apply-templates select="key"/>
								<xsl:apply-templates select="summary"/>
								<xsl:apply-templates select="status"/>
								<xsl:apply-templates select="resolution"/>

							</tr>
						</xsl:for-each>

					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>


	<xsl:template match="type">
		<td nowrap="true">
			<a href="{../link}"><img src="{@iconUrl}" alt="{.}" title="{.}" border="0"/></a>
		</td>
	</xsl:template>

	<xsl:template match="key">
		<td nowrap="true">
			<a href="{../link}"><xsl:value-of select="."/></a>

		</td>
	</xsl:template>

	<xsl:template match="summary">
		<td nowrap="true">
			<a href="{../link}"><xsl:value-of select="."/></a>
		</td>
	</xsl:template>

	<xsl:template match="status">

		<td nowrap="true">
			<img src="{@iconUrl}" border="0"/><xsl:text> </xsl:text><xsl:value-of select="."/>
		</td>
	</xsl:template>

	<xsl:template match="resolution">
		<td nowrap="true">
			<font color="#ff0000"><xsl:value-of select="translate(., $lowercase, $uppercase)"/></font>
		</td>

	</xsl:template>

	<!--
	<xsl:template match="channel/link"/>
	<xsl:template match="channel/title"/>
	<xsl:template match="channel/description"/>
	<xsl:template match="channel/language"/>
	-->


</xsl:stylesheet>
<!-- vim: set tw=10000:-->
