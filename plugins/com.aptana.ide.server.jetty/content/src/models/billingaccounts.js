var BillingAccountClass = Class.create(
{
	initialize: function initialize(data)
	{
		this.id			= 0;
		this.user_id	= 0;
		this.name		= '';
		this.number		= '';
		this.expiry		= '';
		this.address1	= '';
		this.address2	= '';
		this.city		= '';
		this.state		= '';
		this.country	= '';
		this.zipcode	= '';
		this.phone		= '';
		this.sites		= [];
		this.hasExpired	= false;
		
		this.update(data)
	},
	
	update: function update(data)
	{
		if('id' in data)
		{
			this.id = data.id;
		}
		
		if('user_id' in data)
		{
			this.user_id = data.user_id;
		}
		
		if('name' in data)
		{
			this.name = data.name
		}
		
		if('number' in data)
		{
			this.number = data.number;
		}
		
		if('expiration' in data)
		{
			this.expiry = data.expiration;
		}
		
		if('address1' in data)
		{
			this.address1 = data.address1;
		}
		
		if('address2' in data)
		{
			this.address2 = data.address2;
		}
		
		if('city' in data)
		{
			this.city = data.city;
		}
		
		if('state' in data)
		{
			this.state = data.state;
		}
		
		if('zipcode' in data)
		{
			this.zipcode = data.zipcode;
		}
		
		if('country' in data)
		{
			this.country = data.country;
		}
		
		if('site_ids' in data)
		{
			this.sites = data.site_ids;
			
			/*
            this.sites.each(function(item)
            {
				if(!Portal.Data.siteList.sites.get(item.site_id))
				{
					this.sites = this.sites.without(item);
				}
            }.bind(this))
            */
		}
		
		this.checkIfExpired();
	},
	
	checkIfExpired: function checkIfExpired()
	{
		var now = new Date();
		now = now.getTime();
		
		var dateArray = this.expiry.split('/');
		var expiry = dateArray[0] + '/01/' + dateArray[1];

		expiry = Date.parse(expiry);
		
		if(expiry <= now)
		{
			this.hasExpired = true;
		}		
		else
		{
			this.hasExpired = false;
		}
	}
});
