/*
 * Parse the data and create a graph with the data.
 */
function parseData(createGraph) {
	Papa.parse("../data/th.csv", {
		download: true,
		complete: function(results) {
			createGraph(results.data);
		}
	});
}

function createGraph(data) {
	var time = [];
	var temp = ["Temperature"];
	var hum = ["Humidity"];

	for (var i = 1; i < data.length; i++) {
		if(data[i][0] === undefined){
			time.push(null);
		} else if(data[i][1] === undefined){
			temp.push(null);
		} else if(data[i][2] === undefined){
			hum.push(null);
		} else {
			time.push(data[i][0]);
			temp.push(data[i][1]);
			hum.push(data[i][2]);
		}
	}

	var chart = c3.generate({
		bindto: '#chart',
	    data: {
	        columns: [
						temp,
						hum
	        ]
	    },
	    axis: {
	        x: {
	            type: 'category',
	            categories: time,
	            tick: {
	            	multiline: false,
                	culling: {
                    	max: 15
                	}
            	}
	        }
	    },
	    zoom: {
        	enabled: true
    	},
	    legend: {
	        position: 'right'
	    }
	});
}

parseData(createGraph);
