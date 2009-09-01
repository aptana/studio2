var BillingAccountsModule = Class.create(
{
	initialize: function initialize()
	{
		this.processingRequest	= false;
		this.templates			= {};
		this.cardToDelete		= 0;
	},
	
	finishInit: function finishInit()
	{
		this.extendSelf();
		this.registerObservers();
		
		this._init();		
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		if (msg.data.response == 'delete')
		{
			this.handleDeleteCard(msg);
		}
		else if (msg.data.response == 'commit')
		{
			this.handleAddCard(msg);
		}
	},
	
	extendSelf: function extendSelf()
	{
		Object.extend(this, BillingAccountsControllers);
		Object.extend(this, BillingAccountsViews);
	},
	
	registerObservers: function registerObservers()
	{
		Portal.Data.currentUser.observe('billingAccountsLoaded', function() 
		{
			this._init();
			
		}.bind(this));
		
		EventManager.subscribe(Portal.Channels.billing_accounts, 
        {
            channelHook: 'BillingAccounts'
        });
	}
});

var BillingAccountsControllers =
{
	_init: function _init()
	{
		// nothing to do until the billing accounts are loaded
		if(Portal.Data.currentUser.billingLoading)
		{
			$('billingAccountCage').update('<div class="working">Loading billing accounts...</div>');
			
			return;
		}
		
		// callbacks from messages..
		if(this.processingRequest)
		{
			$('billingInfo').reset();
			$('add_card_controls').show();
			
			$('account_create_activity').hide();
			$('add_card_cage').hide();
			
			$('billingAccountCage').setStyle(
		    {
		        height: $('billingAccountCage').getHeight() + 'px'
		    });
			
			this.processingRequest = false;
		}
		
		this._initViews();
	},
	
	showSiteInCloud: function showSiteInCloud(siteId)
	{
		Portal.Vars.siteToLoad = siteId;
		Portal.API.tabs.loadTabManually('tab_my_cloud');
	},
	
	deleteCard: function deleteCard(event)
	{
		if(this.processingRequest)
		{
			return;
		}
		
		var cardId = Event.element(event).id.replace('delete_', '');
		
		if(!cardId)
		{
			cardId = Event.element(event).up().id.replace('delete_', '');
		}
		
		this.cardToDelete = cardId;
		
        Portal.API.dialogs.confirm(
        {
			message: 'Are you sure you wish to delete the selected credit card?',
			title: 'Confirm delete...',
			onConfirm: function()
			{
				$('delete_' + cardId).replace('<div class="working" style="text-align: left;" id="delete_' + cardId + '">Deleting card...</div>');
				
				this.processingRequest = true;
				
				EventManager.publish('/portal/cloud/model', 
                {
					url: 			'users/' + Portal.Data.currentUser.userId + '/billing_accounts/' + cardId,
					request: 		'delete',
					returnChannel: 	Portal.Channels.billing_accounts
				});
			}.bind(this)
		});
	},
	
	handleDeleteCard: function handleDeleteCard(msg)
	{
		// success
		if(('success' in msg.data && msg.data.success == true) || msg.data.status.toString().substr(0, 1) == '2')
		{
			this.cardToDelete = 0;
			Portal.Data.currentUser.fetchBillingAccounts();
			return;
		}
		
		this.processingRequest = false;
		
		// error
		$('delete_' + this.cardToDelete).replace('<div class="clean-error" style="text-align: center;">' + Portal.API.templates.parseErrors('There was a problem deleting this credit card.', msg.data.errors) + '</div>');
	},
	
	addCard: function addCard()
	{
		if(this.processingRequest)
		{
			return;
		}
		
		var formValid = true;
		
		formValid = this.validator.validate();
		
		if(!formValid)
		{
			return;
		}
		
		// check that the date is valid...
		var d = new Date();
		var currentMonth = d.getMonth();
		var currentYear = d.getFullYear();

		var selectedMonth = $F('card_expiry_month');
		
		if(selectedMonth.substr(0, 1) == '0')
		{
			selectedMonth = selectedMonth.substr(1);
		}
		
		if((parseInt(selectedMonth) <= (currentMonth+1) && parseInt(('20' + $F('card_expiry_year'))) <= currentYear) || parseInt(('20' + $F('card_expiry_year'))) < currentYear)
		{
			$('expiry_error').show();
			return;
		}
		
		var name 		= $F('cardholder_name');
		var number 		= $F('card_number');
		var expiration 	= $F('card_expiry_month') + '/' + $F('card_expiry_year');
		var cvv2 		= $F('card_cvv2');
		var address1 	= $F('billing_address_1');
		var address2 	= $F('billing_address_2');
		var city 		= $F('billing_city');
		var state 		= $F('billing_state');
		var country 	= $F('billing_country');
		var zipcode		= $F('billing_zip');
		var phone 		= $F('billing_phone');
		
		$('billingInfo').disable();
		$('add_card_controls').hide();
		
		$('account_create_activity').removeClassName('clean-error');
		$('account_create_activity').addClassName('working');
		$('account_create_activity').update('Adding credit card...');
		
		$('account_create_activity').show();
		
		this.processingRequest = true;
		
		EventManager.publish('/portal/cloud/model', 
        {
			url: 'users/' + Portal.Data.currentUser.userId + '/billing_accounts',
			xmlData: 
			{
				billing_account:
				{
					id:			null,
					name:		name,
					number:		number,
					expiration:	expiration,
					cvv2:		cvv2,
					address1:	address1,
					address2:	address2,
					city:		city,
					state:		state,
					country:	country,
					zipcode:	zipcode,
					phone:		phone
				}
			},
			request: 'commit',
			returnChannel: Portal.Channels.billing_accounts
		});
	},
	
	handleAddCard: function handleAddCard(msg)
	{
		// success
		if(!msg.data.status || msg.data.status.toString().substr(0, 1) == 2)
		{
			Portal.Data.currentUser.fetchBillingAccounts();
			return;
		}
		
		this.processingRequest = false;
		
		// error
		$('account_create_activity').removeClassName('working');
		$('account_create_activity').addClassName('clean-error');
		$('account_create_activity').update(Portal.API.templates.parseErrors('An error occurred adding your credit card.', msg.data.errors));
		
		$('billingInfo').enable();
		$('add_card_controls').show();
		
	}
}

var BillingAccountsViews =
{
	_initViews: function _initViews()
	{
        View.load(
        {
			main:
			{
				file: 'templates/billing_accounts/billing_accounts_new.html',
				binding: function()
				{
					return $('billingAccountCage');
				},
				scope:
				{
					billingAccounts:	Portal.Data.currentUser.billingAccounts,
					billingErrors: 		Portal.Data.currentUser.billingErrors
				},
				behaviors: function()
				{
					var fixDisplay = function()
					{
                        $('billingAccountCage').setStyle(
                        {
                            height: $('billing_accounts_wrap').getHeight() + 20 + 'px'
                        });
					}
					
					if(Portal.Data.currentUser.billingErrors)
					{
						$('billingAccountCage').setStyle(
                        {
                            height: 400 + 'px'
                        });
					}
					
					if($('show_add_credit_card'))
					{
						// add card observer
	                    $('show_add_credit_card').observe('click', function()
	                    {
							if($('add_card_cage').style.display == 'none')
							{
								$('add_card_cage').show();
								fixDisplay();
							}
	                    });
						
					}
					// do add card observer
                    $('do_add_credit_card').observe('click', this.addCard.bindAsEventListener(this));
					
					if($('cancel_add_credit_card'))
					{
						// cancel add observer
	                    $('cancel_add_credit_card').observe('click', function()
	                    {
							$('add_card_cage').hide();
							fixDisplay();
	                    });
					}
					
					// delete card observers
                    $$('a.delete_card').each(function(item)
                    {
                        $(item).observe('click', this.deleteCard.bindAsEventListener(this));
                    }.bind(this));
					
					$('billingInfo').onSubmit = function() { return false; }
			
					$('billingInfo').observe('submit', function(event)
					{
						Event.stop(event);
						return false;
					});
					
					Validation.add('card-number', 'Please enter a valid card number', { minLength: 13 });
					
					// set up the validator
					this.validator = new Validation('billingInfo',
					{
						onSubmit: false,
						immediate : true
					});
					
					this.validator.reset();
					
					fixDisplay();
					
				}.bind(this)
			},
			
			addCreditCard:
			{
				file: 'templates/billing_accounts/add_credit_card.html'
			}
		}, 
		function(templates)
        {
			this.templates = templates;
			
			// triggers the render...
			this.templates.main.set('addCreditCardForm', this.templates.addCreditCard);
			
        }.bind(this));
	}
}
