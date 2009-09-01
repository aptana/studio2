var MyAptanaModule = Class.create({
	initialize: function initialize() 
	{
		this.templates 			= null;
		this.currentProduct 	= 'studio';
		this.changingState		= false;
	},
	
	finishInit: function finishInit()
	{
		this.registerObservers();
		this.extendSelf();
		this._init();
	},
	
	dispatchEvent: function dispatchEvent()
	{
		// nothing here
	},
	
	registerObservers: function registerObservers()
	{
		// nothing here
	},
	
	extendSelf: function extendSelf()
	{
		Object.extend(this, MyAptanaControllers);
		Object.extend(this, MyAptanaViews);
	}
});

var MyAptanaControllers = {
	_init: function _init()
	{
		this._initViews();
	}
}

var MyAptanaViews = {
	_initViews: function _initViews()
	{
		View.load({
			studio: {
				file: 'templates/studio.html',
				binding: function() {
					return $('product_content');
				},
				behaviors: function() {
					$('product_link_studio').observe('click', function(){
						if(this.currentProduct != 'studio')
						{
							this.renderStudio();
						}
					}.bind(this));
					
					$('product_link_plugins').observe('click', function() {
						if(this.currentProduct != 'plugins')
						{
							this.renderPlugins();
						}
					}.bind(this));
					
					$('product_link_cloud').observe('click', function() {
						if(this.currentProduct != 'cloud')
						{
							this.renderCloud();
						}
					}.bind(this));
					
					Portal.API.modules.loadModule('RecentFiles', RecentFilesModule, false);
					Portal.API.utils.fetchRemoteContent('using_studio_with', 'using_studio_with');
					Portal.API.utils.fetchRemoteContent('right_content', 'right_content');
				}.bind(this)
			},
			
			plugins: {
				file: 'templates/plugins.html',
				binding: function() {
					return $('product_content');
				},
				behaviors: function() {
					Portal.API.utils.fetchRemoteContent('plugins', 'plugins_right');
					Portal.API.modules.loadModule('PluginManager', PluginManagerModule, false);
					
					if($('notify_plugins').style.display != 'none')
					{
						new Effect.Fade('notify_plugins', { duration: 0.25 });
					}
				}
			},
			
			cloud: {
				file: 'templates/cloud.html',
				binding: function() {
					return $('product_content');
				},
				behaviors: function behaviors() {
					Portal.API.utils.fetchRemoteContent('cloud_top', 'cloud_top');
					Portal.API.utils.fetchRemoteContent('cloud_right_top', 'cloud_right_top');
				}
			}
		}, function(templates) {
			
			if(typeof(UndeployedProjectsModule) !== 'undefined')
			{
				try {
					$('current_pointer').setStyle({
						marginLeft: '75px'
					});
					$('icon_block_wrap').setStyle({
						width: '450px'
					});
					$('logo_cloud').show();
					
					$('icon_block').setStyle({ height: $('icon_block').getHeight() + 'px' });
				}
				catch (e)
				{
					console.warn(e);
				}
			}
			
			this.templates = templates;
			this.templates.studio.render();
			
			Portal.Vars.currentHelpContent = 'My_Aptana_-_Studio';
			
			$('logo_' + this.currentProduct).setStyle(
	        {
				backgroundPosition: 'center -185px'
			});
			
		}.bind(this));
	},
	
	renderStudio: function renderStudio()
	{
		this.setCurrentProduct('studio');
	},
	
	renderPlugins: function renderPlugins()
	{
		this.setCurrentProduct('plugins');
	},
	
	renderCloud: function renderCloud()
	{
		this.setCurrentProduct('cloud');
	},
	
	setCurrentProduct: function setCurrentProduct(product)
	{
		if(this.changingState)
		{
			return;
		}
		
		// figure out which way to move the pointer
		// this could be optimized, but we're going for quick n' dirty here :)
		var moveBy = 150;
		
		if((this.currentProduct == 'cloud' && product == 'plugins') || (this.currentProduct == 'plugins' && product == 'studio'))
		{
			moveBy = -150;
		}
		else if (this.currentProduct == 'studio' && product == 'cloud')
		{
			moveBy = 300;
		}
		else if (this.currentProduct == 'cloud' && product == 'studio')
		{
			moveBy = -300;
		}
		
		$('logo_' + this.currentProduct).setStyle(
        {
			backgroundPosition: 'center 0px'
		});
		
		this.currentProduct = product;
		
		var helpName = '';
		switch(this.currentProduct)
		{
			case 'studio':
				helpName = 'Studio';
				break;
			case 'plugins':
				helpName = 'Plugins';
				break;
			case 'cloud':
				helpName = 'Cloud';
				break;
		}
		
		Portal.Vars.currentHelpContent = 'My_Aptana_-_' + helpName;
		
		$('logo_' + this.currentProduct).setStyle(
        {
			backgroundPosition: 'center -185px'
		});
		
		this.changingState = true;
		
		new Effect.MoveBy('current_pointer', 0, moveBy, {
            duration: 0.5,
			afterFinish: function() {
				this.templates[this.currentProduct].render();
				this.changingState = false;
			}.bind(this)
		});
	}
}
