var PluginsClass = Class.create(
{
	initialize: function initialize()
	{
		this.updatesExist		= false;
		this.totalUpdates		= 0;
		this.pluginChanges		= '';
		this.numNew				= 0;
		this.numUpdated			= 0;
		this.pluginList			= new Hash();
		this.popularPlugins		= new Hash();
		this.otherPlugins		= new Hash();
		
		this.loading			= true;
	},
	
	finishInit: function finishInit()
	{
        EventManager.subscribe('/portal/features/changes/', 
        {
            channelHook: 'plugins',
            onComplete: function()
            {
				this.checkForUpdates();
            }.bind(this)
        });
		
        EventManager.subscribe('/portal/plugins/list/', 
        {
			channelHook: 'plugins',
			onComplete: function()
			{
				EventManager.publish('/portal/plugins/list', { request: 'listPlugins' });
			}
		});
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		if(msg.data.response == 'featureChanges')
		{
			this.checkForUpdates(msg);
		}
		else if (msg.data.response == 'listPlugins')
		{
			this.update(msg.data);
		}
	},
	
	checkForUpdates: function checkForUpdates(msg)
	{
		if(!msg)
		{
            EventManager.publish('/portal/features/changes', 
            {
                request: 'featureChanges'
            });
		}
		else
		{
			this.updatesExist	= msg.data.changesExist;
			this.totalUpdates	= msg.data.changes.length;
			this.pluginChanges	= msg.data.changes;
			
			if(this.updatesExist && $('notify_plugins'))
			{
				var newHtml = this.totalUpdates + ' Plugin';
				newHtml += (this.totalUpdates == 1) ? ' ' : 's ';
				newHtml += 'Updated (<a href="javascript: Portal.Vars.pluginDetailsClicked = true; Portal.Modules.MyAptana.setCurrentProduct(\'plugins\');">details</a>)<div></div>';
				// test code to show the notification:
				// $('notify_plugins').update('2 Plugins Updated (<a href="javascript: Portal.Vars.pluginDetailsClicked = true; Portal.Modules.MyAptana.setCurrentProduct(\'plugins\');">details</a>)<div></div>').show();
				$('notify_plugins').update(newHtml);
				new Effect.Appear('notify_plugins');
			}
		}
	},
	
	update: function update(data)
	{
		this.pluginList = new Hash();
		
        $H(data).each(function(item)
        {
			if(item.key == 'response')
			{
				return;
			}
			
			var plugins = new Hash();
			
            item.value.each(function(plugin)
            {
				plugins.set(plugin.name, plugin);
            });
			
			this.pluginList.set(item.key, plugins);
			
        }.bind(this));
		
		/*
		var tempList = new Hash();
		var sortList = this.pluginList.keys();
		
		sortList.sort();
		
        sortList.each(function(item)
        {
			tempList.set(item, this.pluginList.get(item));
        }.bind(this));
		
		this.pluginList = tempList;
		*/
		
		if(this.loading == true)
		{
			this.loading = false;
		}
		
		this.notify('update');
	},
	
	updatePopularPlugins: function updatePopularPlugins(data)
	{
        data.each(function(item)
        {
			var name = item.name;
			
			if(!this.popularPlugins.get(name))
			{
				var tempPlugin = new Plugin(item);
			}
			else
			{
				var tempPlugin = this.popularPlugins.get(name);
				tempPlugin.update(data);
			}
			
			this.popularPlugins.set(name, tempPlugin);
        }.bind(this));
		
		// now, we sort them...
		
		/*
		var nameArray = this.popularPlugins.keys();
		nameArray.sort();
		
		var tempHash = new Hash();
        nameArray.each(function(item)
        {
			tempHash.set(item, this.popularPlugins.get(item));
        }.bind(this));
		
		this.popularPlugins = tempHash;
		*/
	},
	
	updateOtherPlugins: function updateOtherPlugins(data)
	{
        data.each(function(item)
        {
			var name = item.name;
			
			if(!this.otherPlugins.get(name))
			{
				var tempPlugin = new Plugin(item);
			}
			else
			{
				var tempPlugin = this.otherPlugins.get(name);
				tempPlugin.update(data);
			}
			
			this.otherPlugins.set(name, tempPlugin);
        }.bind(this));
		
		// now, we sort them...
		
		/*
		var nameArray = this.otherPlugins.keys();
		nameArray.sort();
		
		var tempHash = new Hash();
        nameArray.each(function(item)
        {
			tempHash.set(item, this.otherPlugins.get(item));
        }.bind(this));
		
		this.otherPlugins = tempHash;
		*/
	}
});
Object.Event.extend(PluginsClass);

var Plugin = Class.create(
{
	initialize: function initialize(data)
	{
		this.id				= '';
		this.name			= '';
		this.description	= '';
		this.installed		= false;
		this.link			= '';
		this.more			= '';
		this.updateExists	= false;
		
		this.update(data);
	},
	
	update: function update(data)
	{
		if('id' in data)
		{
			this.id = data.id;
		}
		
		if('name' in data)
		{
			this.name = data.name;
		}
		
		if('description' in data)
		{
			this.description = data.description;
		}
		
		if('installed' in data)
		{
			this.installed = data.installed;
		}
		
		if('link' in data)
		{
			this.link = data.link;
		}
		
		if('more' in data)
		{
			this.more = data.more;
		}
		
		if('update' in data)
		{
			this.updateExists = data.update;
		}
	}
});

