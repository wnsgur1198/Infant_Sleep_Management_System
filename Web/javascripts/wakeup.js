/*
 * Parse the data and create a graph with the data.
 */
function parseData(createGraph) {
	Papa.parse("../data/wakeup.csv", {
		download: true,
		complete: function(results) {
			createGraph(results.data);
		}
	});
}

function createGraph(data) {
	var time = [];
	var wakeup = ["WakeUp"];

	for (var i = 1; i < data.length; i++) {
		if(data[i][0] === undefined){
			time.push(null);
		} else if(data[i][1] === undefined){
			wakeup.push(null);
		}  else {
			time.push(data[i][0]);
			wakeup.push(data[i][1]);
		}
	}

	console.log(time);
	console.log(wakeup);

	var chart = c3.generate({
		bindto: '#chart',
	    data: {
	        columns: [
						wakeup
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
