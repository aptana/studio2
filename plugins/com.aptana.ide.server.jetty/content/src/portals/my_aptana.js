var MyAptanaPortalObject = Class.create(
{
	initialize: function inititalize()
	{
		this.contentPath		= 'templates/portals/my_aptana/';
		this.templatePath		= 'templates/my_aptana/';
        this.pointerPosition	= 0;
		this.currentProduct		= 'studio';
		Portal.Vars.switchingContent	= false;
		this.currentHelpContent = '';
		this.products			= new Hash();
		this.templates			= new Hash();
		
		if(typeof(DeploymentWizardModule) != 'undefined')
		{
			this.products.set('cloud', 
	        {
	            offset: 0,
	            contentLocation: this.templatePath + 'cloud.html',
	            product: 'cloud',
	            productName: 'Cloud',
	            tagLine: 'Instantly Deploy &amp; Manage'
	        });			
		}
		else
		{
			this.products.set('cloud', 
	        {
	            offset: 0,
	            contentLocation: this.templatePath + 'cloud_noplugin.html',
	            product: 'cloud',
	            productName: 'Cloud',
	            tagLine: 'Instantly Deploy &amp; Manage'
	        });
		}
		
		offset = 0;
		
        this.products.each(function(item)
        {
			item.value.offset = offset;
			
			this.products.set(item.key, item.value);
			
			offset += 150;
        }.bind(this));
	},
	
	finishInit: function finishInit()
	{
		if(Portal.Vars.preloadComplete == false)
		{
			return;
		}
		
		Portal.API.tabs.loadPortalContent(Portal.Modules.MyAptanaPortal.contentPath + 'my_aptana.html', Portal.Modules.MyAptanaPortal);
		
		Portal.API.tabs.observe('tabFocusChanged', function(currentTab)
        {
			if(currentTab == 'tab_my_aptana')
			{
				Portal.Vars.currentHelpContent = this.currentHelpContent;
			}
        }.bind(this));
	},
	
	loadComplete: function loadComplete()
	{
        new Ajax.Request(this.templatePath + 'product_template.html', 
        {
			onComplete: function(response)
			{
				this.templates.set('productTemplate', response.responseText);
				
				this.parseProductDisplay();
				
			}.bind(this)
		});
	},
	
	parseProductDisplay: function parseProductDisplay()
	{
//		new HotKey('RIGHT', function(event)
//        {
//			if($('current_pointer') && $('content_tab_my_aptana').style.display != 'none')
//			{
//				this.getNextProduct();
//			}
//        }.bind(this),
//        {
//            ctrlKey: false
//        });
//		
//		new HotKey('LEFT', function(event)
//        {
//			if($('current_pointer') && $('content_tab_my_aptana').style.display != 'none')
//			{
//				this.getPreviousProduct();
//			}
//        }.bind(this),
//        {
//            ctrlKey: false
//        });
//		
//		var productTemplate = new Template(this.templates.get('productTemplate'));
//			
//        this.products.each(function(product)
//        {
//			if (!$('logo_' + product.value.product)) 
//			{
//				$('icon_block_wrap').innerHTML += productTemplate.evaluate(product.value);
//			}
//        });
//		
//		this.boundClickHandler = this.clickHandler.bindAsEventListener(this);
//		
//		this.products.each(function(product)
//		{
//			if(this.boundClickHandler)
//			{
//				Event.stopObserving('product_link_' + product.key, 'click', this.boundClickHandler);
//			}
//			
//			Event.observe('product_link_' + product.key, 'click', this.boundClickHandler);
//		}.bind(this));
		
		if(Portal.Vars.productToShow != false)
		{
			this.switchToProduct(Portal.Vars.productToShow);
			Portal.Vars.productToShow = false;
		}
		else
		{
			this.switchToProduct('cloud');
			this.loadProductContent('cloud');
		}
		
		Portal.API.models.loadModel('plugins', PluginsClass);
	},
	
	clickHandler: function clickHandler(event)
	{
		var productKey = Event.element(event).id.replace('product_link_', '');
		this.switchToProduct(productKey);
	},
	
	switchToProduct: function switchToProduct(productKey)
	{
		if($('wizardCage'))
		{
			return;
		}
		
		/*
		if(this.currentProduct == productKey)
		{
			$('logo_' + this.currentProduct).setStyle(
	        {
				backgroundPosition: 'center -185px'
			});
			
			return;
		}
		
		if(Portal.Vars.switchingContent == true)
		{
			return;
		}
		*/
		var trackingId = 'ma.c.op';
		/*
		switch(productKey)
		{
			case 'studio':
				trackingId = 'ma.s.op';
				break;
			case 'cloud':
				trackingId = 'ma.c.op';
				break;
			case 'jaxer':
				trackingId = 'ma.j.op';
				break;
			case 'plugins':
				trackingId = 'ma.p.op';
				break;
			default:
				trackingId = '';
		}
		*/
		if(trackingId != '')
		{
			Portal.API.utils.sendTrackingData(trackingId);
		}
		/*
		var offset = this.products.get(productKey).offset;
		var moveBy = parseInt(offset - this.pointerPosition);
		
		this.pointerPosition = offset;
		this.currentProduct = productKey;
		
        new Effect.MoveBy('current_pointer', 0, moveBy, 
        {
            duration: 0.5,
			afterFinish: function()
			{
				Portal.Vars.switchingContent = false;
				
				if(!$('logo_' + this.currentProduct))
				{
					return;
				}
				
				$('logo_' + this.currentProduct).setStyle(
		        {
					backgroundPosition: 'center -185px'
				});
			}.bind(this),
			beforeStart: function()
			{
		        $$('div.logo_block').each(function(item)
		        {
		            $(item).setStyle(
		            {
						backgroundPosition: 'center top'
					});
		        });
				
				Portal.Vars.switchingContent = true;
			}.bind(this)
        });
		*/
		this.loadProductContent(productKey);
	},
	
	loadProductContent: function loadProductContent(product)
	{
		if(!product && $('product_content'))
		{
            new Effect.Appear('product_content', 
            {
                duration: 0.25,
                afterFinish: function()
                {
                    this.notify('switchedProduct', this.currentProduct);
                }.bind(this)
            });
			return;
		}
		
		if(!$('product_content'))
		{
			return;
		}
		
		Portal.Vars.currentHelpContent = 'My_Aptana_-_' + this.products.get(product).productName;
		this.currentHelpContent = Portal.Vars.currentHelpContent;
		
		var productContent = this.products.get(product).contentLocation;
		
        new Effect.Fade('product_content', 
        {
			duration: 0.25,
			afterFinish: function()
			{
                new Ajax.Updater('product_content', productContent, 
                {
					evalScripts: true,
					onComplete: function()
					{
						this.loadProductContent();
						new Effect.Fade('notify_' + product);
					}.bind(this)
				});
			}.bind(this)
		});
	},
	
	getNextProduct: function getNextProduct()
	{
		var productKeys = this.products.keys();
		var currentIndex = productKeys.indexOf(this.currentProduct);
		
		currentIndex ++;
		
		if(currentIndex >= productKeys.size())
		{
			return;
		}
		
		this.switchToProduct(productKeys[currentIndex]);
	},
	
	getPreviousProduct: function getPreviousProduct()
	{
		var productKeys = this.products.keys();
		var currentIndex = productKeys.indexOf(this.currentProduct);
		
		currentIndex --;
		
		if(currentIndex < 0)
		{
			return;
		}
		
		this.switchToProduct(productKeys[currentIndex]);
	}
});

Object.Event.extend(MyAptanaPortalObject);

Portal.API.tabs.registerTab('my_aptana', 1, 
{
	name: 'my_aptana',
	moduleName: 'MyAptanaPortal',
	moduleObj: MyAptanaPortalObject,
	isDefault: true,
	title: 'Hosting'
});
