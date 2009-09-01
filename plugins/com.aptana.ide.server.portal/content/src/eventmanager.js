var EventManagerObject = Class.create( 
{
	initialize: function()
	{
		this.currentChannelSubs = new Array();
		this.channelSubObjects	= new Hash();
		this.channelHooks 		= new Hash();
		this.messageQueue 		= new Array();
		this.processingQueue 	= false;
		this.connected			= false;
	},
	
	/**
	 * Creates the connection to comet
	 * 
	 * When the comet tunnel is opened, this function will continue the loading of the portal
	 */
	initComet: function()
	{
		if(this.connected)
		{
			return;
		}
		
		// clear any old stuff that might be straggling...
		dojox.cometd.disconnect();
		
		dojo.connect(dojox.cometd, '_finishInit', function()
		{
			this.notify('cometInitComplete');
			this.connected = true;
		}.bind(this));
		
        Portal._log(
        {
            eventType: 'UPDATE',
            controller: 'EventManager',
			msg: 'Binding comet to port ' + Portal.Vars.cometPort
        });
		
		dojox.cometd.init('http://127.0.0.1:' + Portal.Vars.cometPort + '/cometd');
	},
	
	/**
	 * Subscribes to a channel (if not already subscribed)
	 * 
	 * When passing the channel, simply omit the trailing slash ("/") if you do not want the 
	 * clientId included in the channel sub
	 * 
	 * @param {String} channel
	 */
	subscribe: function(channel, data)
	{
		if(channel.endsWith('/'))
		{
			channel = channel + dojox.cometd.clientId;
		}
		
		if(this._isSubscribed(channel))
		{
			Portal._log(
	        {
				eventType: 'SKIP CHANNEL SUB',
				controller: 'EventManager',
				msg: 'Already subscribed to channel: ' + channel
			});
			
			if(data && typeof(data.channelHook) != 'undefined')
			{
				this.addChannelHook(channel, data.channelHook);
			}
			
			if(data && typeof(data.onComplete) == 'function')
			{
				data.onComplete();
			}
			
			return;
		}
		
		var channelSub = dojox.cometd.subscribe(channel, 'handleMessageResponse');
		this.currentChannelSubs.push(channel);
		
		if(data)
		{
	        channelSub.addCallback(function()
	        {
				if(typeof(data.onComplete) == 'function')
				{
					data.onComplete();
				}
	        });
			
			if(typeof(data.channelHook) != 'undefined')
			{
				this.addChannelHook(channel, data.channelHook)
			}
		}
		
		this.channelSubObjects.set(channel, channelSub);
		
		Portal._log(
        {
			eventType: 'CHANNEL SUB',
			controller: 'EventManager',
			msg: 'Subscribed to channel: ' + channel
		});
	},

	/**
	 * Unsubscribes from a channel
	 * 
	 * @param {String} channel
	 */
	unsubscribe: function(channel)
	{
		if(channel.endsWith('/'))
		{
			channel = channel + dojox.cometd.clientId;
		}
		
		if(this.currentChannelSubs.indexOf(channel) == -1)
		{
			return;
		}
		
		this.currentChannelSubs = this.currentChannelSubs.without(channel);
		dojox.cometd.unsubscribe(this.channelSubObjects.get(channel));
		this.channelSubObjects.unset(channel);
	    
        Portal._log(
        {
			eventType: 'CHANNEL UNSUB',
			controller: 'EventManager',
			msg: 'Unsubscribed from channel: ' + channel
		});
	},
	
	/**
	 * Returns true / false if we're subbed to a channel
	 * 
	 * Created this as a separate function if we ever decide we need some extra 
	 * logic in here
	 * 
	 * @param {Strings} channel
	 */
	_isSubscribed: function(channel)
	{
        if(this.currentChannelSubs.indexOf(channel) != -1)
		{
			return true;
		}
		
		return false;
	},
	
	/**
	 * Adds a module to the hooks for a channel, skips the add if the hook already exists
	 * 
	 * @param {String} channel
	 * @param {String} module
	 */
	addChannelHook: function(channel, module)
	{
		if(channel.endsWith('/'))
		{
			channel = channel + dojox.cometd.clientId;
		}
		
		// see if there's a hash map for the current channel
		var hookExists = this.channelHooks.get(channel);
		
		// create the hash if we need to...
		if(!hookExists)
		{
			this.channelHooks.set(channel, []);
		}
		
		var modulesArray = this.channelHooks.get(channel);
		
		if(modulesArray.indexOf(module) == -1)
		{
			modulesArray.push(module);
            Portal._log(
            {
				eventType: 'ADD MESSAGE HOOK',
				controller: 'EventManager',
				msg: 'Added module to channel hook',
				extra:
				[
					'channel: ' + channel,
					'module: ' + module
				]
			});
			
			this.channelHooks.set(channel, modulesArray);
		}
		else
		{
			Portal._log(
            {
				eventType: 'SKIP ADD MESSAGE HOOK',
				controller: 'EventManager',
				msg: 'Already added module to channel hook',
				extra:
				[
					'channel: ' + channel,
					'module: ' + module
				]
			});
		}
	},
	
	/**
	 * Removes a module from all hooks if it is no longer loaded...
	 * 
	 * @param {String} module
	 */
	removeHook: function(module)
	{
        this.channelHooks.each(function(hookInfo)
        {
			var tempArray = hookInfo.value;
			
			if(tempArray.indexOf(module) == -1)
			{
				return;
			}
			
			tempArray = tempArray.without(module);
			
			this.channelHooks.set(hookInfo.key, tempArray);
            
			Portal._log(
            {
				eventType: 'REMOVED MESSAGE HOOK',
				controller: 'EventManager',
				msg: 'Removed module from channel hook: ' + hookInfo.key + ' (' + module + ')'
			});
        }.bind(this));
	},
	
	/**
	 * Publishes a message to a channel, via the message queue
	 * 
	 * @param {String} channel
	 * @param {Object} message
	 */
	publish: function(channel, message)
	{
		if(!channel)
		{
			return;
		}
		
		if(channel == '/portal/cloud/model' && 'url' in message)
		{
			if(Portal.Vars.devMode)
			{
				message.url = Portal.Vars.devSiteManagerUrl + message.url;
			}
			else
			{
				message.url = Portal.Vars.siteManagerUrl + message.url;
			}
		}
		
		try
		{
			Portal._log(
	        {
				eventType: 'PUBLISH MESSAGE',
				controller: 'EventManager',
				msg: 'Publishing message',
				extra: 
				[
					'channel: ' + channel,
					'message: ' + Object.toJSON(message)
				]
			});
			
			dojox.cometd.publish(channel, message);
		}
		catch(e)
		{
			Portal._log(
            {
                eventType: 'ERROR',
                controller: 'EventManager',
                msg: 'Bad message data / channel'
            });
		}
	},
	
	/**
	 * Publishes all the messages in the message queue
	 * 
	 * It's also important to note that this message pauses a tenth of second between executions
	 * to keep everything sane in the browsers (mostly stupid IE :)
	 */
	processMessageQueue: function()
	{
		this.processingQueue = true;
		
		// this needs to be here too for some reason...
		if(this.messageQueue.length == 0)
		{
			this.processingQueue = false;
			return;
		}
		
		var msgToPublish = this.messageQueue.shift();
		
		try
		{
			if(msgToPublish.channel != '/portal/cloud/log')
			{
	            Portal._log(
	            {
					eventType: 'PUBLISH MESSAGE',
					controller: 'EventManager',
					msg: 'Publishing message',
					extra: 
					[
						'channel: ' + msgToPublish.channel,
						'message: ' + Object.toJSON(msgToPublish.message)
					]
				});
			}
			
			dojox.cometd.publish(msgToPublish.channel, msgToPublish.message);	
		}
		catch(e)
		{
            Portal._log(
            {
                eventType: 'ERROR',
                controller: 'EventManager',
                msg: 'Malformed message: ' + Object.toJSON(msgToPublish)
            });
		}
		
		
		if(this.messageQueue.length == 0)
		{
			this.processingQueue = false;
		}
		else
		{
			this.processMessageQueue();
		}
	},
	
	/**
	 * Handles a response on a channel
	 * 
	 * Basically, we take a look at what channel this message came in on, and execute the dispatchEvent function for module(s) 
	 * who hook to this channel and pass them the message as well
	 * 
	 * @param {Object} msg
	 */
	handleMessageResponse: function(msg)
	{
		var dispatchedModules = [];
		var modulesToDispatch = this.channelHooks.get(msg.channel);
		
		if(modulesToDispatch)
		{
            modulesToDispatch.each(function(module)
            {
				if (Portal.Modules[module]) 
				{
					Portal.Modules[module].dispatchEvent.call(Portal.Modules[module], msg);
					dispatchedModules.push(module);
				}
				else if (Portal.Data[module])
				{
					Portal.Data[module].dispatchEvent.call(Portal.Data[module], msg);
					dispatchedModules.push(module);
				}
            });
		}
		
		if(Portal.Vars.debugMessages == true)
		{
			Portal._log(
	        {
				eventType: 'MESSAGE RESPONSE',
				controller: 'EventManager',
				msg: 'Received message for channel',
				extra:
				[
					'channel: ' + msg.channel,
					'dispatched modules: ' + dispatchedModules.inspect(),
					'message contents: ' + Object.toJSON(msg.data)
				]
			});
		}
		else
		{
			Portal._log(
	        {
				eventType: 'MESSAGE RESPONSE',
				controller: 'EventManager',
				msg: 'Received message for channel',
				extra:
				[
					'channel: ' + msg.channel,
					'dispatched modules: ' + dispatchedModules.inspect()
				]
			});
		}
	}
});

Object.Event.extend(EventManagerObject);

function handleMessageResponse(msg)
{
	EventManager.handleMessageResponse(msg);
}
