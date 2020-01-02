<!DOCTYPE HTML>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <!-- <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; style-src 'self'; ">
        <meta http-equiv="Content-Security-Policy" content="default-src *; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline' 'unsafe-eval' [*.URL]"> -->
        <title>CO2 Example</title>

        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
        <style type="text/css">
        ${demo.css}
        </style>
        
    </head>
    <body>
        <script src="../javascripts/highcharts.js"></script>
        <script src="../javascripts/modules/exporting.js"></script>
        <?php
        $mysql_host = 'relaxleep.ckbl1fdsxpkw.ap-northeast-2.rds.amazonaws.com';
        $mysql_user = 'user';
        $mysql_password = '00000000';
        $mysql_db = 'relaxleep';

        $conn = mysqli_connect( $mysql_host, $mysql_user, $mysql_password, $mysql_db );
        if ( conn == false ) {
            echo "<p>Failure</p>";
        } else {
            echo "<p>Success</p>";
        }

        $str_Time="";
        $str_Value="";
        $result = mysqli_query($conn, "SELECT * FROM PulO2_Table");

        while($row = mysqli_fetch_assoc($result)) 
        {

            $str_Time .="'".$row['xTime']."',";
            $str_Value .="'".$row['Pulse']."',";

            if ($row['Pulse']<50)
            {
                echo "<script type=\"text/javascript\">alert('심박수가 낮습니다');</script>";
            }
            if ($row['Pulse']>150)
            {
                echo "<script type=\"text/javascript\">alert('심박수가 높습니다');</script>";
            }
           
        }
        $str_Time= substr($str_Time,0,-1);
        $str_Value= substr($str_Value,0,-1);
        $str_Value = str_replace('\'','',$str_Value);
        ?>
        <script type="text/javascript">
        $(function () {
            $('#container').highcharts({
                chart: {
                    type: 'line'
                },
                title: {
                    text: 'Average Pulse'
                },
                subtitle: {
                    text: 'Relaxleep'
                },
                xAxis: {
                    categories: [<?php echo $str_Time?>]
                },
                yAxis: {
                    title: {
                        text: 'Pulse (bpm)'
                    }
                },
                plotOptions: {
                    line: {
                        dataLabels: {
                            enabled: true
                        },
                        enableMouseTracking: false
                    }
                },
                series: [
                    {
                        name: 'bpm',
                        data: [<?php echo $str_Value?>]
                    }
            ]
            });
        });
        </script>
        <div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
    </body>
</html>
	