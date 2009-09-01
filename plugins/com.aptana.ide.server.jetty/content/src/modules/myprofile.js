var MyProfileModule = Class.create(
{
	initialize: function initialize()
	{
		this.templates		= {};
	},
	
	finishInit: function finishInit()
	{
		this.registerObservers();
		this.extendSelf();
		
		this._init();
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		if(msg.data.response == 'updateUser')
		{
			this.doEditUser(msg);
		}
	},
	
	registerObservers: function registerObservers()
	{
		EventManager.subscribe('/portal/user/update', { channelHook: 'MyProfile' });
	},
	
	extendSelf: function extendSelf()
	{
		Object.extend(this, MyProfileControllers);
		Object.extend(this, MyProfileViews);
	}
});

var MyProfileControllers = 
{
	_init: function _init()
	{
		this._initViews();
	},
	
	doEditUser: function doEditUser(msg)
	{
		if(!msg)
		{
			var valid = this.validator.validate();
			
			if(!valid)
			{
				return;
			}
			
			var userData = $('user_form').serialize({ hash: true });
			
			$('user_form').disable();
			
			var role = $F('role');
			var email = $F('email');
			var company = $F('company');
			var organization_type = $F('organization_type');
			var organization_size = $F('organization_size');
			var sites_per_year = $F('sites_per_year');
			var ajax = $F('ajax');
			var javascript = $F('javascript');
			var php = $F('php');
			var ruby = $F('ruby');
			var python = $F('python');
			var java = $F('java');
			var net = $F('net');
			var site_development = $F('site_development');
			var application_development = $F('application_development');
			// var newsletter = $F('newsletter');
			
			Portal.Data.currentUser.update(userData);
			
			var msg =
			{
				first_name:					Portal.Data.currentUser.firstName,
				last_name:					Portal.Data.currentUser.lastName,
				email:						Portal.Data.currentUser.email,
				phone:						Portal.Data.currentUser.phone,
				address1:					Portal.Data.currentUser.address.address1,
				address2:					Portal.Data.currentUser.address.address2,
				city:						Portal.Data.currentUser.address.city,
				state:						Portal.Data.currentUser.address.state,
				zipcode:					Portal.Data.currentUser.address.zipcode,
				country:					Portal.Data.currentUser.address.country,		
				role: 						role,
				company: 					company,
				organization_type: 			organization_type,
				organization_size: 			organization_size,
				sites_per_year: 			sites_per_year,
				ajax:						ajax,
				javascript:					javascript,
				php:						php,
				ruby:						ruby,
				python:						python,
				java:						java,
				net:						net,
				site_development:			site_development,
				application_development:	application_development,
				request:	'updateUser'
			};
			
			msg = $H(msg);
			
            msg.each(function(item)
            {
				if(item.value == '1')
				{
					item.value = true;
				}
				
				if(item.value == null)
				{
					item.value = false;
				}
				
				msg.set(item.key, item.value);
            });
			
			$('do_edit_profile_cage').hide();
			$('edit_profile_activity').show();
			
			EventManager.publish('/portal/user/update', msg.toObject());
			
		}
		else
		{
			if(msg.data.success == true)
			{
				this.templates.main.set('userData', Portal.Data.currentUser);
				this.templates.main.set('editMode', false);
						
				Portal.Data.currentUser.fetchUserChannels();
				Portal.Data.currentUser.fetchChannelSubs();
						
				$('do_edit_profile_cage').hide();
				$('edit_profile_activity').hide();
				$('edit_profile_button_cage').show();
			}
			else
			{
				Portal.API.dialogs.alert(Portal.API.templates.parseErrors('There was a problem updating your profile', msg.data.errors));
				
				this.templates.main.set('editMode', false);
						
				$('do_edit_profile_cage').hide();
				$('edit_profile_button_cage').show();
			}
		}
	}
}

var MyProfileViews = 
{
	_initViews: function _initViews()
	{
        View.load(
        {
			main:
			{
				file: 'templates/my_profile/my_profile_main.html',
				binding: function()
				{
					return $('myProfileCage');
				},
				scope:
				{
					userData: Portal.Data.currentUser,
					editMode: false
				},
				behaviors: function()
				{
					$('user_form').onSubmit = function() { return false; }
		
					$('user_form').observe('submit', function(event)
					{
						Event.stop(event);
						return false;
					});
					
					
					$('edit_profile_button').stopObserving();
                    $('edit_profile_button').observe('click', function()
                    {
						this.templates.main.set('editMode', true);
						
						$('do_edit_profile_cage').show();
						$('edit_profile_button_cage').hide();
						
                    }.bind(this));
					
					this.validator = new Validation('user_form',
					{
						onSubmit: false,
						immediate : true
					});
					
					$('cancel_edit_profile').stopObserving('click');
                    $('cancel_edit_profile').observe('click', function()
                    {
						this.templates.main.set('editMode', false);
						
						$('do_edit_profile_cage').hide();
						$('edit_profile_button_cage').show();
                    }.bind(this));
					
					$('do_edit_profile').stopObserving('click');
                    $('do_edit_profile').observe('click', function()
                    {
						this.doEditUser();
						
                    }.bind(this));
					
				}.bind(this)
			},
			
			personalInfo:
			{
				file: 'templates/my_profile/personal_info.html'
			},
			
			personalInfoForm:
			{
				file: 'templates/my_profile/personal_info_form.html'
			},
			
			address:
			{
				file: 'templates/my_profile/address.html'
			},
			
			addressForm:
			{
				file: 'templates/my_profile/address_form.html'
			},
			
			companyInfo: 
			{
				file: 'templates/my_profile/company_info.html'
			},
			
			companyInfoForm:
			{
				file: 'templates/my_profile/company_info_form.html'
			},
			
			interests:
			{
				file: 'templates/my_profile/interests.html'
			},
			
			interestsForm:
			{
				file: 'templates/my_profile/interests_form.html'
			}
			
		}, 
		function(templates)
        {
			this.templates = templates;
			
			var subTemplates = 
			{
				personalInfo: 		this.templates.personalInfo,
				personalInfoForm:	this.templates.personalInfoForm,
				address: 			this.templates.address,
				addressForm: 		this.templates.addressForm,
				companyInfo:		this.templates.companyInfo,
				companyInfoForm:	this.templates.companyInfoForm,
				interests:			this.templates.interests,
				interestsForm:		this.templates.interestsForm
			}
			
			this.templates.main.set('subTemplates', subTemplates);
			
        }.bind(this));
	}
}
