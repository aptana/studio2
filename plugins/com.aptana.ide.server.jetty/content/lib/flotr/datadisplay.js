/*
Dashalytics: A Google Analytics Widget for Mac OSX
Copyright (C) 2006  Robert Scriva (dashalytics@rovingrob.com)

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

function drawBigGraphs (data) {
	bigGraphElements.each(function(item) {
		drawBigGraph(item, data);
	});
}

function drawBigGraph(e, graphdata) {

	$(e + '-graph-tips').immediateDescendants().each(function(item) {
		item.remove();
	});

	$(e + '-subtitle').innerHTML = graphdata['title'].Name + ' - ' + graphdata.daterange;
	$(e + '-narrative').innerHTML = graphdata.narrative.Message;

	// Get total number of columns
	var totalCols = graphdata.data.size();

	// Get maximum/minimum values
	var maxValue = graphdata.data.max(function(col) {
		return parseInt(removeCommas(col.Value));
	});
	
//	maxValue = parseFloat(removeCommas(graphdata.yaxislabel[1]));
	
	// Get the canvas
	var canvas = $(e + '-canvas');

	// use getContext to use the canvas for drawing
	var ctx = canvas.getContext('2d');

	var canvas_width = canvas.getWidth();
	var canvas_height = canvas.getHeight();
	ctx.clearRect(0, 0, canvas_width, canvas_height);

	ctx.strokeStyle = "rgba(254, 254, 254, 0.20)";
	
	// create y-axis values
	var y_axis_div = document.createElement('div');
	$(e + '-graph-tips').appendChild(y_axis_div);	
	y_axis_div.addClassName('graph-axis');
	var axis_xpos_calc = 0;
	var axis_ypos_calc = (canvas_height - 5) - ((canvas_height - 20));
	var axis_xpos = axis_xpos_calc + 'px';
	var axis_ypos = (axis_ypos_calc + 2) + 'px';
	y_axis_div.setStyle({left : axis_xpos, top: axis_ypos});
	y_axis_div.innerHTML = maxValue.numberFormat("#,#");

	// draw y axis line
	ctx.beginPath();
	ctx.lineWidth = 1;
	ctx.moveTo(0,axis_ypos_calc);
	ctx.lineTo(canvas_width, axis_ypos_calc);
	ctx.closePath();
	ctx.stroke();

	y_axis_div = document.createElement('div');
	$(e + '-graph-tips').appendChild(y_axis_div);	
	y_axis_div.addClassName('graph-axis');
	axis_xpos_calc = 0;
	axis_ypos_calc = (canvas_height - 5) - ((1 / 2) * (canvas_height - 20));
	axis_xpos = axis_xpos_calc + 'px';
	axis_ypos = (axis_ypos_calc + 2) + 'px';
	y_axis_div.setStyle({left : axis_xpos, top: axis_ypos});
	y_axis_div.innerHTML = parseInt(maxValue / 2).numberFormat("#,#");

	// draw y axis line
	ctx.beginPath();
	ctx.lineWidth = 1;
	ctx.moveTo(0,axis_ypos_calc);
	ctx.lineTo(canvas_width, axis_ypos_calc);
	ctx.closePath();
	ctx.stroke();

	ctx.fillStyle = "rgba(255, 255, 255, 0.25)";
	ctx.strokeStyle = "rgb(255,255,255)";

	ctx.beginPath();
	ctx.lineWidth = 2;
	ctx.lineCap = 'round';

	var i = 1;
	var xplot;
	var yplot;
	graphdata.data.each(function(item) {
//		item.PrimaryValue = removeCommas(item.PrimaryValue);
		xplot = ( i / (totalCols)) * (canvas_width - 10);
		yplot = (canvas_height - 5) - ((parseInt(removeCommas(item.Value)) / maxValue) * (canvas_height - 20));

		if (i == 1) {
			ctx.moveTo(xplot, canvas_height - 2);
			ctx.lineTo(xplot, yplot);
		} else {
			ctx.lineTo(xplot, yplot);
		}
		i++;
	});
	ctx.lineTo(xplot, canvas_height - 2);
	ctx.closePath();
	ctx.fill();

	ctx.beginPath();
	i = 1;
	graphdata.data.each(function(item) {
//		item.PrimaryValue = removeCommas(item.PrimaryValue);
		xplot = ( i / (totalCols)) * (canvas_width - 10);
		yplot = (canvas_height - 5) - ((parseInt(removeCommas(item.Value)) / maxValue) * (canvas_height - 20));

		var my_div = document.createElement('div');
		$(e + '-graph-tips').appendChild(my_div);	
		my_div.addClassName('graph-tip');
		var xpos = xplot - 2 + 'px';
		var ypos = yplot - 2 + 'px';
		my_div.setStyle({left : xpos, top: ypos});
		tooltipText = item.Label + '<br/>' + item.Value;
		new Effect.Tooltip(my_div, tooltipText, {className: 'tip'});

		if (i == 1) {
			ctx.moveTo(xplot, yplot);
		} else {
			ctx.lineTo(xplot, yplot);
		}
		i++;
	});
	ctx.stroke();
	

}

function drawSparklines (data, dataselector, dataformats) {
	var section2Element = $('section2');
	section2Element.immediateDescendants().each(function(item) {
		item.remove();
	});	

	var i = 0;
	data.each(function(item) {
		if (dataselector[i]) {
			var spark_div = document.createElement('div');
			spark_div.addClassName('visitor-spark');
			section2Element.appendChild(spark_div);
			drawSparkline(spark_div, item, dataformats[i]);
		};
		i++;
	});
}

function drawSparkline(e, data, numformat) {
	// Create spark summary
	var summary_span = document.createElement('span');
	summary_span.addClassName('spark-summary');
	e.appendChild(summary_span);

	// Create break
	var spark_br = document.createElement('br');
	e.appendChild(spark_br);

	// Create spark canvas
	var spark_canvas = document.createElement('canvas').show();
	spark_canvas.addClassName('spark-canvas');
	e.appendChild(spark_canvas);

	// Create spark hi value
	var hi_span = document.createElement('span');
	hi_span.addClassName('spark-hi');
	e.appendChild(hi_span);

	// Create spark low value
	var low_span = document.createElement('span');
	low_span.addClassName('spark-low');
	e.appendChild(low_span);

	summary_span.innerHTML = data.summary.SummaryValue + ' ' + data.summary.Message;
	
	var objGraph = data;
	// Get total number of columns
	var totalCols = objGraph.data.size();

	// Get maximum/minimum values
	var maxValue = objGraph.data.max(function(col) {
		return parseFloat(col);
	});
	var minValue = objGraph.data.min(function(col) {
		return parseFloat(col);
	});

	hi_span.innerHTML = maxValue.numberFormat(numformat);
	low_span.innerHTML = minValue.numberFormat(numformat);
		
	// Get the canvas
//	var canvas = spark_canvas;

	// use getContext to use the canvas for drawing
	var ctx = spark_canvas.getContext('2d');
	ctx.fillStyle = "rgba(55, 55, 55, 0.75)";
	ctx.strokeStyle = "black";
	
	var canvas_height = spark_canvas.getHeight();
	var canvas_width = spark_canvas.getWidth();
	ctx.clearRect(0, 0, canvas_width, canvas_height);

	// Stroked triangle
	ctx.lineWidth = 1;
	ctx.lineCap = 'round';

	ctx.beginPath();

	// create the fill path
	var i = 1;
	var xplot;
	var yplot;
	objGraph.data.each(function(item) {
		xplot = ( i / (totalCols)) * (canvas_width - 5);
		yplot = canvas_height - 5 - ( (parseFloat(item) / maxValue) * (canvas_height - 10));

		if (i == 1) {
			ctx.moveTo(xplot, canvas_height -3);
			ctx.lineTo(xplot, yplot);
		} else {
			ctx.lineTo(xplot, yplot);
		}

		i++;
	});
	ctx.lineTo(xplot, canvas_height - 3);
	ctx.closePath();
	ctx.fill();

	// create the plot line
	ctx.beginPath();
	i = 1;
	objGraph.data.each(function(item) {
		xplot = ( i / (totalCols)) * (canvas_width - 5);
		yplot = canvas_height - 5 - ( (parseFloat(item) / maxValue) * (canvas_height - 10));

		if (i == 1) {
			ctx.moveTo(xplot, yplot);
		} else {
			ctx.lineTo(xplot, yplot);
		}
		i++;
	});
	ctx.stroke();
	
	
}

function drawTables (data) {
	var section3Element = $('section3');
	section3Element.immediateDescendants().each(function(item) {
		item.remove();
	});

	data.each(function(item) {
		tble = document.createElement('table');
		tble.addClassName('display-table');
		drawTable(tble, item);
		section3Element.appendChild(tble);
	});

}

function drawTable(e, tabledata) {
	var tableElement = e;

	var trhead = document.createElement('tr');
	tableElement.appendChild(trhead);

	var tdhead = document.createElement('th');
	tdhead.innerHTML = tabledata.keycolname;
	tdhead.addClassName(siteReport + '-col1');
	trhead.appendChild(tdhead);

	tdhead = document.createElement('th')
	tdhead.innerHTML = tabledata.colname[1];
	tdhead.addClassName('coldata');
	trhead.appendChild(tdhead);

	tabledata.data.each(function(itemhead) {

		var trdata = document.createElement('tr');
		tableElement.appendChild(trdata);

		var tddata = document.createElement('td');
		tddata.innerHTML = itemhead.Key;
		tddata.addClassName('col1');
		trdata.appendChild(tddata);

		tddata = document.createElement('td');
		tddata.innerHTML = itemhead.Value[1];
		tddata.title = itemhead.Value[0];
		tddata.addClassName('coldata');
		trdata.appendChild(tddata);
	});
}
