/* -------------------------------------------------------------------- */
/* plugin_manager.js templates											*/
/* -------------------------------------------------------------------- */
var pluginListTemplate = new Template('<table cellpadding="3" cellspacing="0" border="0" width="100%"><tr><td colspan="3" class="bold size14 pad3">#{title}</td></tr>#{pluginList}</table>');
var pluginItemTemplate = new Template('<tr><td class="pad3"><table class="pluginBox" width="100%" style="margin: 0px 0px 3px 0px;"><tr>' 
						+ '<td style="padding: 8px 5px 5px 8px; width: 22px;" class="center" valign="top">#{pluginImage}</td>'
						+ '<td style="padding: 5px;"><strong>#{pluginName}</strong><div class="size10">#{pluginDesc}</div>'
						+ '<div>#{updateStatus}</div></td></tr></table></td></tr>');
						
