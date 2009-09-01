var ProjectListModule = Class.create(
{
    initialize: function()
    {
        this.firstLoad = true;
    },
    
    finishInit: function()
    {
        Portal.Data.projects.observe('update', function()
        {
            this.showProjectDrop();
        }.bind(this));
		
		this.showProjectDrop();
    },
	
    /**
     * Parses the project drop based
     *
     * @param {Object} msg
     */
    showProjectDrop: function()
    {
        if (Portal.Data.projects.loading) 
        {
            $('projectListCage').update('<div class="activity">Loading Projects...</div>');
            return;
        }
        
        var projects = '';
        
        if (Portal.Data.projects.totalProjects == 0) 
        {
            Portal.API.utils.setContent('projectListCage', noProjectsDropTemplate.evaluate(
            {}));
            return;
        }
        
       Portal.Data.projects.projectList.each(function(item)
        {
            projects += projectOptionTemplate.evaluate(
            {
                project: item.value.name
            })
        });
        
        if (!$('projectListCage')) 
        {
            return;
        }
        
        if (this.firstLoad == true) 
        {
            new Effect.Fade('projectListCage', 
            {
                duration: 0.25,
                afterFinish: function()
                {
                    Portal.API.utils.setContent('projectListCage', ProjectListTemplate.evaluate(
                    {
                        projectOptions: projects,
                        openProject: 'javascript: Portal.Modules.ProjectList.open();'
                    }));
                    
                    if (!$('projectListCage')) 
                    {
                        return;
                    }
                    
                    new Effect.Appear('projectListCage', 
                    {
                        duration: 0.25
                    });
					
					this.firstLoad = false;
                }.bind(this)
            });
            
            
        }
        else 
        {
            Portal.API.utils.setContent('projectListCage', ProjectListTemplate.evaluate(
            {
                projectOptions: projects,
                openProject: 'javascript: Portal.Modules.ProjectList.open();'
            }));
        }
    },
    
    open: function()
    {
        EventManager.publish('/portal/projects/show', 
        {
            project: $F('projectList')
        })
    },
    
    /**
     * Opens the new project wizard
     */
    newProject: function()
    {
        EventManager.publish('/portal/wizard', 
        {
            id: 'com.aptana.ide.wizards.WebProjectWizard',
            request: 'new'
        });
    }
});
