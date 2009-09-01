var RecentFilesModule = Class.create(
{
	initialize: function initialize()
	{
		this.templates	= {};
		this.fileList	= new Hash();
	},
	
	finishInit: function finishInti()
	{
		this.registerObservers();
		this.extendSelf();
		this._init();
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		if(msg.data.response == 'listRecentFiles')
		{
			this.updateFileList(msg.data);
		}
	},
	
	registerObservers: function registerObservers()
	{
		EventManager.subscribe('/portal/recentFiles', { channelHook: 'RecentFiles' });
	},
	
	extendSelf: function extendSelf()
	{
		Object.extend(this, RecentFilesControllers);
		Object.extend(this, RecentFilesViews);
	}
});

var RecentFilesControllers =
{
	_init: function _init()
	{
		this._initViews();
	},
	
	fetchFileList: function fetchFileList()
	{
        EventManager.publish('/portal/recentFiles', 
        {
            request: 'listRecentFiles'
        });
	},
	
	updateFileList: function updateFileList(data)
	{
		this.fileList = new Hash();
		
        data.projects.each(function(item)
        {
			var files = [];
			
			if('files' in item)
			{
                item.files.each(function(file)
                {
					files.push(file.name);
                });
			}
			
			this.fileList.set(item.name, files);
        }.bind(this))
		
		this.templates.main.set('fileList', this.fileList);
	},
	
	newProject: function newProject()
	{
		EventManager.publish('/portal/wizard', 
        {
            id: 'com.aptana.ide.wizards.WebProjectWizard',
            request: 'new'
        });
	},
	
	newHostedProject: function newHostedProject()
	{
		EventManager.publish('/portal/wizards/newsite', {});
	},
	
	openProject: function openProject(name)
    {
        EventManager.publish('/portal/projects/show', 
        {
            project: name
        })
    },
	
	openFile: function openFile(project, file)
	{
        EventManager.publish('/portal/recentFiles', 
        {
			request: 'openFile',
			project: project,
			file: file
		});
	},
	
	newFile: function newFile(type)
	{
        EventManager.publish('/portal/files/new', type);
	},
	
	browseSamples: function browseSamples()
	{
		EventManager.publish('/portal/samples', { request: 'showSamplesView' });
	}
}

var RecentFilesViews = 
{
	_initViews: function _initViews()
	{
        View.load(
        {
			main:
			{
				file: 'templates/recentfiles/recentfiles.html',
				binding: function()
				{
					return $('recentFilesCage');
				}
			}
		}, 
		function(templates)
        {
			this.templates = templates;
			
			this.fetchFileList();
        }.bind(this));
	}
}
