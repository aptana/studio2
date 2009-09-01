var UserClass = Class.create(
{
	initialize: function initialize()
	{
		// register the channels
		Portal.Channels.billing_accounts 	= '/portal/user/billing';
		Portal.Channels.userModel			= '/portal/user/model';

		this.initVars();
	},
	
	initVars: function initVars()
	{
		this.userId				= '';
		this.username			= '';
		this.password			= '';
		this.email				= '';
		this.firstName			= '';
		this.lastName			= '';
		this.phone				= '';
		this.createdAt			= new Date();
		this.isJavaAndJaxerEnabled = false;
        this.address = 
        {
            address1: '',
            address2: '',
            city: '',
            state: '',
            country: '',
            zipcode: '',
            phone: ''
        };
        this.companyInfo = 
        {
            company: '',
            role: '',
            organization_type: '',
            organization_size: '',
            sites_per_year: ''
        };
		this.interests			= {
			ajax: false,
			javascript: false,
			php: false,
			ruby: false,
			python: false,
			java: false,
			net: false,
			site_development: false,
			application_development: false,
			newsletter: false			
		}
		this.loggedIn			= false;
		this.loading			= true;
		this.billingErrors		= false;
		this.billingLoading		= true;
		this.billingAccounts	= new Hash();
		
		if(typeof(UserSubscriptionClass) != 'undefined')
		{
			this.subscriptions	= new Hash();
		}
	},
	
	finishInit: function finishInit()
	{
		EventManager.subscribe(Portal.Channels.user, 
        {
            channelHook: 'currentUser'
		});
		
		EventManager.subscribe(Portal.Channels.userModel,
		{
			channelHook: 'currentUser',
			onComplete: function()
			{
            	this.fetchUserData();
				   
			}.bind(this)
		});
		
        EventManager.subscribe(Portal.Channels.billing_accounts, 
        {
            channelHook: 'currentUser'
        });

		Portal.Modules.AptanaIdManager.observe('userLoggedIn', function() 
		{
			this.loading = true;
			this.fetchUserData();
			
		}.bind(this));
		
        Portal.Modules.OnlineStatus.observe('onlineStatusChanged', function(isOnline)
        {
			if(!isOnline)
			{
				return;
			}
			
			this.loading = true;
			this.fetchUserData();
        }.bind(this));
		
        Portal.Modules.AptanaIdManager.observe('userLoggedOut', function()
        {
			this.initVars();
        }.bind(this));
		
        this.observe('userModelLoaded', function()
        {
			this.fetchBillingAccounts();
        }.bind(this));
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		if(msg.data.response == 'currentUser')
		{
			this.update(msg.data);
		}
		else if (msg.channel == Portal.Channels.billing_accounts && msg.data.response == 'describe')
		{
			this.fetchBillingAccounts(msg);
		}
	},
	
	update: function update(data)
	{
		var newData = $H(data);
		
		newData.each(function(item)
        {
			if(item.value == null)
			{
				newData.unset(item.key);	
			}
        });
		
		data = newData.toObject();
		
		if('username' in data)
		{
			this.username = (data.username == false) ? '' : data.username;
			this.loggedIn = (data.username == false) ? false : true;
		}
		
		if('password' in data)
		{
			this.password = data.password;
		}
		
		if('email' in data) 
		{
			this.email = data.email;
		}
		
		if('id' in data)
		{
			this.userId = data.id;
		}
		
		if('phone' in data)
		{
			this.phone = data.phone;
		}
		
		if('address1' in data)
		{
			this.address.address1 = data.address1;
		}
		
		if('address2' in data)
		{
			this.address.address2 = data.address2;
		}
		
		if('city' in data)
		{
			this.address.city = data.city;
		}
		
		if('state' in data)
		{
			this.address.state = data.state;
		}
		
		if('zipcode' in data)
		{
			this.address.zipcode = data.zipcode;
		}
		
		if('country' in data)
		{
			this.address.country = data.country;
		}
		
		if('last_name' in data)
		{
			this.lastName = data.last_name;
		}
		
		if('first_name' in data)
		{
			this.firstName = data.first_name;
		}
		
		if('company' in data)
		{
			this.companyInfo.company = data.company;
		}
		
		if('role' in data)
		{
			this.companyInfo.role = data.role;
		}
		
		if('organization_type' in data)
		{
			this.companyInfo.organization_type = data.organization_type;
		}
		
		if('organization_size' in data)
		{
			this.companyInfo.organization_size = data.organization_size;
		}
		
		if('sites_per_year' in data)
		{
			this.companyInfo.sites_per_year = data.sites_per_year;
		}
		
		// Grab the created_at value and set a Date instance that will hold it. 
		// In case we get an error, the default date for now will be used.
		if('created_at' in data)
		{
			userCreationTime = data.created_at;
			if (userCreationTime && userCreationTime.length > 0) 
			{
				creationDateArray = userCreationTime.match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/)
				if (creationDateArray) 
				{
					this.createdAt = new Date(creationDateArray[1], creationDateArray[2] - 1, creationDateArray[3], creationDateArray[4], creationDateArray[5], creationDateArray[6]);
					this.isJavaAndJaxerEnabled = this.createdAt < Portal.Vars.javaAndJaxerDisablementDate;
				}
			}
		}
		
		// ... and now for all the interests...
		this.interests = 
		{
			ajax: false,
			javascript: false,
			php: false,
			ruby: false,
			python: false,
			java: false,
			net: false,
			site_development: false,
			application_development: false,
			newsletter: false			
		}
		
		if('ajax' in data)
		{
			this.interests.ajax = data.ajax;
		}
		
		if('javascript' in data)
		{
			this.interests.javascript = data.javascript;
		}
		
		if('php' in data)
		{
			this.interests.php = data.php;
		}
		
		if('ruby' in data)
		{
			this.interests.ruby = data.ruby;
		}
		
		if('python' in data)
		{
			this.interests.python = data.python;
		}
		
		if('java' in data)
		{
			this.interests.java = data.java;
		}
		
		if('net' in data)
		{
			this.interests.net = data.net;
		}
		
		if('site_development' in data)
		{
			this.interests.site_development = data.site_development;
		}
		
		if('application_development' in data)
		{
			this.interests.application_development = data.application_development;
		}
		
		if('newsletter' in data)
		{
			this.interests.newsletter = data.newsletter;
		}
		
		if(this.loading == true)
		{
			this.loading = false;
			this.notify('userModelLoaded');
		}
	},
	
	fetchUserData: function fetchUserData(profile)
	{
		if (Portal.Modules.AptanaIdManager.isLoggedIn) 
		{
			EventManager.publish(Portal.Channels.user, 
			{
				request: 'currentUser'
			});
			return;
		}
	},
	
	fetchBillingAccounts: function(msg)
	{
		if(!msg && Portal.Modules.AptanaIdManager.isLoggedIn)
		{
			this.billingLoading = true;
			
            EventManager.publish('/portal/cloud/model', 
            {
				url: 'users/' + this.userId + '/billing_accounts',
				returnChannel: Portal.Channels.billing_accounts,
				request: 'describe'
			});
		}
		else
		{
			if (!Portal.Vars.preloadComplete)
			{
				this.notify('startupHookComplete');
			}
			
			if(!Portal.Modules.AptanaIdManager.isLoggedIn)
			{
				return;
			}
			
			if(!msg.data.xmlData.billing_accounts)
			{
				return;
			}
			
			if(msg.data.status.toString().substr(0,1) == '5')
			{
				this.billingErrors = true;
				
				return;
			}
			
			this.billingAccounts = new Hash();
			
            msg.data.xmlData.billing_accounts.each(function(item)
            {
				var tempAccount = new BillingAccountClass(item.billing_account);
				
				this.billingAccounts.set(tempAccount.id, tempAccount);
				
            }.bind(this));
			
			this.billingLoading = false;
			
			this.notify('billingAccountsLoaded');
		}
	}
});

Object.Event.extend(UserClass);


Portal.API.startup.registerStartupItem(12, 
{
	startupMessage: 'Loading billing data...',
	onInit: function()
	{
		Portal.API.models.loadModel('currentUser', (typeof(CloudUserClass) != 'undefined') ? CloudUserClass : UserClass, true);
	}
});