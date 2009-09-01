var PortalObject = Class.create({
	initialize: function initialize()
	{
		// set up our namespace
		this.Vars 		= {};
		this.Channels	= {};
		this.API		= {};
		this.Modules	= {};
		this.Data		= {};
		this.Pollers	= {};
		
		// set up our Vars namespace
        Object.extend(this.Vars, 
        {
			debugMode:				false,
			debugMessages:			false,
			devMode:				false,
			pluginDetailsClicked:	false
		});
		
		// build our API
		this.API.modules	= new PortalModuleRegistry();
		this.API.models		= new PortalModelRegistry();
		this.API.logging	= new PortalLogging();
		this.API.utils		= new PortalUtilities();
	},
	_init: function _init()
	{
		this.API.logging._init();

		this._parseUri();

		// log out the current portal version, as long as it's not blank
		if (Portal.Vars.portalVersion != '') 
		{
			console.warn('STARTING PORTAL VERSION: ' + Portal.Vars.portalVersion);
		}
		
		EventManager.observeOnce('cometInitComplete', function() 
		{
			Portal.API.models.loadModel('plugins', PluginsClass)
			Portal.API.modules.loadModule('MyAptana', MyAptanaModule, false);
		}.bind(this));
		
		EventManager.initComet();
	},
	
	_parseUri: function parseUri()
	{
		var uriData = 
		{
			cometPort:		'8600',
			newDefaultTab:	false,
			siteToLoad:		false,
			siteToDeploy:	false,
			productToShow:	false,
			showLogin:		false,
			siteManagerUrl:	'',
			firstTime:		false,
			portalVersion:	''
		};
		
		var uri = document.location.href.substr(document.location.href.indexOf('?') + 1);
		var uriParts = uri.split('&');
		
        uriParts.each(function(part)
        {
			part = part.gsub('#', '').split('=');
			
			switch(part[0])
			{
				case 'port':
					uriData.cometPort = part[1];
					break;
				case 'tab':
					uriData.newDefaultTab = part[1];
					break;
				case 'siteId':
					uriData.siteToLoad = part[1];
					break;
				case 'project':
					uriData.siteToDeploy = unescape(part[1]);
					break;
				case 'product':
					uriData.productToShow = unescape(part[1]);
					break;
				case 'sm':
					uriData.siteManagerUrl = unescape(part[1]) + '/';
					break;
				case 'login':
					uriData.showLogin = true;
					break;
				case 'firstTime':
					uriData.firstTime = true;
					break;
				case 'pv':
					uriData.portalVersion = unescape(part[1]);
					CurrentPortalVersion = uriData.portalVersion;
					break;
			}
        });
		
		if(uriData.firstTime)
		{
			uriData.productToShow = 'cloud';
		}
		
		if(uriData.siteManagerUrl.indexOf('staging') == -1)
		{
			uriData.passwordLink = 'http://id.aptana.com/reset_password';
		}
		else
		{
			uriData.passwordLink = 'http://staging.id.aptana.com/reset_password';
		}
		
		Object.extend(this.Vars, uriData);
	},
		
	_log: function(data)
	{
		this.API.logging.log.call(this.API.logging, data);
	}
});

/* ---------------------------------------------------------------------------- */
/* Portal logging		    													*/
/* ---------------------------------------------------------------------------- */
var PortalLogging = Class.create(
{
	initialize: function initialize()
	{
		this.logBuffer = [];
	},
	
	_init: function _init()
	{
		this.toggleLogging = new HotKey('L', function(event)
        {
            if(Portal.Vars.debugMode)
			{
				this.stopLogging();
			}
			else
			{
				this.startLogging(false);
			}
        }.bind(this),
		{
			shiftKey: true
		});
		this.toggleLoggingMessages = new HotKey('M', function(event)
        {
            if(Portal.Vars.debugMode)
			{
				this.stopLogging();
			}
			else
			{
				this.startLogging(true);
			}
        }.bind(this),
		{
			shiftKey: true
		});
	},
	
	log: function log(data)
	{
		var extraInfo = '';
		
		if(typeof console == 'undefined' || Portal.Vars.debugMode == false)
		{
			if(this.logBuffer.size() > 100)
			{
				console.warn('flushing log buffer')
				this.logBuffer.clear();
			}
			
			this.logBuffer.push(data);
			
			
			return;
		}
		console.group(data.eventType + ': ' + data.controller);
		
		if (data.eventType == 'ERROR' || data.eventType == 'CHANNEL UNSUB') 
		{
			console.warn(data.msg);
		}
		else
		{
			console.info(data.msg);
		}
		
		if(data.extra && data.extra.length > 0)
		{
			console.group('Additional Info');
			console.log(data.extra.join('\n'));
			console.groupEnd();
		}
		
		console.groupEnd();
	},
	
	logEvent: function logEvent(event, msg)
	{
		if(typeof console != 'undefined' && Portal.Vars.debugMode != false)
		{
			console.info(event + ': ' + msg);
		}
		else
		{
			var data = 
			{
				eventType: event,
				controller: '[EVENT BUFFER]',
				msg: msg
			}
			
			if(this.logBuffer.size() > 100)
			{
				console.warn('flushing log buffer')
				this.logBuffer.clear();
			}
			
			this.logBuffer.push(data);
		}
	},
	
	startLogging: function startLogging(debugMessages)
	{
		window.console.open();
		
		console.warn('LOGGING STARTED');
		
		Portal.Vars.debugMode = true;
		Portal.Vars.debugMessages = debugMessages;
		
		this.logBuffer.each(function(item)
		{
			this.log(item);
		}.bind(this));
		
		this.logBuffer.clear();
	},
	
	stopLogging: function stopLogging()
	{
		console.warn('LOGGING STOPPED');
		
		Portal.Vars.debugMode = false;
		Portal.Vars.debugMessages = false;
	},
	
	toggleLogging: function toggleLogging(debugMessages)
	{
		alert('here');
		
		if(Portal.Vars.debugMode)
		{
			this.stopLogging();
		}
		else
		{
			this.startLogging(debugMessages)
		}
	}
});

/* ---------------------------------------------------------------------------- */
/* Portal utilities		    													*/
/* ---------------------------------------------------------------------------- */
var PortalUtilities = Class.create(
{
	newHostedProject: function newHostedProject()
	{
		// Show the Web Deployment wizard
		EventManager.publish('/portal/studio/my_cloud', { request: 'open' });
	},
	
	setContent: function setContent(element, content)
	{
		if ($(element)) 
		{
			$(element).update(content);
		}
	},
	
	setPref: function setPref(key, value)
	{
        EventManager.publish('/portal/preferences', 
        {
            request: 'set',
            name: key,
            value: value
        });
	},
	
	getPref: function getPref(key)
	{
        EventManager.publish('/portal/preferences', 
        {
			request: 'get',
			name: key
		});
	},
	
	openUrl: function openUrl(url)
	{
		var cleanUrl = url.replace(/(private\-|staging\-)?[\w\-]+?(\.aptanacloud\.com)/i, "$1???$2");
		
		this.sendTrackingData('ma.vurl', cleanUrl);
		EventManager.publish('/portal/browser', { url: url });
	},
	
	showHelp: function showHelp(helpContent)
	{
		if(!helpContent)
		{
			var helpContent = Portal.Vars.currentHelpContent;
		}
		
		EventManager.publish('/portal/help', { id: helpContent });
	},
	
	openView: function openView(pluginViewId, subId)
	{
		if (!subId) 
		{
			EventManager.publish('/portal/views/show', 
			{
				id: pluginViewId
			});
		}
		else
		{
			EventManager.publish('/portal/views/show', 
			{
				id: pluginViewId,
				subid: subId
			});
		}
	},
	
	openPerspective: function openPerspective(perspectiveId, viewId, extra)
	{
		if (viewId) 
		{
            EventManager.publish('/portal/perspectives/show', 
            {
				id: perspectiveId,
				viewId: viewId,
				extra: (!extra) ? {} : extra
			});
		}
		else 
		{
			EventManager.publish('/portal/perspectives/show', 
			{
				id: perspectiveId,
				extra: (!extra) ? {} : extra
			});
		}
	},
	
	sendTrackingData: function sendTrackingData(id, msg)
	{
		if(!msg)
		{
			var msg = '';
		}
		
		Portal.API.logging.logEvent('TRACKING DATA', 'ID: ' + id);
		
		EventManager.publish('/portal/cloud/eventtracker', { request: 'submitEvent', id: id, data: msg });
	},
	
	fetchRemoteContent: function fetchRemoteContent(content, container)
	{
		var contentUrl = 'http://content.aptana.com/aptana/my_aptana/?content=' + content + '&sv=' + CurrentPortalVersion;

		if(!$(container))
		{
			Portal.API.logging.logEvent('REMOTE CONTENT', 'container not found');
			return;
		}
		
		Portal.API.logging.logEvent('REMOTE CONTENT', contentUrl);			
		
        new Ajax.Updater(container, '/proxy?url=' + encodeURIComponent(contentUrl), 
        {
            method: 'get',
			evalScripts: true,
            onComplete: function()
            {
				// make sure we don't get the stupid mac display bug...
				var fixDisplay = function()
				{
					if ($(container)) 
					{
						$(container).setStyle(
						{
							height: $(container).getHeight() + 'px'
						});
					}
				}
				
				setTimeout(fixDisplay, 50);
            }
        });
	}
});


/* ---------------------------------------------------------------------------- */
/* Portal module registry    													*/
/* ---------------------------------------------------------------------------- */
var PortalModuleRegistry = Class.create(
{
	initialize: function inititalize()
	{
		this.data	= 
		{
			loadedModules:	new Hash()
		}
	},
	
	loadModule: function loadModule(moduleName, moduleObject, persist, isStartupItem)
	{
		if(!isStartupItem)
		{
			var isStartupItem = false;
		}
		
		if(!persist)
		{
			var persist = false;	
		}
		
		if(typeof(moduleObject) == 'undefined')
		{
			alert('Can not find ' + moduleName);
			return false;
		}
		
		if(this._isLoadedModule(moduleName))
		{
			this._unloadModule(moduleName);
		}
		
		this.data.loadedModules.set(moduleName, { persist: persist });
		Portal.Modules[moduleName] = new moduleObject();
		
		if(isStartupItem)
		{
            Portal.Modules[moduleName].observeOnce('startupHookComplete', function()
            {
				Portal.API.startup.startPortal();
            });
		}
		
		if(typeof Portal.Modules[moduleName].finishInit == 'function')
		{
			Portal.Modules[moduleName].finishInit.call(Portal.Modules[moduleName]);
		}
		
		this.notify('moduleLoaded', moduleName);
		
		return true;
	},
	
	_isLoadedModule: function _isLoadedModule(moduleName)
	{
		if(this.data.loadedModules.get(moduleName) != undefined)
		{
			return true;
		}
		
		return false;
	},
	
	_unloadModule: function _unloadModule(moduleName)
	{
		// if(this.data.loadedModules.get(moduleName).persist == false)
		// {
			Portal.Modules[moduleName] = null;
		// }
		
		this.data.loadedModules.unset(moduleName);
	}
});
Object.Event.extend(PortalModuleRegistry);

/* ---------------------------------------------------------------------------- */
/* Portal model registry    													*/
/* ---------------------------------------------------------------------------- */
var PortalModelRegistry = Class.create(
{
	initialize: function inititalize()
	{
		this.data = 
		{
			loadedModels:	new Hash()
		}
	},
	
	loadModel: function loadModel(className, classObject, isStartupItem, params)
	{
		if(!isStartupItem)
		{
			var isStartupItem = false;
		}
		
		if(typeof(classObject) == 'undefined')
		{
			alert('Can not find class: ' + className);
			return false;
		}
		
		if(this._isLoadedModel())
		{
			return;
		}
		
		this.data.loadedModels.set(className, className);
		
		if (params) 
		{
			Portal.Data[className] = new classObject(params);
		}
		else 
		{
			Portal.Data[className] = new classObject();
		}
		
		if(isStartupItem == true)
		{
            Portal.Data[className].observeOnce('startupHookComplete', function()
            {
				Portal.API.startup.startPortal();
            });	
		}
		
		if(typeof Portal.Data[className].finishInit == 'function')
		{
			Portal.Data[className].finishInit.call(Portal.Data[className]);
		}
	},
	
	_isLoadedModel: function _isLoadedModel(className)
	{
		if(this.data.loadedModels.get(className))
		{
			return true;
		}
		
		return false;
	}
});