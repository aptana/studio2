var View = Class.create({
	initialize: function(text){
		
		this.id = new String(View.i);
		++View.i;
        
        if (typeof(text) == 'string') 
        {
            this.text = text;
            this.scope = new Hash({});
            this.binding = null;
            this.behaviors = null;
        }
        else 
        {
            this.text = text.text;
            this.scope = text.scope || {};
            this.binding = text.binding || null;
            this.behaviors = text.behaviors || null;
        }
		
		if(!this.scope._object)
		{
			this.scope = new Hash(this.scope);
		}
		
		Object.Event.extend(this.scope);
		
		this.scope.makeObservable('set');
		
		this.scope.observe('set',function(key,value)
		{
			this.render();
		}.bind(this));
		
		this.ejs = new EJS(
		{
			text: this.text
		});
	},
	render: function(scope)
	{
		var final_scope = (this.scope.toObject()) ? this.scope.toObject() : this.scope;
		
		if(scope)
		{
			if(scope.toObject)
			{
				scope = scope.toObject();
				scope.each(function(pair)
				{
					final_scope.set(pair.key,pair.vaule);
				});
			}
			else
			{
				Object.extend(final_scope, scope || {});
			}
		}
		
		final_scope.partial = function(view, partial_scope) 
		{
			return view.render(partial_scope || {});
			
		}.bind(this);
		
		
		var response = this.ejs.render(final_scope);
		
		if(this.binding)
		{
			var element = $($value(this.binding));
            
			if(!element)
			{
				return;
			}
			
            if (this.behaviors) 
            {
                var purge_functions = function(object)
                {
                    var attributes = object.attributes;
                    for (var i = 0; i < attributes.length; ++i) 
                    {
                        if (typeof(object[attributes[i].name]) == 'function') 
                        {
                            object[attributes[i].name] = null;
                        }
                    }
                };
                var all_elements = element.select('*');
                all_elements.invoke('stopObserving');
                all_elements.each(function(element)
                {
                    purge_functions(element);
                });
            }
			
			element.update(response);
			
			this.notify('render',element,this.id);
			
			if(this.behaviors)
			{
				this.behaviors(element,final_scope);
			}
		}
		else
		{
			this.notify('render',response,this.id);
		}
		
		return response;
	},
	set: function(key,value)
	{
		if(this.scope)
		{
			return this.scope.set(key,value);
		}
	},
	get: function(key)
	{
		if (this.scope) 
		{
			return this.scope.get(key);
		}
	}
});



Object.extend(View,
{
	i: 0,
	load: function(templates,proceed)
	{
		if (typeof(templates.keys) == 'undefined') 
		{
			templates = $H(templates);
		}
		
		var total_number_of_templates = templates.keys().length;
		var number_of_templates_loaded = 0;
		
		var process_template = function(template,request)
		{
			if (request) 
			{
				template.value.text = request.responseText;
			}
			
            try 
            {
				if(!Portal.Vars.totalViews)
				{
					Portal.Vars.totalViews = 0;
				}
				
                templates.set(template.key, new View(template.value));
				Portal.Vars.totalViews ++;
            } 
            catch (e) 
            {
                console.warn(e)
            }
			
			++number_of_templates_loaded;
			
			
			if (number_of_templates_loaded == total_number_of_templates) 
			{
				proceed(templates.toObject());
			}
		};
		templates.each(function(template)
		{
			if(typeof(template.value) == 'string')
			{
				template.value = 
				{
					text: template
				};
			}
			
			if (template.value.file) 
			{
				new Ajax.Request(template.value.file, 
				{
					onComplete: process_template.curry(template)
				});
			}
			else 
			{
				process_template(template)
			}
		});
	}
});
Object.Event.extend(View);

EJS.prototype.render = function(object,mixin)
{
	mixin = mixin || {};
	var v = new EjsView(object);
	
	for(var method in mixin)
	{
		var underscore = method.underscore();
		
		if (!v[underscore]) 
		{
			v[underscore] = mixin[method];
		}
		
		if (!v[method]) 
		{
			v[method] = mixin[method];
		}
	}
	
	try
	{
		var response = this.template.process.call(v,object,v);
	}
	catch(e)
	{
		console.warn(e);
	}
	
	return response;
};