var MessageCenterModule = Class.create(
{
	initialize: function initialize()
	{
		this.unreadMessageCount = 0;
		this.urgentMessageCount = 0;
	},
	
	finishInit: function finishInit()
	{
        EventManager.subscribe('/portal/messages', 
        {
            channelHook: 'MessageCenter',
            onComplete: function()
            {
				EventManager.publish('/portal/messages', { request: 'describeMessages' });
            }
        });
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		if(msg.data.response == 'describeMessages')
		{
			this.updateMessageCount(msg);
		}
	},
	
	updateMessageCount: function updateMessageCount(msg)
	{
		this.unreadMessageCount = msg.data.unreadCount;
		this.urgentMessageCount = msg.data.urgentCount;
		
		this.updateDisplay();
	},
	
	updateDisplay: function updateDisplay()
	{
		Portal.API.utils.setContent('unreadMessageCount', this.unreadMessageCount);
		
		if(this.unreadMessageCount == 0)
		{
			$('messageCountHeader').hide();
		}
		else
		{
			$('messageCountHeader').show();
		}
		
		if(this.urgentMessageCount > 0 && $('notify_studio') && Portal.Modules.MyAptanaPortal.currentProduct != 'studio')
		{
			var newHtml = this.urgentMessageCount + ' unread message';
			newHtml += (this.urgentMessageCount == 1) ? '' : 's';
			newHtml += '<div></div>';
			
			$('notify_studio').update(newHtml);
			new Effect.Appear('notify_studio');
		}
	},
	
	showMessageCenter: function showMessageCenter()
	{
		EventManager.publish('/portal/messages', { request: 'showMessages' });
	}
	
});

