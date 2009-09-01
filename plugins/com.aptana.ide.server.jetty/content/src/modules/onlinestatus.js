var OnlineStatusModule = Class.create(
{
	initialize: function initialize()
	{
		this.isOnline		= false;
		this.currentStatus	= '';
		this.oldStatus		= '';
	},
	
	finishInit: function finishInit()
	{
		this.registerObservers();
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		if(msg.data.response == 'addURL')
		{
			this.addUrls(msg);
		}
		else if(msg.data.response == 'checkOnlineStatus' || msg.data.response == 'onlineStateChange')
		{
			this.handleStatusUpdate(msg);
		}
	},
	
	registerObservers: function registerObservers()
	{
        EventManager.subscribe('/portal/network/online', 
        {
            channelHook: 'OnlineStatus',
            onComplete: function()
            {
				this.addUrls();
				
            }.bind(this)
        });
	},
	
	addUrls: function addUrls(msg)
	{
		if(!msg)
		{
			EventManager.publish('/portal/network/online', { request: 'addURL', url: Portal.Vars.siteManagerUrl.replace('https:', 'http:') + 'ok.jsp' });
		}
		else
		{
			this.checkOnlineStatus();
		}
	},
	
	checkOnlineStatus: function checkOnlineStatus()
	{
		EventManager.publish('/portal/network/online', { request: 'checkOnlineStatus' });
	},
	
	setOnlineStatus: function setOnlineStatus(status)
	{
		EventManager.publish('/portal/network/online', { status: status, old_status: (status.toLowerCase() == 'online') ? 'OFFLINE' : 'ONLINE', response: 'onlineStateChange' });
	},
	
	handleStatusUpdate: function handleStatusUpdate(msg)
	{
		if(msg.data.status.toLowerCase() == 'online' || msg.data.status.toLowerCase() == 'unknown')
		{
			this.isOnline = true;
		}
		else
		{
			this.isOnline = false;
		}
		
		this.status = msg.data.status;
		
		if('old_status' in msg.data)
		{
			this.oldStatus = msg.data.old_status;
		}
		
		$('offlineModeCage').style.display = (this.isOnline) ? 'none' : '';
		
		this.notify('onlineStatusChanged', this.isOnline);
		
		if(this.oldStatus == 'ONLINE' && !this.isOnline)
		{
			Control.Window.windows.each(function(w)
	        {
	            if (w instanceof Control.Modal) 
	            {
	                w.destroy();
	            }
	            
	        });
			
			if (typeof(Portal.Modules.DeploymentWizard) != 'undefined' && Portal.Modules.DeploymentWizard.deploymentInProgress) 
			{
				Portal.API.dialogs.alert('The Aptana Cloud Manager service is unreachable.  Your deployment is still in progress, and you will be able to view its status when it becomes reachable again.', 'Aptana Cloud Manager Unreachable');
				Portal.Modules.DeploymentWizard.deploymentInProgress = false;
			}
			else
			{
				// Portal.API.dialogs.alert('The Aptana Cloud Manager service is unreachable.  Some functionality will not be available until it is reachable.', 'Aptana Cloud Manager Unreachable');
			}
		}
		else if (Portal.Vars.preloadComplete)
		{
			try
			{
				Portal.API.dialogs.closeAlert();
			}
			catch (e)
			{
				// nothing
			}
		}
		
		if(Portal.Vars.preloadComplete == false)
		{
			this.notify('startupHookComplete');
		}
	}
});

Object.Event.extend(OnlineStatusModule);

Portal.API.startup.registerStartupItem(0, 
{
	moduleName: 'OnlineStatus',
	moduleObject: OnlineStatusModule,
	modulePersist: true,
	startupMessage: 'Checking online status...'
});
