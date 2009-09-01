/**
 * @author Ian Selby <iselby@aptana.com>
 * @copyright 2008 Aptana, Inc.
 * 
 */

var CurrentPortalVersion;

var PortalObject = Class.create(
{
	initialize: function inititalize()
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
			siteManagerAvailable:	true,
			siteManagerPolling:		false,
			preloadComplete:		false,
			currentHelpContent:		'Cloud',
			siteToLoad:				0,
			switchingContent:		false,
			accountTabToShow:		'',
			deployLocation:			'https://deploy.aptana.com/portal',
			stagingDeployLocation:	'https://staging-deploy.aptana.com/portal',
			javaAndJaxerDisablementDate: new Date(2009, 7, 19, 11, 30, 49) // Always sync to  deployment wizard - cloud_user.rb
		});
		
		// build our API
		this.API.startup	= new PortalStartupRegistry();
		this.API.modules	= new PortalModuleRegistry();
		this.API.models		= new PortalModelRegistry();
		this.API.logging	= new PortalLogging();
		this.API.utils		= new PortalUtilities();
		this.API.dialogs	= new PortalDialogs();
		this.API.templates 	= new PortalTemplateLoader();
		// this is in _portaltabs.js
		this.API.tabs		= new PortalTabsObject();
		
		// disable rails support by default
		this.Vars.railsEnabled = false;
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
		
		this.API.startup.buildStartupHtml();

		EventManager.observeOnce('cometInitComplete', function() 
		{
			this.API.startup.startPortal();
		}.bind(this));
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
			portalVersion:	'',
			newSite:		false,
			studioId:		'',
			macAddress:		'',
			referrer:       '',
			jettyPort:		'',
			showProjectChooser: false
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
					uriData.newDefaultTab = part[1].replace('+', ' ');
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
				case 'newSite':
					uriData.newSite = true;
					break;
				case 'studioid':
					uriData.studioId = unescape(part[1]);
					break;
				case 'macaddress':
					uriData.macAddress = unescape(part[1]);
					break;
				case 'referrer':
					uriData.referrer = unescape(part[1]);
					break;
				case 'chooseProject':
					uriData.showProjectChooser = true;
					break;
			}
			
			// get the jetty port
			var location = window.location.href.replace('http://', '');
			uriData.jettyPort = location.substr(location.indexOf(':') + 1, 4);
			
        });
		
		// if we've got a project AND a siteId, we don't want to show the wizard again
		// it's possible the project was just sent back to studio to be bound.
		if(uriData.siteToDeploy !== false && uriData.siteToLoad !== false)
		{
			uriData.siteToDeploy = false;
		}
		
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
/* Portal template loader    													*/
/* ---------------------------------------------------------------------------- */
var PortalTemplateLoader = Class.create(
{
	load: function load(templates, proceed)
	{
	    var total_number_of_templates = templates.keys().length;
	    var number_of_templates_loaded = 0;
		
        templates.each(function(template)
        {
			new Ajax.Request(template.value, 
	        {
	            onComplete: function(template_name, request)
	            {
					templates.set(template_name, new Template(request.responseText));
					
	                ++number_of_templates_loaded;
					
	                if (number_of_templates_loaded == total_number_of_templates) 
					{
						proceed(templates);
					}
					
	            }.curry(template.key)
	        });
        });
	},
	
	parseErrors: function parseErrors(prefix, errors)
	{
		if(errors.length == 0)
		{
			return prefix;
		}
		
		return prefix + '<br /><span class="size11">&bull; ' + errors.join('</span><br /><span class="size11">&bull; ') + '</span>';
	},
	
	fetchRemoteContent: function fetchRemoteContent(content, container)
	{
		var contentUrl = 'http://content.aptana.com/aptana/my_cloud/?content=' + content + '&sv=' + Portal.Vars.portalVersion;

		if(!$(container))
		{
			return;
		}
		
		if(!Portal.Modules.OnlineStatus.isOnline)
		{
			Portal.API.logging.logEvent('CACHED CONTENT', 'Displaying Cached Content For: ' + contentUrl);
		}
		else
		{
			Portal.API.logging.logEvent('REMOTE CONTENT', contentUrl);			
		}
		
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
				
				setTimeout(fixDisplay, 0);
            }
        });
	}
});


/* ---------------------------------------------------------------------------- */
/* Portal startup registry    													*/
/* ---------------------------------------------------------------------------- */
var PortalStartupRegistry = Class.create(
{
	initialize: function()
	{
		this.vars = 
		{
			totalStartupSteps:	0,
			currentStartupStep:	-1,
			startupElement:		'load_progress'
		}
		
		this.data = 
		{
			startupItems:	new Hash()
		}
	},
	
	registerStartupItem: function registerStartupItem(step, options)
	{
		var startupData = Object.extend(
		{
			moduleName:		'',
			moduleObject:	'',
			modulePersist:	false,
			onInit:			Prototype.emptyFunction,
			startupMessage:	''
		}, options || {});
		
		if(!this.data.startupItems.get(step))
		{
			this.data.startupItems.set(step, startupData);
		}
		else
		{
			while(this.data.startupItems.get(step))
			{
				step ++;
			}
			
			this.data.startupItems.set(step, startupData);
		}
				
		this.vars.totalStartupSteps = (step > this.vars.totalStartupSteps) ? step : this.vars.totalStartupSteps;
	},
	
	startPortal: function()
	{
		if(Portal.Vars.preloadComplete == true)
		{
			return;
		}
		
		if(this.vars.currentStartupStep >= this.vars.totalStartupSteps)
		{
			Portal.Vars.preloadComplete = true;
			
			// keep these things from borking startup, they're not crucial functionality at the moment anyway			
			try
			{
				if (typeof(CurrentPortalVersion) != 'undefined') 
				{
					Portal.API.utils.sendTrackingData('ma.v', CurrentPortalVersion);
				}
				
				Portal.API.models.loadModel('currentUser', (typeof(CloudUserClass) != 'undefined') ? CloudUserClass : UserClass);
				if(typeof(CloudTrialStatusModule) != 'undefined')
				{
					Portal.API.modules.loadModule('CloudTrialStatus', CloudTrialStatusModule, false);
				}
				if (typeof(PortalUpdateModule) != 'undefined') 
				{
					Portal.API.modules.loadModule('PortalUpdate', PortalUpdateModule, false);
				}
				if (typeof(MessageCenterModule) != 'undefined') 
				{
					Portal.API.modules.loadModule('MessageCenter', MessageCenterModule, false);
				}
			}
			catch (e)
			{
				console.warn(e)
			}
						
			this.startupModal.close();
			this.startupModal.destroy();
					
			// load the override content
            new Ajax.Request('/proxy?url=' + encodeURIComponent('http://content.aptana.com/aptana/my_cloud/?content=override&sv=' + Portal.Vars.portalVersion), {
                method: 'get',
				onComplete: function(response)
				{
					try 
					{
						eval(response.responseText);
					}
					catch (e)
					{
						console.warn('Error with override code: ' + e);
					}
						
					Portal.API.tabs.finishInit.call(Portal.API.tabs, Portal.Vars.newDefaultTab);
					
					if (Portal.Vars.siteToDeploy != false) {
						EventManager.publish.delay(1, '/portal/cloud/startDeployment', {
							action: 'startDeployment',
							projectName: Portal.Vars.siteToDeploy
						});
					}
					
					if(Portal.Vars.showProjectChooser != false)
					{
						var showDialog = function(){
							Portal.Modules.DeploymentWizard.showProjectChooserDialog();
						}
						
						setTimeout(showDialog, 1000);
					}
					
					if (Portal.Vars.showLogin == true) {
						var showLogin = function(){
							Portal.Modules.AptanaIdManager.showLoginWindow();
						}
						
						showLogin.delay(1);
					}
				},
				onFailure: function()
				{
					Portal.API.tabs.finishInit.call(Portal.API.tabs, Portal.Vars.newDefaultTab);
					
					if (Portal.Vars.siteToDeploy != false) {
						EventManager.publish.delay(1, '/portal/cloud/startDeployment', {
							action: 'startDeployment',
							projectName: Portal.Vars.siteToDeploy
						});
					}
					
					if (Portal.Vars.showLogin == true) {
						var showLogin = function(){
							Portal.Modules.AptanaIdManager.showLoginWindow();
						}
						
						showLogin.delay(1);
					}
				}
            });
						
			
			return;
		}
		
		if($('startup_' + this.vars.currentStartupStep))
		{
			$('startup_' + this.vars.currentStartupStep).removeClassName('active');
			$('startup_' + this.vars.currentStartupStep).addClassName('complete');
		}
		
		this.vars.currentStartupStep ++;
		
		if($('startup_' + this.vars.currentStartupStep))
		{
			$('startup_' + this.vars.currentStartupStep).removeClassName('pending');
			$('startup_' + this.vars.currentStartupStep).addClassName('active');
		}
		
		var currentStartupItem = this.data.startupItems.get(this.vars.currentStartupStep);
		
		if(!currentStartupItem)
		{
			this.startPortal();
			return;
		}
		
		if(currentStartupItem.moduleName != '' && currentStartupItem.moduleObject.moduleObject != '')
		{
			Portal.API.modules.loadModule(currentStartupItem.moduleName, currentStartupItem.moduleObject, currentStartupItem.modulePersist, true);
		}
		
		currentStartupItem.onInit();	
	},
	
	buildStartupHtml: function buildStartupHtml()
	{
		var startupHtml = '<div id="load_progress" class="line16 clean-yellow"><div><strong>Starting My Cloud...</strong></div>';
		
        var startupIndexes = this.data.startupItems.keys().sort(function(x, y)
        {
			var a = parseInt(x);
			var b = parseInt(y);
			
			if( a > b)
			{
				return 1;
			}
			
			if(a < b)
			{
				return -1;
			}
			
			return 0;
        });
		
        startupIndexes.each(function(index)
        {
			var item = this.data.startupItems.get(index);
			
			if(!item.startupMessage)
			{
				return;
			}
			
			startupHtml += '<div id="startup_' + index + '" class="pending">' + item.startupMessage + '</div>';
			
        }.bind(this));
		
		startupHtml += '<div class="top5 size10">Please wait, this can take a few moments...</div>';
		startupHtml += '</div>';
		startupHtml += '<div class="top-left-edge"></div><div class="top-edge"></div><div class="top-right-edge"></div><div class="right-edge"></div><div class="bottom-right-edge"></div><div class="bottom-edge"></div><div class="bottom-left-edge"></div><div class="left-edge"></div>';
		
        this.startupModal = new Control.Modal(startupHtml, 
        {
			closeOnClick: false,
			fade: false,
			className: 'modal',
			width: Portal.API.dialogs.vars.dialogWidth,
			overlayOpacity: 0.5,
			fadeDuration: 0.5,
			afterOpen: function() 
			{
				EventManager.initComet();
			}
		});
		
		this.startupModal.open();
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

/* ---------------------------------------------------------------------------- */
/* Portal dialogs		    													*/
/* ---------------------------------------------------------------------------- */
var PortalDialogs = Class.create(
{
	initialize: function inititalize()
	{
		this.alertDialog	= false;
		this.confirmDialog	= false;
		
		this.vars = 
		{
			dialogPath:		'templates/dialogs/',
			dialogWidth:	350
		}
	},
	
	alert: function alert(message, title)
	{
		if(this.confirmDialog != false)
		{
			return;
		}

		if(this.alertDialog != false)
		{
			if (!$('alertDialog')) 
			{
				this.alertDialog = false;
			}
			else 
			{
				this.closeAlert();
			}
		}
		
		if(!title)
		{
			var title = 'Warning';
		}
		
		// fetch the content...
        new Ajax.Request(this.vars.dialogPath + 'alert.html', 
        {
			onComplete: function(response)
			{
				var alertTemplate = new Template(response.responseText);
				var content = alertTemplate.evaluate({ message: message, title: title });
				
                this.alertDialog = new Control.Modal(content, 
                {
					closeOnClick: 'overlay',
					className: 'modal',
					width: Portal.API.dialogs.vars.dialogWidth,
					overlayOpacity: 0.5,
					fade: false,
					height: null,
					fadeDuration: 0.25,
					iframeshim: false,
					afterOpen: function()
					{
                        Portal.API.dialogs.alertHotKeyReturn = new HotKey('RETURN', function(event)
                        {
                            Portal.API.dialogs.closeAlert();
                        },
                        {
                            ctrlKey: false
                        });
						Portal.API.dialogs.alertHotKeyEsc = new HotKey('ESC', function(event)
                        {
                            Portal.API.dialogs.closeAlert();
                        },
                        {
                            ctrlKey: false
                        });
					}
				});
				
				this.alertDialog.open();
			}.bind(this)
		});
	},
	
	closeAlert: function closeAlert()
	{
		try
		{
			if(this.alertDialog)
			{
				this.alertHotKeyReturn.destroy();
				this.alertHotKeyEsc.destroy();
				
				this.alertDialog.close();
				this.alertDialog.destroy();
				this.alertDialog = false;
			}
		}
		catch (e)
		{
			console.warn('ERROR CLOSING ALERT: ' + e);
		}
	},
	
	confirm: function confirm(data)
	{
		if(this.confirmDialog != false)
		{
			return;
		}

		if(this.alertDialog != false)
		{
			if (!$('alertDialog')) 
			{
				this.alertDialog = false;
			}
			else 
			{
				this.closeAlert();
			}
		}
		
		if(!data.title)
		{
			data.title = 'Please confirm...';
		}
		
		if(typeof(data.onConfirm) != 'function')
		{
			return;
		}
		
		if(typeof(data.beforeClose) == 'function')
		{
			this.beforeCloseFunction = data.beforeClose;
		}
		else
		{
			this.beforeCloseFunction = false;
		}
		
		this.onConfirmFunction = data.onConfirm;
		
        new Ajax.Request(this.vars.dialogPath + 'confirm.html', 
        {
			onComplete: function(response)
			{
				var confirmTemplate = new Template(response.responseText);
                var content = confirmTemplate.evaluate(
                {
                    message: data.message,
                    title: data.title
                });
				
				this.confirmDialog = new Control.Modal(content, 
                {
					closeOnClick: false,
					className: 'modal',
					width: ('width' in data) ? data.width : Portal.API.dialogs.vars.dialogWidth,
					overlayOpacity: 0.5,
					fade: false,
					height: null,
					fadeDuration: 0.25,
					iframeshim: false,
					afterOpen: function()
					{
                        Portal.API.dialogs.confirmHotKeyReturn = new HotKey('RETURN', function(event)
                        {
                            Portal.API.dialogs.doOnConfirm();
                        },
                        {
                            ctrlKey: false
                        });
						Portal.API.dialogs.confirmHotKeyEsc = new HotKey('ESC', function(event)
                        {
                            Portal.API.dialogs.closeConfirm();
                        },
                        {
                            ctrlKey: false
                        });
					},
					afterClose: function()
					{
						Portal.API.dialogs.notify('confirmClosed');
					}
					
				});
				
				this.confirmDialog.open();
			}.bind(this)
		});
		
	},
	
	doOnConfirm: function doOnConfirm()
	{
		if(Portal.API.dialogs.beforeCloseFunction !== false)
		{
			if(Portal.API.dialogs.beforeCloseFunction())
			{
				Portal.API.dialogs.onConfirmFunction();
				Portal.API.dialogs.closeConfirm();
			}
		}
		else
		{
			// this one may or may not have "this" properly scoped so...
			Portal.API.dialogs.onConfirmFunction();
			Portal.API.dialogs.closeConfirm();
		}
	},
	
	closeConfirm: function closeConfirm()
	{
		try
		{
			this.onConfirmFunction = null;
			this.beforeCloseFunction = false;
			
			this.confirmHotKeyReturn.destroy();
			this.confirmHotKeyEsc.destroy();
			
			this.confirmDialog.close();
			this.confirmDialog.destroy();
			this.confirmDialog = false;
		}
		catch (e)
		{
			console.warn('ERROR CLOSING CONFIRM: ' + e);
		}
	},
	
	initModalForm: function initModalForm()
	{
		var ua = navigator.userAgent.toLowerCase();
		
		if(ua.indexOf('firefox') > -1 || ua.indexOf('gecko') > -1)
		{
			return true;
		}
		
		return false;
	}
});
Object.Event.extend(PortalDialogs);

/* ---------------------------------------------------------------------------- */
/* Portal utilities		    													*/
/* ---------------------------------------------------------------------------- */
var PortalUtilities = Class.create(
{
	setRailsSupport: function setRailsSupport(enabled)
	{
		var railsEnabled = (!enabled) ? false : true;
		
		Portal.Vars.railsEnabled = railsEnabled;
		
		if(!railsEnabled)
		{
			Portal.Data.cloudProjects.notCloudCompatible.push('rails');
		}
		else
		{
			Portal.Data.cloudProjects.notCloudCompatible = Portal.Data.cloudProjects.notCloudCompatible.without('rails');
		}
		
		// make sure its unique
		Portal.Data.cloudProjects.notCloudCompatible = Portal.Data.cloudProjects.notCloudCompatible.uniq();
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

function doRedirect(newLocation)
{
	Portal.API.logging.logEvent('REDIRECT', newLocation);
	window.location = newLocation;
}
