<html>
  <head>
    <title>CO2 Database</title>
    <style type="text/css">
    table {
      text-align: center;
      border-collapse: collapse;
      border: 1px solid #d4d4d4;
    }

    tr:nth-child(even) {
      background: #d4d4d4;
    }

    th,
    td {
      padding: 10px 30px;
    }

    th {
      border-bottom: 1px solid #d4d4d4;
    }

    div.l_wrapper {
      max-width: 500px;
      margin: 0 auto;
      /*    background-color: antiquewhite;*/
      padding: 0 20px;
    }

    div.l_row {
      /*    background-color: yellow;*/
      /*    overflow: hidden;*/
      margin: 0 -10px;
    }

    div.l_col {
      /*    background-color: orange;*/
      float: left;
      /*        width: 50%;*/
      padding: 0 10px;
      box-sizing: border-box;
      margin-bottom: 20px;
    }
  </style>
  </head>
  <body>
    <h1 align="center">이산화탄소</h1>
        <?php
        $conn = mysqli_connect('relaxleep.ckbl1fdsxpkw.ap-northeast-2.rds.amazonaws.com','user','00000000','relaxleep');
        $result = mysqli_query($conn, "SELECT * FROM CO2_Table");
        
        echo '<div class="l_wrapper">';
        echo("<div class='l_row'>");
        echo("<div class='l_col'>");
        echo("<table>");
        echo("<tr><td>번호</td><td>시간</td><td>co2</td></tr>");
        while($row = mysqli_fetch_assoc($result)) 
        {
          for($i=0; $i<sizeof($result); $i++)
          {
            $count++;
            echo("<tr><td>".$count."</td><td>".$row['xTime']."</td><td>".$row['xValue']."</td></tr>");
            //이상징후알림
            if($row['value']>3000)
            {
              echo "<script type=\"text/javascript\">alert('환기가 필요합니다');</script>";
            }
          }
          
        }
        echo("</table>");
        echo("</div>");
        echo("</div>");
        echo("</div>");
        ?>

        <!--    echo("아래는 테스트");
           //테이블그리기코드
           //테스트해볼것
             echo("<table>");
             for($i=0; $i<sizeof($result); $i++)
             {
             $count++;
             echo("<tr><td>".$count."</td><td>".$row['Time']."</td><td>".$row['CO2']."</td></tr>");
             }
             echo("</table>");
        -->
  </body>
</html>