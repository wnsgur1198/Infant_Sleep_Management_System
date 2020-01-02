<?php
    var data = JSON.parse(json);

    while($aRow = mysqli_fetch_assoc($oRes)){
      $aData[] = array("y" => $aRow['number'], "name" => $aRow['string']);
   }
   exit(json_encode($aData));

   oGraph = new Highcharts.chart({
    chart: {
       renderTo: 'graphid',
    ... [code] ...
    plotOptions: {
       pie: {
    ... [code] ...
    series: [{
       name: 'MyName',
       data: []
    ... [code] ...
});

$.ajax({
    type     : 'post',
    url      : 'http://54.180.153.11/charthtml/setJSON.php',
    dataType : 'json'
 }).done(function( oJson ) {
    var oObj = [];
    $.each(oJson, function(ix,sliceData) {
       oObj.push({
          name: sliceData.name,
          y: eval(sliceData.y)
       });
    });
    oGraph.series[0].setData(oObj, true);
 });
?>