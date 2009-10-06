<?php
	function abc()
	{
	}
?>
<?unknown this is an unknown processing instruction?>
<% this is an ASP-style tag %>
<html>
	<style type="text/css">
		body {
			background: red;
		}
	</style>
	<style type="text/javascript">
		document.body.style.background = "red";
	</style>
	<style type="unknown">
		this is an unknown style language
	</style>
	
	<script type="text/javascript">
		var x = 10;
		
		// comment
		/* multi-line comment */
		/** scriptdoc */
	</script>
	<script type="text/php">
		function def()
		{
		}
	</script>
	<script type="unknown">
		this is an unknown script language
	</script>
</html>
