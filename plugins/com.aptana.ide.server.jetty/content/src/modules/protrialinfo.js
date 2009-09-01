var ProTrialInfoModule = Class.create(  
{
	initialize: function()
	{
		this.templates		= {};
		this.licenseType	= 'none';
		this.daysLeft		= 0;
	},
	
	finishInit: function()
	{
		this.registerObservers();
		this.extendSelf();
		
		this._init();
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		this.setLicenseInfo(msg.data);
	},
	
	registerObservers: function registerObservers()
	{
		EventManager.subscribe('/portal/license', {
			channelHook: 'ProTrialInfo'
		});
	},
	
	extendSelf: function extendSelf()
	{
		Object.extend(this, ProTrialInfoControllers);
		Object.extend(this, ProTrialInfoViews);
	}
});

var ProTrialInfoControllers =
{
	_init: function _init()
	{
		this._initViews();
	},
	
	fetchLicenseStatus: function fetchLicenseStatus()
	{
		EventManager.publish('/portal/license', { request: 'licenseInfo' });
	},
	
	setLicenseInfo: function setLicenseInfo(data)
	{
		if(data.daysLeft < 0 || data.type == 'none')
		{
			this.licenseType = 'none';
			this.daysLeft = 0;
		}
		else
		{
			this.licenseType = data.type;
			this.daysLeft = data.daysLeft;
		}
		
		this.templates.main.set('data', { licenseType: this.licenseType, daysLeft: this.daysLeft });
	}
}

var ProTrialInfoViews = 
{
	_initViews: function _initViews()
	{
        View.load(
        {
			main:
			{
				file: 'templates/pro_trial/pro_trial.html',
				binding: function()
				{
					return $('proTrialInfoCage');
				}
			}
		},
		function (templates) 
		{
			this.templates = templates;
			
			this.fetchLicenseStatus();
			
		}.bind(this));
	}
}
