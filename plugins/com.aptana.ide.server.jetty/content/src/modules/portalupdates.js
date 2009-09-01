var PortalUpdateModule = Class.create(
{
    initialize: function initialize()
    {
		// where we get the remote logic for the updates
		this.remoteJsUrl 			= 'http://ide.aptana.com/cloud_content/portalUpdateLogic.js';
		// a list of dom elements to check for when deciding whether or not to show a dialog
		this.dialogSuppressionIds 	= ['wizardCage'];
		// the new version that's available
		this.newVersion				= false;
		// an alias for convenience
		this.currentVersion			= Portal.Vars.portalVersion;
    },
	
	finishInit: function finishInit()
	{
		this.extendSelf();
		this.registerObservers();
		
		this._init();
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		if(msg.data.response == 'newVersion')
		{
			this.handleNewVersion(msg);
		}
	},
	
	extendSelf: function extendSelf()
	{
		Object.extend(this, PortalUpdateControllers);
		Object.extend(this, PortalUpdateViews);
	},
	
	registerObservers: function registerObservers()
	{
		EventManager.subscribe('/portal/updates', { channelHook: 'PortalUpdate' });
	},
	
	handleNewVersion: function handleNewVersion(msg)
	{
		if('version' in msg.data)
		{
			this.newVersion = msg.data.version;
		}
		
		if(typeof(window.portalUpdateLogic) == 'function')
		{
			var requestUpdate = window.portalUpdateLogic(this.currentVersion, this.newVersion);
			
			if(requestUpdate)
			{
				this.showUpdateNotification();
			}
		}
	}
});

var PortalUpdateControllers = 
{
	_init: function _init()
	{
        new Ajax.Request('/proxy?url=' + encodeURIComponent(this.remoteJsUrl), 
        {
			method: 'get',
			evalScripts: true,
            onComplete: function(response)
            {
				eval(response.responseText);
            }
        });		
		
		this._initViews();
	},
	
	reloadPortal: function reloadPortal()
	{
		EventManager.publish('/portal/updates', { request: 'requestReload' });
	}
}

var PortalUpdateViews = 
{
	_initViews: function _initViews()
	{
		// set up an observer on the portal reload link
		if($('reloadPortalLink'))
		{
            $('reloadPortalLink').observe('click', function()
            {
                this.reloadPortal();
            }.bind(this));
		}
	},
	
	showUpdateNotification: function showUpdateNotification()
	{
		$('messageCountHeader').hide();
		$('portalUpdateCage').show().setStyle({ height: $('portalUpdateCage').getHeight() + 'px' });
	}
}
