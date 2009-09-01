/**
 * Global scope variable definitions
 */

var Portal = new PortalObject();
var EventManager;

/**
 * Portal Load functionality
 */
Event.observe(window, 'load', function()
{
	EventManager = new EventManagerObject();
	Portal._init();
});


Event.observe(window, 'unload', function()
{
	try
	{
		dojox.cometd.disconnect();
	}
	catch (e)
	{
		// do nothing, we're closing the window...
	}
});
