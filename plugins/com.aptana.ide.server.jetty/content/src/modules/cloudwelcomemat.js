var CloudWelcomeMatModule = Class.create(
{
	initialize: function initialize()
	{
		this.templates		= false;
		this.samples		= false;
		this.samplesList	= new Hash();
		this.loadingWizard	= false;
	},
	
	finishInit: function finishInit()
	{
		this.extendSelf();
		this.loadTemplates();
		this.registerObservers();
	},
	
	dispatchEvent: function dispatchEvent(msg)
	{
		this.updateSamples(msg);
	},
	
	registerObservers: function registerObservers()
	{
		// log in / out changes the whole shell template
		Portal.Modules.AptanaIdManager.observe(['userLoggedIn', 'userLoggedOut'], function()
        {
			var data = 
            {
                isLoggedIn: 		Portal.Modules.AptanaIdManager.isLoggedIn,
                isOnline:			Portal.Modules.OnlineStatus.isOnline
            }
            
            this.templates.main.set('data', data);
			
        }.bind(this));
		
		// online / offline mode changes the template
		Portal.Modules.OnlineStatus.observe('onlineStatusChanged', function()
        {
			var data = 
            {
                isLoggedIn: 		Portal.Modules.AptanaIdManager.isLoggedIn,
                isOnline:			Portal.Modules.OnlineStatus.isOnline
            }
            
            this.templates.main.set('data', data);
			
        }.bind(this));
		
		// updates to the project list
		Portal.Data.cloudProjects.observe(['projectListUpdated'], function()
        {
            this.templates.projectList.set('data', 
            {
                totalDeployed: Portal.Data.cloudProjects.totalDeployed,
                totalProjects: Portal.Data.cloudProjects.totalProjects,
                undeployedProjects: Portal.Data.cloudProjects.undeployedProjects,
                deployedProjects: Portal.Data.cloudProjects.deployedProjects,
                samples: this.samplesList
            });
        }.bind(this));
		
		// updates to the site list
		Portal.Data.siteList.observe(['siteAdded', 'siteStatusChanged', 'siteUpdated', 'siteModelLoaded'], function() 
		{
			var data = 
            {
                isLoggedIn: 		Portal.Modules.AptanaIdManager.isLoggedIn,
                isOnline:			Portal.Modules.OnlineStatus.isOnline
            }
            
            // this.templates.main.set('data', data);
			
			if($('projectlist_cage') && $('projectlist_cage').innerHTML == '')
			{
				this.templates.projectList.set('data', 
	            {
	                totalDeployed: Portal.Data.cloudProjects.totalDeployed,
	                totalProjects: Portal.Data.cloudProjects.totalProjects,
	                undeployedProjects: Portal.Data.cloudProjects.undeployedProjects,
	                deployedProjects: Portal.Data.cloudProjects.deployedProjects,
	                samples: this.samplesList
	            });
			}
			
			if($('cloud_mat_sitelist'))
			{
				this.templates.siteList.render();
			}
		}.bind(this));
		
        EventManager.subscribe('/portal/cloud/samples/', 
        {
            channelHook: 'CloudWelcomeMat',
            onComplete: function()
            {
				this._init();
            }.bind(this)
        });
	},
	
	extendSelf: function extendSelf()
	{
		Object.extend(this, CloudWelcomeMatControllers);
		Object.extend(this, CloudWelcomeMatViews);
	}
});

var CloudWelcomeMatControllers = 
{
	_init: function _init()
	{
		if(!this.samples)
		{
			this.fetchSamples();
		}
		else
		{
			this._initViews();
		}
	},
	
	fetchSamples: function fetchSamples()
	{
		EventManager.publish('/portal/cloud/samples', { request: 'listSamples' });
	},
	
	updateSamples: function updateSamples(msg)
	{
		this.samples = true;
		this.samplesList = new Hash();
		
        msg.data.sample.each(function(sample)
        {
			this.samplesList.set(sample.name, sample);
        }.bind(this));
		
		this._initViews();
	},
	
	showSiteInCloud: function showSiteInCloud(siteId)
	{
		Portal.Vars.siteToLoad = siteId;
		Portal.API.tabs.loadTabManually('tab_my_cloud');
	},
	
	showWizard: function showWizard(project)
	{
		if(project == '---')
		{
			return;
		}
		
		this.loadingWizard = true;
		
		$('deploy_project_button').hide();
		
		if(project.substr(0,1) == '0')
		{
			project = project.substr(1);
			var projectType = Portal.Data.cloudProjects.undeployedProjects.get(project).type;
			
			$('loading_wizard_cage').update('<img src="images_global/img_activity.gif" align="absmiddle" class="inline" /> Loading wizard...').show();
			
			// show the wizard
			Portal.Modules.DeploymentWizard.showWizard( { data : { projectName: project }} );
		}
		else
		{
			$('loading_wizard_cage').update('<img src="images_global/img_activity.gif" align="absmiddle" class="inline" /> Importing project and loading wizard...').show();
			
			project = project.substr(1);
			
			sampleProject = this.samplesList.get(project);
			
            EventManager.publish('/portal/cloud/samples', 
            {
                request: 'importSampleProject',
                name: project,
                file: sampleProject.file
            });
		}
		
	},
	
	newProject: function newProject()
	{
		EventManager.publish('/portal/wizard', 
        {
            id: 'com.aptana.ide.wizards.WebProjectWizard',
            request: 'new'
        });
	}
}

var CloudWelcomeMatViews = 
{
	_initViews: function _initViews()
	{
		this.loadTemplates();
	},
	
	loadTemplates: function loadTemplates()
	{
        View.load(
        {
			main:
			{
				file: 'templates/cloud_welcome/cloud.html',
				binding: function()
				{
					return $('cloud_mat_content');
				}
			},
			
			siteList:
			{
				file: 'templates/cloud_welcome/site_list.html',
				binding: function()
				{
					return $('cloud_mat_sitelist');
				}
			},
			
			projectList:
			{
				file: 'templates/cloud_welcome/project_list.html',
				binding: function()
				{
					return $('projectlist_cage');
				},
				scope:
				{
					data:
					{
						totalDeployed:		Portal.Data.cloudProjects.totalDeployed,
						totalProjects:		Portal.Data.cloudProjects.totalProjects,
						undeployedProjects:	Portal.Data.cloudProjects.undeployedProjects,
						deployedProjects:	Portal.Data.cloudProjects.deployedProjects,
						samples:			this.samplesList
					}	
				}
			}
		}, 
		function(templates)
        {
			
			this.templates 				= {};
            this.templates.main 		= templates.main;
			this.templates.siteList		= templates.siteList;
			this.templates.projectList	= templates.projectList;
            
            var data = 
            {
                isLoggedIn: 		Portal.Modules.AptanaIdManager.isLoggedIn,
                isOnline:			Portal.Modules.OnlineStatus.isOnline
            }
            
            this.templates.main.set('data', data);
			
			if($('cloud_mat_sitelist'))
			{
				this.templates.siteList.render();
			}
			
			if(Portal.Modules.OnlineStatus.isOnline)
			{
				this.templates.projectList.render();
			}
            
        }.bind(this));
	}
}
