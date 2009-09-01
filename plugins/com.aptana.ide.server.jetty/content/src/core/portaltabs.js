var PortalTabsObject = Class.create(
{
	initialize: function inititalize()
	{
		this.tabContainer 		= 'portal_tabs';
		this.currentTab			= '';
		this.tabs				= new Hash();
		this.switchingTabs		= false;
	},
	
	finishInit: function finishInit(newDefault)
	{
		// check to make sure we've got sites, otherwise we don't want to show the "my cloud" tab
		if(typeof(Portal.Data.siteList) != 'undefined' && Portal.Data.siteList.sites.size() == 0)
		{
			var cloudTab = this.tabs.get('my_cloud');
			cloudTab.display = false;
			
			if(Portal.Vars.newDefaultTab == 'my_cloud' || newDefault == 'my_cloud')
			{
				Portal.Vars.newDefaultTab = 'my_aptana';
				newDefault = 'my_aptana';
				Portal.Vars.productToShow = 'cloud';
			}
		}
		
		// this isn't the best logic for true "dynamic" stuff, but since there's only one tab that works in offline mode,
		// this works for now...
		if(newDefault && this.tabs.get(newDefault) && Portal.Modules.OnlineStatus.isOnline)
		{
            this.tabs.each(function(tab)
            {
				var tempValue = tab.value;
				
				if (Portal.Modules.AptanaIdManager.isLoggedIn) 
				{
                    tempValue.isDefault = (tab.key == newDefault) ? true : false;
				}
				else
				{
					tempValue.isDefault = (tab.key == newDefault && !tab.value.requireLogin) ? true : false;
				}
				
				this.tabs.set(tab.key, tempValue);
				
            }.bind(this));
			
		}
		
        Portal.Modules.OnlineStatus.observe('onlineStatusChanged', function(isOnline)
        {
			this.buildTabs();
			
        }.bind(this));
		
		this.sortTabs();
		
		// this.buildTabs();
		this.loadDefaultPortal();
	},
	
	registerTab: function registerTab(name, displayOrder, options, forceRedraw, callback)
	{
		if(this.tabs.get(name))
		{
			return;	
		}
		
        var newTabData = Object.extend(
        {
			name: 			name,
			moduleName:		'',
			moduleObj:		'',
			isDefault:		false,
			title:			'',
			requireLogin:	false,
			requireOnline:	false,
			order:			displayOrder,
			display:		true
		}, options || {});
		
		this.tabs.set(name, newTabData);
		
		if(typeof(callback) == 'function')
		{
			callback();
		}
	},
	
	sortTabs: function sortTabs()
	{
		var tabsArray = new Array();
		var tempHash = new Hash();
		
		this.tabs.each(function(item)
		{
			var index = item.value.order;
			var name = item.value.name;
			
			if(tabsArray.indexOf(index) != -1)
			{
				while(tabsArray.indexOf(index) != -1)
				{
					index ++;
				}
			}
			
			tabsArray[index] = name;
		});
		
        tabsArray.each(function(item)
        {
			tempHash.set(item, this.tabs.get(item));
        }.bind(this));
		
		this.tabs = tempHash;
	},
	
	buildTabs: function buildTabs()
	{
		this.sortTabs();
		
		var userLoggedIn = Portal.Modules.AptanaIdManager.isLoggedIn;
		
		// clear all the event listeners if we need to
		if(this.boundSwitchTabs)
		{
            $A($$('a.portal_tab')).each(function(item)
            {
				Event.stopObserving(item, 'click', this.boundSwitchTabs);
            }.bind(this));
		}
		
		$(this.tabContainer).update('');
		var setActive = false;
		
		this.tabs.each(function(item)
		{
			if((item.value.requireLogin == true && userLoggedIn == false) || (item.value.requireOnline && Portal.Modules.OnlineStatus.isOnline == false) || item.value.display == false)
			{
				if($('content_tab_' + item.key))
				{
					$('content_tab_' + item.key).remove();
				}
				
				if(this.currentTab == 'tab_' + item.key)
				{
					this.currentTab = '';
					this.loadDefaultPortal();
				}
				
				return;
			}
			
			var tabTemplate = new Template('<li id="tab_#{tabKey}" #{activeClass}><a href="#" class="portal_tab" id="tab_link_#{tabKey}"><span>#{tabTitle}</span></a></li>');
			
			var activeClass = '';
			
			if(item.value.isDefault == true  && this.currentTab == '' && !setActive)
			{
				setActive = true;
				activeClass = 'class="active"';
				this.currentTab = 'tab_' + item.key;
			}
			else if ('tab_' + item.key == this.currentTab)
			{
				activeClass = 'class="active"';
			}
			
            $(this.tabContainer).insert(
            {
                bottom: tabTemplate.evaluate(
                {
                    tabKey: item.key,
                    activeClass: activeClass,
                    tabTitle: item.value.title
                })
            });
            
			
			if(!$('content_tab_' + item.key))
			{
                $('portal_content').insert(
                {
                    bottom: '<div id="content_tab_' + item.key + '" style=""></div>'
                });
			}
			
		}.bind(this));
		
		this.injectTabBehavior();
	},
	
	injectTabBehavior: function injectTabBehavior()
	{
		this.boundSwitchTabs = this.switchTabs.bindAsEventListener(this);
		
        $A($$('a.portal_tab')).each(function(item)
        {
			Event.observe(item, 'click', this.boundSwitchTabs);
			 
        }.bind(this));
	},
	
	switchTabs: function switchTabs(event)
	{
		var newTab = Event.element(event).up().id.replace('link_', '');
		
		if(newTab == this.currentTab)
		{
			return;
		}
		
		if(this.switchingTabs == true)
		{
			return;
		}
		
		var trackingId = '';
			
		switch(newTab)
		{
			case 'tab_my_cloud':
				trackingId = 'ma.mc.op';
				break;
			case 'tab_my_account':
				trackingId = 'ma.ma.op';
				break;
			case 'tab_my_aptana':
				trackingId = 'ma.map.op';
				break;
			default:
				trackingId = '';
		}
		
		if(trackingId != '')
		{
			Portal.API.utils.sendTrackingData(trackingId);
		}
		
		if($('content_' + newTab).innerHTML != '')
		{
			this.switchingTabs = true;
			
            new Effect.Fade('content_' + this.currentTab, 
            {
                duration: 0.25,
				afterFinish: function()
				{
					if(!$(this.currentTab))
					{
						return;
					}
					
					$(this.currentTab).removeClassName('active');
					this.currentTab = newTab;
					
					$(this.currentTab).addClassName('active');
					
                    new Effect.Appear('content_' + this.currentTab, 
                    {
                        duration: 0.25,
                        afterFinish: function()
                        {
							this.switchingTabs = false;
							
							this.notify('tabFocusChanged', this.currentTab);
							
							
                        }.bind(this)
                    })
				}.bind(this)
            });
			
			return;
		}
		
        new Effect.Fade('content_' + this.currentTab, 
        {
            duration: 0.25,
            afterFinish: function()
            {
				$(this.currentTab).removeClassName('active');
				this.currentTab = newTab;
				$(this.currentTab).addClassName('active');
				
				var contentToLoad = this.tabs.get(newTab.replace('tab_', ''));
				
				if(contentToLoad != '')
				{
					Portal.API.modules.loadModule(contentToLoad.moduleName, contentToLoad.moduleObj, true);
				}
            }.bind(this)
        });
	},
	
	unregisterTab: function unregisterTab(tab)
	{
		try
		{
			var tempTab = this.tabs.get(tab);
			
			if(!tempTab || tempTab.display == false)
			{
				return;
			}
			
			tempTab.display = false;
			
			if(tab == 'my_cloud')
			{
				Portal.Vars.productToShow = 'cloud';
			}
			
			this.tabs.set(tab, tempTab);
			
			this.buildTabs();
			
			// make sure we're not on the tab being removed...
			if($('content_tab_my_aptana').innerHTML == '')
			{
				this.loadTabManually('tab_my_aptana');
			}
		}
		catch (e)
		{
			console.warn('Tab unregister error: ' + e);
		}
	},
	
	reRegisterTab: function reRegisterTab(tab)
	{
		try
		{
			var tempTab = this.tabs.get(tab);
			
			if(!tempTab || tempTab.display == true)
			{
				return;
			}
			
			tempTab.display = true;
			
			this.tabs.set(tab, tempTab);
			
			this.buildTabs();
		}
		catch (e)
		{
			console.warn('Tab re-register error: ' + e);
		}
	},
	
	loadTabManually: function loadTabManually(newTab)
	{
		$('content_' + newTab).innerHTML = '';
		
		this.switchingTabs = true;
		
		new Effect.Fade('content_' + this.currentTab, 
        {
            duration: 0.25,
            afterFinish: function()
            {
				if($(this.currentTab))
				{
					$(this.currentTab).removeClassName('active');
				}
				
				this.currentTab = newTab;
				$(this.currentTab).addClassName('active');
				
				var contentToLoad = this.tabs.get(newTab.replace('tab_', ''));
								
				if(contentToLoad != '')
				{
					Portal.API.modules.loadModule(contentToLoad.moduleName, contentToLoad.moduleObj, true);
				}
            }.bind(this)
        });
	},
	
	loadDefaultPortal: function loadDefaultPortal()
	{
        this.tabs.each(function(item)
        {
			if(item.value.isDefault == true)
			{
				if (this.currentTab != '') 
				{
					$(this.currentTab).removeClassName('active');
				}
				
				this.currentTab = 'tab_' + item.value.name;
				
				if(!$(this.currentTab))
				{
					this.buildTabs();
				}
				
				$(this.currentTab).addClassName('active');
				
				this.switchingTabs = true;
				
				Portal.API.modules.loadModule(item.value.moduleName, item.value.moduleObj, true);
				
				item.value.isDefault = false;
			}
        }.bind(this));
		
		// set my aptana back to default
		var tab = this.tabs.get('my_aptana');
		tab.isDefault = true;
	},
	
	loadPortalContent: function loadPortalContent(sourceFile, portalObject)
	{
		var currentTab = Portal.API.tabs.currentTab;
		
		new Effect.Fade('content_' + currentTab, 
		{
			duration: 0.25,
			afterFinish: function()
			{
                new Ajax.Updater('content_' + currentTab, sourceFile, 
                {
                    evalScripts: true,
                    onComplete: function()
                    {
                        new Effect.Appear('content_' + currentTab,
                        {
                            duration: 0.25,
                            afterFinish: function()
                            {
								Portal.API.tabs.switchingTabs = false;
                                portalObject.loadComplete.call(portalObject);
                            }
                        });
                    }
                });		
			}
		});
	}
});

Object.Event.extend(PortalTabsObject);
