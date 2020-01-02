<html>
  <head>
    <title>Tempul Database</title>
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
    <h1 align="center">체온</h1>
        <?php
        $conn = mysqli_connect('relaxleep.ckbl1fdsxpkw.ap-northeast-2.rds.amazonaws.com','user','00000000','relaxleep');
        $result = mysqli_query($conn, "SELECT * FROM Bodytemp_Table");
        
        echo '<div class="l_wrapper">';
        echo("<div class='l_row'>");
        echo("<div class='l_col'>");
        echo("<table>");
        echo("<tr><td>번호</td><td>시간</td><td>체온</td></tr>");
        while($row = mysqli_fetch_assoc($result)) 
        {
          for($i=0; $i<sizeof($result); $i++)
          {
            $count++;
            echo("<tr><td>".$count."</td><td>".$row['xTime']."</td><td>".$row['Temperature']."</td></tr>");
            // 이상알림
            if($row['Temperature']>39)
            {
              echo "<script type=\"text/javascript\">alert('체온 높습니다');</script>";
            }
            if($row['Temperature']<35)
            {
              echo "<script type=\"text/javascript\">alert('체온 낮습니다');</script>";
            }
          }
        }
        echo("</table>");
        echo("</div>");
        echo("</div>");
        echo("</div>");
        ?>
  </body>
</html>