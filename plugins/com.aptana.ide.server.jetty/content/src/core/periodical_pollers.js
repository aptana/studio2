
Portal.Pollers.register = function register(type, id, options)
{
    if (!(type in Portal.Pollers)) 
    {
        Portal.Pollers[type] = {};
    }
    
	if(!(id in Portal.Pollers[type]))
    {
	    Portal.Pollers[type][id] = new PeriodicalMethodExecuter(options);
		
		Portal.API.logging.logEvent('REGISTER METHOD POLLER', 'Portal.Polling.' + type + '.' + id);
		
		return true;
	} 
    
    return false;
};

var PeriodicalMethodExecuter = Class.create(PeriodicalExecuter, 
{
	initialize: function initialize ($super, options)
	{
		this.cycleComplete 	= true;
		this.observing		= false;
		this.options 		= Object.extend(
		{
			object:			{},
			method: 		'',
			frequency: 		1,
			args:			[],
			notifyEvent:	''
		}, options || {});
		
		$super(function()
		{
			this.options.object[this.options.method].apply(this.options.object, this.options.args)
		}.bind(this), this.options.frequency);
	},
	
	changeIntervalLength: function changeIntervalLength (frequency)
	{
		
		clearInterval(this.timer);
		this.timer = null;
		this.options.frequency = frequency;
		this.frequency = frequency;
		
		Portal.API.logging.logEvent('NEW TIMER INTERVAL', this.options.method + ' (' + this.options.frequency + ')');
		
		this.registerCallback();
	},
	
	stop: function stop($super)
	{
		$super();
	},
	
	destroy: function destroy()
	{
		this.stop();
		this.options.method = null;
	},
	
	onTimerEvent: function onTimerEvent()
	{
		Portal.API.logging.logEvent('TIMER EVENT', this.options.method + ' (' + this.options.frequency + ')');
		
		if(!this.cycleComplete || this.currentlyExecuting)
		{
			return;
		}
		
		try
		{
			this.currentlyExecuting = true;
			
			if(!this.observing)
			{
				this.options.object.observe(this.options.notifyEvent, function(cancel)
				{
					if(cancel == true)
					{
						this.currentExecuting = false;
						this.destroy();
					}
					
					if(arguments.length != this.options.args.length)
					{
						// return;
					}
					
					this.cycleComplete = true;	
					
					/*		
					for(var i=0; i<arguments.length; i++)
					{
						if(arguments[i] != this.options.args[i])
						{
							this.cycleComplete = false;
							return;
						}
					}
					*/
					
					
					this.notify('cycleComplete', this.options.notifyEvent);
					
				}.bind(this));
				
				this.observing = true;
			}
			
			this.cycleComplete = false;
			
			try 
			{
				this.options.object[this.options.method].apply(this.options.object, this.options.args);
			}
			catch (e)
			{
				console.warn('Error with polling event, could not execute:' + e);
				
				this.destroy();
			}
		}
		catch(e)
		{
			console.warn(e);
		}
		finally
		{
			this.currentlyExecuting = false;
		}
	}
});

Object.Event.extend(PeriodicalMethodExecuter);

/*
	var p = new PeriodicalPoller({
		callback: function(params,request){
			//request will only be present if url was passed
		},
		frequency: 1,
		//OPTIONAL
		url: 'http://blah...',
		options: {
			parameters: {
				//POST params
			},
			asynchronous: false
		},
		params: {} //can be any object, not just a Hash
	});
	p.observe('executed',function(){
		//callback was executed
	});
*/
var PeriodicalPoller = Class.create(PeriodicalExecuter,
{
	initialize: function initialize($super, options)
	{
		this.activeAjaxRequest = null;
		this.options = Object.extend(
		{
			callback: Prototype.emptyFunction,
			frequency: 1,
			url: null,
			options: null,
			params: null
		}, options);
		$super(options.callback, options.frequency);
	},
	changeIntervalLength: function changeIntervalLength(frequency)
	{
		clearInterval(this.timer);
		this.timer = null;
		this.options.frequency = frequency;
		this.registerCallback();
	},
	stop: function stop($super)
	{
		if(this.activeAjaxRequest)
		{
			this.activeAjaxRequest.transport.abort();
			this.activeAjaxRequest = null;
		}			
		$super();
	},
	destroy: function destroy()
	{
		this.stop();
		this.options.callback = null;
	},
	onTimerEvent: function onTimerEvent()
	{
		if(!this.currentlyExecuting)
		{
			try
			{
				this.currentlyExecuting = true;
				if(options.url)
				{
					if(!this.activeAjaxRequest)
					{
						this.activeAjaxRequest = new Ajax.Request(url, Object.extend(
						{
							onComplete: function(request)
							{
								this.options.callback(params,request);
								this.notify('executed',params);
								this.activeAjaxRequest = null;
							}.bind(this)
						}, this.options.options));
					}
				}
				else
				{
					var params = $value(this.options.params);
					this.options.callback(params);
					this.notify('executed',params);
				}
			}
			finally
			{
				this.currentlyExecuting = false;
			}
		}
	}
});