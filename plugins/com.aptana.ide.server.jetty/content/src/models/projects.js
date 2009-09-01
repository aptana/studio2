var ProjectsClass = Class.create(
{
	initialize: function initialize()
	{
		this.loading 			= true;
		this.projectList		= new Hash();
		this.totalProjects		= 0;
	},
	
	finishInit: function finishInit()
	{
        EventManager.subscribe('/portal/projects', 
        {
            channelHook: 'projects',
            onComplete: function()
            {
                this.requestProjectsList()
            }.bind(this)
        });
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		if(msg.data.response == 'listProjects')
		{
			this.updateProjectList(msg);
		}
	},
	
	requestProjectsList: function requestProjectsList()
	{
        EventManager.publish('/portal/projects', 
        {
            request: 'listProjects'
        });
	},
	
	updateProjectList: function updateProjectList(msg)
	{
		this.projectList	= new Hash();
		var sortArray		= [];
		var tempHash		= new Hash();
		
		this.totalProjects = 0;
		
		msg.data.projects.each(function(item)
		{
            tempHash.set(item.name, 
            {
                name: item.name,
                type: item.type,
                deployed: item.deployed,
                siteId: item.siteId
            });
			
			this.totalProjects ++;
			
			sortArray.push(item.name);
		}.bind(this));
		
		sortArray.sort(function(x, y)
        {
            var a = String(x).toUpperCase();
            var b = String(y).toUpperCase();
            
            if (a > b) 
            {
                return 1;
            }
            
            if (a < b) 
            {
                return -1;
            }
            
            return 0;
        });
		
        sortArray.each(function(item)
        {
			var project = new Project(tempHash.get(item));
			this.projectList.set(project.name, project);
        }.bind(this));
		
		
		if(this.loading == true)
		{
			this.loading = false;
			this.notify('startupHookComplete');
		}
		
		this.notify('update');
	}
});
Object.Event.extend(ProjectsClass);
Portal.API.startup.registerStartupItem(3, 
{
	startupMessage: 'Loading projects...',
	onInit: function()
	{
		Portal.API.models.loadModel('projects', ProjectsClass, true);
	}
});

var Project = Class.create(
{
	initialize: function initialize(data)
	{
		this.name		= '';
		this.type		= 'unknown';
		
		this.update(data);
	},
	
	update: function update(data)
	{
		if('name' in data)
		{
			this.name = data.name;
		}
		if('type' in data)
		{
			this.type = data.type;
		}
	}
})
