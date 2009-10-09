Object.extend(Array.prototype, 
	{
	  _each: function(iterator) 
	  {
	    for (var i = 0; i < this.length; i++)
	      iterator(this[i]);
	  }
	});
	

	
Math = {};
document.write(Math.sin(5)); // Math.sin doesn't exist

Function = {};			
function foo()
{				
}
document.write(foo.call); // foo.call does exist