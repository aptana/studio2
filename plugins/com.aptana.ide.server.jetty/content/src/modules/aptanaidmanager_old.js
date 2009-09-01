var AptanaIdManagerModule = Class.create(
{
	initialize: function inititalize()
	{
		Portal.Channels.user = '/portal/user';
		this.initVars();
	},
	
	initVars: function initVars()
	{
		this.userId					= '';
		this.username				= '';
		this.isLoggedIn				= false; 
		this.loginStatusContainer	= 'loginBlock';
		this.loginRetries			= 0;
		this.currentAction			= '';
		this.loginForm				= 'templates/aptana_id/form_login.html';
		this.createForm				= 'templates/aptana_id/form_create.html';
		this.usernameCreateVal		= '';
		this.captchaPublicKey		= '';
	},
	
	/**
	 * Subscribes to user event channel and then requests current user status
	 */
	finishInit: function()
	{
        EventManager.subscribe(Portal.Channels.user, 
        {
            channelHook: 'AptanaIdManager',
            onComplete: function()
            {
				EventManager.publish(Portal.Channels.user, { request: 'currentUser' });
            }
        });
		
	},
	
	
	/**
	 * Handles responses on all /portal/user channels
	 * 
	 * @param {Object} msg
	 */
	dispatchEvent: function(msg)
	{
		if(!msg.data.response)
		{
			return;
		}
		
		if(msg.data.response == 'describe')
		{
			this.captchaPublicKey = msg.data.xmlData.captcha.public_key;
		}
		
		if(msg.data.response == 'currentUser' && this.currentAction == '')
		{
			this.handleUserInfo(msg);	
		}
		else if (msg.data.response == 'logoutUser')
		{
			this.logOutUser(msg);
		}
		else if (msg.data.response == 'loginUser')
		{
			this.logInUser(msg);
		}
		else if (msg.data.response == 'createUser')
		{
			this.createNewUser(msg);
		}
		else if (msg.data.response == 'showLogin')
		{
			this.showLoginWindow();
		}
	},
	
	/**
	 * Deals with messages on the /portal/user channel
	 * 
	 * Also has a try / catch to help prevent a username = undefined error, and will continue the startup process if needed
	 * 
	 * @param {Object} msg
	 */	
	handleUserInfo: function handleUserInfo(msg)
	{
		var currentLoggedInValue = this.isLoggedIn;
		
		try
		{
			this.username = (msg.data.username != false) ? msg.data.username : false;
			this.isLoggedIn = (msg.data.username != false) ? true : false;
			
			if(this.isLoggedIn)
			{
				this.userId = msg.data.id;
				
				if (typeof(Portal.Data.currentUser) != 'undefined') 
				{
					Portal.Data.currentUser.password = msg.data.password;
				}
			}
			
			if(this.username == 'undefined' && this.loginRetries < 1)
			{
				throw "Username undefined";
			}	
		}
		catch(e)
		{
            Portal.API.logging.log(
            {
                eventType: 'ERROR',
                controller: 'AptanaIdManager',
                message: 'Username was undefined, trying again...'
            });
			
            EventManager.publish(Portal.Channels.user, 
            {
                request: 'currentUser'
            });
		}
		
		this.parseLoginHtml();
		
		// send messages to anything that may need to know about log in / out events
		if(currentLoggedInValue != this.isLoggedIn)
		{
			if(this.isLoggedIn)
			{
				this.notify('userLoggedIn');
			}
			else
			{
				this.notify('userLoggedOut');
			}
		}
		
		if(Portal.Vars.preloadComplete == false)
		{
			this.notify('startupHookComplete');
		}
	},
	
	/**
	 * Displays the appropriate text to the end user depending on logged in / out
	 * 
	 */
	parseLoginHtml: function parseLoginHtml()
	{
		this.bounceLoginTabs();
		
		if(this.isLoggedIn)
		{
			Portal.API.utils.setContent(this.loginStatusContainer, 'Signed in as: ' + this.username + ' [<a href="javascript: Portal.Modules.AptanaIdManager.logOutUser();" class="bold">sign out</a>]');
		}
		else
		{
			Portal.API.utils.setContent(this.loginStatusContainer, '<a href="javascript: Portal.Modules.AptanaIdManager.showLoginWindow();" class="bold">Sign In</a> | <a href="javascript: Portal.Modules.AptanaIdManager.showCreateWindow();" class="bold">Create Aptana ID</a>');
		}
	},
	
	/**
	 * Spawns the log in window (modal)
	 * 
	 * You can mass a custom message to this function to display to the user above the login form
	 * 
	 * @param {String} customMessage
	 */
	showLoginWindow: function showLoginWindow(customMessage)
	{
        if (!Portal.Modules.OnlineStatus.isOnline) 
        {
			Portal.API.dialogs.alert('The Aptana Cloud Manager service is unreachable.  Some functionality will not be available until it is reachable.', 'Aptana Cloud Manager Unreachable');
			
			return;
        }
		
		if (typeof(DeploymentWizardModule) != 'undefined' && Portal.Modules.DeploymentWizard.wizardDialog && Portal.Modules.DeploymentWizard.wizardDialog.isOpen == true) 
		{
			return;
		}
		
		EventManager.subscribe('/portal/user/login', { channelHook: 'AptanaIdManager' });
		
        new Ajax.Request(this.loginForm, 
        {
			onComplete: function(response)
			{
				var contents = new Template(response.responseText);
				var show = { message: 'Please fill out the form below to log in.' };
				
				if(customMessage)
				{
					show.message = customMessage;
				}
				
                this.loginDialog = new Control.Modal(contents.evaluate(show), 
                {
					closeOnClick: false,
					className: 'modal',
					width: Portal.API.dialogs.vars.dialogWidth,
					overlayOpacity: 0.75,
					fade: true,
					height: null,
					fadeDuration: 0.25,
					iframeshim: false,
					afterOpen: function()
					{
						$('loginWindow').style.display = 'none';
						$('control_overlay').style.position = 'absolute';
						
						var fixDisplay = function()
						{
							$('loginWindow').style.display = 'block';
							$('username').focus();
						}
						
						setTimeout(fixDisplay, 0);
						
						// set up return to submit the form (since it's not really a form ;)
                        this.loginHotKey = new HotKey('RETURN', function(event)
                        {
							this.logInUser()
                        }.bind(this), 
                        {
                            element: $('loginWindow'),
                            ctrlKey: false
                        });
						
						// set up esc to close the window
                        this.cancelLoginHotKey = new HotKey('ESC', function(event)
                        {
							this.cancelLogin();
                        }.bind(this), 
                        {
                            element: $('loginWindow'),
                            ctrlKey: false
                        });
						
					}.bind(this)
				});
				
				this.loginDialog.open();
			}.bind(this)
		});
	},
	
	cancelLogin: function cancelLogin()
	{
		this.loginHotKey.destroy();
		this.cancelLoginHotKey.destroy();
		
		this.loginDialog.close();
		this.loginDialog.destroy();
	},
	
	/**
	 * Shows the create new account dialog (modal)
	 * 
	 */
	showCreateWindow: function showCreateWindow()
	{
		if (!Portal.Modules.OnlineStatus.isOnline) 
        {
			Portal.API.dialogs.alert('The Aptana Cloud Manager service is unreachable.  Some functionality will not be available until it is reachable.', 'Aptana Cloud Manager Unreachable');
			
			return;
        }
		
		EventManager.subscribe('/portal/user/create', { channelHook: 'AptanaIdManager' });
		
		if(this.loginDialog && this.loginDialog.isOpen == true)
		{
			this.loginDialog.options.afterClose = function()
			{
				this.showCreateWindow();
		 	}.bind(this);

			this.loginHotKey.destroy();
			this.cancelLoginHotKey.destroy();

			this.loginDialog.close();	
			this.loginDialog.destroy();		
			
			return;
		}
		
		new Ajax.Request(this.createForm, 
        {
			onComplete: function(response)
			{
				
                this.createDialog = new Control.Modal(response.responseText, 
                {
					closeOnClick: false,
					className: 'modal',
					width: Portal.API.dialogs.vars.dialogWidth,
					overlayOpacity: 0.75,
					fade: true,
					height: null,
					fadeDuration: 0.25,
					iframeshim: false,
					afterOpen: function()
					{
						$('createWindow').style.display = 'none';
						$('control_overlay').style.position = 'absolute';
						
						var fixDisplay = function()
						{
							$('createWindow').style.display = 'block';
							$('username').focus();
						}
						
						setTimeout(fixDisplay, 0);
					}.bind(this)
				});
				
				this.createDialog.open();
			}.bind(this)
		});
	},
	
	cancelCreate: function cancelCreate()
	{
		this.createDialog.close();
		this.createDialog.destroy();
	},
	
	/**
	 * Actually performs the login.
	 * 
	 * Validates the form, and sends the message if everything's ok, and will interpret the 
	 * returned success / fail message about the actual login
	 * 
	 * @param {Object} msg
	 */
	logInUser: function logInUser(msg)
	{
		if(!msg)
		{
			$('loginError').hide();
			$('loginError').removeClassName('clean-error');
			
			var username = $F('username');
			var password = $F('password');
			
			if(!username || !password)
			{
				$('loginError').addClassName('clean-error');
				Portal.API.utils.setContent('loginError', 'Please fill out the form completely');
				$('loginError').show();
				return false;
			}
			
			$('loginError').addClassName('working');
			Portal.API.utils.setContent('loginError', 'Checking Credentials...');
			$('loginError').show();
			
			this.username = username;
			this.currentAction = 'login';
			
            EventManager.publish('/portal/user/login', 
            {
                request: 'loginUser',
                username: username,
                password: password
            });
		}
		else
		{
			this.currentAction = '';
			
			// valid login
			if(msg.data.userValid == true)
			{
				this.username = msg.data.username;
				this.password = msg.data.password;
				this.userId = msg.data.id;
				
				this.isLoggedIn = true;
				this.parseLoginHtml();
				if (!$('wizardCage')) 
				{
					this.loginDialog.close();
					this.loginDialog.destroy();
				}
				
				this.notify('userLoggedIn');
			}
			// failed login
			else
			{
				this.isLoggedIn = false;
				this.username = '';
				
				$('loginError').removeClassName('working');
				$('loginError').addClassName('clean-error');
				Portal.API.utils.setContent('loginError', 'Login Failed.  Please try again.');
			}	
		}
	},
	
	/**
	 * Logs out a user and displays the appropriate info where appropriate
	 * 
	 * @param {Object} msg
	 */
	logOutUser: function logOutUser(msg)
	{
		if(!msg)
		{
			this.currentAction = 'logout';
			
			EventManager.subscribe('/portal/user/logout', { channelHook: 'AptanaIdManager' });
			
			Portal.API.utils.setPref('portal.cloud.deployment.' + this.username + '.rememberme', false);
			Portal.API.utils.setPref('portal.cloud.deployment.' + this.username + '.rememberme.password', '');
			
			Portal.API.utils.setContent(this.loginStatusContainer, '<div class="white right floatRight" style="width: 75px;">Signing Out..</div><div class="clearfix"></div>');
			EventManager.publish('/portal/user/logout', { request: 'logoutUser' });
			
			var checkLogout = function()
			{
				if(Portal.Modules.AptanaIdManager.isLoggedIn == true)
				{
					Portal.Modules.AptanaIdManager.logOutUser();
				}
			}
			
			// setTimeout(checkLogout, 2000);
		}
		else
		{
			this.currentAction = '';
			this.isLoggedIn = false;
			this.parseLoginHtml();
			
			this.notify('userLoggedOut');
			
			if (typeof(DeploymentWizardModule) != 'undefined' && Portal.Modules.DeploymentWizard.wizardDialog && Portal.Modules.DeploymentWizard.wizardDialog.isOpen == true) 
			{
				return;
			}
			else if (typeof(DeploymentWizardModule) != 'undefined' && Portal.Modules.DeploymentWizard.deploymentInProgress == true)
			{
				return;
			}
			else 
			{
				Portal.API.dialogs.alert('You will not be able to use the Aptana Cloud features until you sign back in again.', 'You have been signed out.');
			}
			
			
			this.initVars();
		}
	},
	
	/**
	 * Validates new user creation and handles the feedback from studio regarding
	 * success / fail create events 
	 * 
	 * @param {Object} msg
	 */
	createNewUser: function(msg) 
	{
		if(!msg)
		{
			try {
			
			var username = $F('username');
			var password = $F('password');
			var email = $F('email');
			
			// make sure nothing's blank
			if(!email || !password || !username)
			{
				alert('Please fill out the form completely!');
				return false;
			}
			
			if(password.length < 5)
			{
				alert('Your password must be at least 5 characters long');
				return false;
			}
			
			// confirm passwords match
			if(password != $F('confirm_password'))
			{
				alert('Your passwords does not match.  Please try again.');
				$('password').focus();
				return false;
			}
			
			var emailRegex = new RegExp(/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i);
			
			if (!emailRegex.test(email)) 
			{
				alert('Please enter a valid email address');
				$('email').focus();
				return false;
			}
			
			// disable the form and send the message...
			$('username').disable();
			$('password').disable();
			$('email').disable();
			$('confirm_password').disable();
			
			$('createMessage').removeClassName('clean-error');
			$('createMessage').addClassName('working');
			
			Portal.API.utils.setContent('createMessage', 'Creating your account..');
			this.username = username;
			this.isLoggedIn = false;
			
            EventManager.publish('/portal/user/create', 
            {
                request: 'createUser',
                username: username,
                password: password,
				first_name: '',
				last_name: '',
                email: email,
				captcha_token: 'bogus',
				captcha_response: 'bogus'
            });
			} catch (e) { console.warn(e) }
		}
		else if ($('createMessage'))
		{
			if(msg.data.success == true)
			{
				this.isLoggedIn = true;
				this.parseLoginHtml();
				this.createDialog.close();
				this.createDialog.destroy();
			}
			else
			{
				$('createMessage').addClassName('clean-error');
				$('createMessage').removeClassName('working');
				
				Portal.API.utils.setContent('createMessage', 'Account creation failed.  Please try again.<br /><span class="size11">&bull; ' + msg.data.errors.join('</span><br /><span class="size11">&bull; ') + '</span>');
				
				$('username').enable();
				$('password').enable();
				$('email').enable();
				$('confirm_password').enable();
				
				$('username').focus();
			}
		}
	},
	
	logInExternal: function(username, password)
	{
		this.username = username;
        EventManager.publish('/portal/user/login', 
        {
            request: 'loginUser',
            username: username,
            password: password
        });	
	},
	
	bounceLoginTabs: function()
	{
		if(typeof Portal.API.tabs == 'undefined')
		{
			return;
		}
		
		var currentTabInfo = Portal.API.tabs.tabs.get(Portal.API.tabs.currentTab);
		
		if(((!currentTabInfo) || (currentTabInfo && currentTabInfo.requireLogin == true)) && this.isLoggedIn == false && Portal.Vars.preloadComplete == true)
		{
			if(Portal.API.tabs.currentTab != 'tab_my_aptana')
			{
				Portal.API.tabs.loadTabManually('tab_my_aptana');
			}
		}
		
		// rebuild the tabs
		if (Portal.Vars.preloadComplete == true) 
		{
			Portal.API.tabs.buildTabs();
		}
	},
	
	bounceMyCloud: function()
	{
		this.bounceLoginTabs();
	},
	
	maskUsername: function maskUser()
	{
		var username = $F('username');
		
		if(username != this.usernameCreateVal)
		{
			$('username').value = username.toLowerCase().gsub(' ', '');
		}
		
		this.usernameCreateVal = username;
	}
});


Object.Event.extend(AptanaIdManagerModule);

Portal.API.startup.registerStartupItem(1, 
{
	moduleName: 'AptanaIdManager',
	moduleObject: AptanaIdManagerModule,
	modulePersist: true,
	startupMessage: 'Loading user status...'
});
