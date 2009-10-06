myExtends = function(child, base)
{
	child.prototype = new base();
}

parent = function(){};
child = function(){};

myExtends(child, parent);




Object.extend(Enumerable, {
  map:     Enumerable.collect,
  find:    Enumerable.detect,
  select:  Enumerable.findAll,
  member:  Enumerable.include,
  entries: Enumerable.toArray
});