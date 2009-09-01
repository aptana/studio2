var PluginManagerModule = Class.create(
{
	initialize: function initialize()
	{
		this.templates		= {};
	},
	
	finishInit: function finishInit()
	{
		this.extendSelf();
		this.registerObservers();
		
		this._init();
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		
	},
	
	registerObservers: function registerObservers()
	{
		Portal.Data.plugins.observe('update', function()
        {
			this._init();
        }.bind(this));
	},
	
	extendSelf: function extendSelf()
	{
		Object.extend(this, PluginManagerControllers);
		Object.extend(this, PluginManagerViews);
	}
});

var PluginManagerControllers = 
{
	_init: function _init()
	{
		if(Portal.Data.plugins.loading == true)
		{
			return;
		}
		
		var totalUpdated = 0;
		var totalNew = 0;
		
		Portal.Data.plugins.pluginChanges.each(function(data)
		{
			if(data.oldVersion == '')
			{
				totalNew ++;
			}
			else
			{
				totalUpdated ++;
			}
		});
		
		this.totalUpdated = totalUpdated;
		this.totalNew = totalNew;
		
		this._initViews();
	},
	
	installPlugin: function installPlugin(pluginId)
	{
		EventManager.publish('/portal/plugins/install', pluginId);	
	},
	
	showUpdates: function showUpdates()
	{
		EventManager.publish('/portal/plugins/updates', {});	
	}
}

var PluginManagerViews = 
{
	_initViews: function _initViews()
	{
        View.load(
        {
			main:
			{
				file: 'templates/plugins/plugins.html',
				binding: function()
				{
					return $('plugin_content');
				},
				scope:
				{
					popularPlugins: Portal.Data.plugins.popularPlugins,
					otherPlugins: Portal.Data.plugins.otherPlugins,
					pluginList: Portal.Data.plugins.pluginList,
					pluginChanges: Portal.Data.plugins.pluginChanges,
					updatesExist: Portal.Data.plugins.updatesExist,
					totalUpdated: this.totalUpdated,
					totalNew: this.totalNew
				},
				behaviors: function()
				{
					new Control.Tabs('plugin_tabs');
				}
			}
		}, 
		function(templates)
        {
			this.templates = templates;
			
			this.templates.main.render();
			
        }.bind(this));
	}
}
