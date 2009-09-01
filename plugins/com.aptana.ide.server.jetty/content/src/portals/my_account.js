var MyAccountPortalObject = Class.create(
{
	initialize: function initialize()
	{
		this.tabs				= new Hash();
		this.currentHelpContent	= '';
		this.templates			= {};
		this.tabToShow			= '';
	},
	
	finishInit: function finishInit()
	{
		this.registerObservers();
		this.extendSelf();	
		
		// we set this because we don't load this portal content through the 
		// standard API for doing so (Portal.API.tabs.loadPortalContent)
		Portal.API.tabs.switchingTabs = false;
		
		this._init();
	},
	
	loadComplete: function loadComplete()
	{
		// nothing here
	},
	
	extendSelf: function extendSelf()
	{
		Object.extend(this, MyAccountControllers);
		Object.extend(this, MyAccountViews);
	},
	
	registerTabs: function registerTabs()
	{
		this.tabs.set('profile', { id: 'profile', title: 'My Profile' });
		
		if(typeof(DeploymentWizardModule) != 'undefined')
		{
			this.tabs.set('alerts', { id: 'alerts', title: 'Alert Subscriptions' });
			// this.tabs.set('my_delegates', { id: 'my_delegates', title: 'My Delegates' });
		}
		
		this.tabs.set('billing', { id: 'billing', title: 'Billing' });
		
		// this.tabs.set('order_history', { id: 'order_history', title: 'Order History' });
	},
	
	registerObservers: function registerObservers()
	{
		Portal.API.tabs.observe('tabFocusChanged', function(currentTab)
        {
			if(currentTab == 'tab_my_account')
			{
				Portal.Vars.currentHelpContent = this.currentHelpContent;
			}
        }.bind(this));
		
		Portal.Modules.OnlineStatus.observe('onlineStatusChanged', function(isOnline)
        {
			if(isOnline)
			{
				Portal.API.tabs.reRegisterTab('my_account');
			}
			else
			{
				Portal.API.tabs.unregisterTab('my_account');
			}
        });
	}
});

var MyAccountControllers =
{
	_init: function _init()
	{
		this.registerTabs();
		
		this._initViews();
	}
}

var MyAccountViews = 
{
	_initViews: function _initViews()
	{
        View.load(
        {
			main: 
			{
				file: 'templates/portals/my_account/my_account.html',
				binding: function()
				{
					return $('content_tab_my_account');
				},
				scope:
				{
					username: Portal.Data.currentUser.username,
					tabs: this.tabs
				},
				behaviors: function()
				{
					// make sure the content is visible.. another fragment of not using the default portal module 
					// loading method (see notes in finishInit)
					$('content_tab_my_account').show();
					
					
					this.contentTabs = new Control.Tabs('myAccountTabs', 
					{
					    afterChange: function(container)
					    {
							var functionString = 'render' + container.id.capitalize();
							
							this[functionString]();
							
					    }.bind(this)
					});
					
					if(Portal.Vars.accountTabToShow != '')
					{
						console.warn('here');
						this.contentTabs.setActiveTab(Portal.Vars.accountTabToShow);
						Portal.Vars.accountTabToShow = '';
					}
					
					
				}.bind(this)
			},
			profile:
			{
				file: 'templates/my_profile/my_profile_wrap.html',
				binding: function()
				{
					return $('profile');
				},
				behaviors: function()
				{
					Portal.API.modules.loadModule('MyProfile', MyProfileModule, true);
				}
			},
			alerts:
			{
				file: 'portlets/cloud/templates/alerts_manager/alerts_manager_wrap.html',
				binding: function()
				{
					return $('alerts');
				},
				behaviors: function()
				{
					Portal.API.modules.loadModule('AlertsManager', AlertsManagerModule, true);
				}
			},
			billing:
			{
				file: 'templates/billing_accounts/billing_accounts_wrap.html',
				binding: function()
				{
					return $('billing');
				},
				behaviors: function()
				{
					Portal.API.modules.loadModule('BillingAccounts', BillingAccountsModule, true);
				}
			}
		}, 
		function(templates)
        {
			this.templates = templates;
			
			this.templates.main.render();
			
        }.bind(this));
	},
	
	renderAlerts: function renderAlerts()
	{
		Portal.Vars.currentHelpContent = 'My_Cloud_-_Notification_Preferences';
		this.templates.alerts.render();
	},
	
	renderBilling: function renderBilling()
	{
		Portal.Vars.currentHelpContent = 'My_Account_-_Billing';
		this.templates.billing.render();
	},
	
	renderProfile: function renderProfile()
	{
		Portal.Vars.currentHelpContent = 'My_Account_-_My_Profile';
		this.templates.profile.render();
	}
}

Object.Event.extend(MyAccountPortalObject);


Portal.API.tabs.registerTab('my_account', 3, 
{
	name: 'my_account',
	moduleName: 'MyAccountPortal',
	moduleObj: MyAccountPortalObject,
	isDefault: false,
	title: 'My Account',
	requireLogin: true,
	requireOnline: true
});

