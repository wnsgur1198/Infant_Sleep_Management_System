import time
import pymysql  # to store DB
import serial   # to get sensor value
import sys

ser = serial.Serial('/dev/ttyUSB0', 9600)
#ser = None

#conn = pymysql.connect(host="relaxleep.ckbl1fdsxpkw.ap-northeast-2.rds.amazonaws.com",
                        user="user", passwd="00000000", db="relaxleepDB")
conn = None

#reset = True  # flag to reset table
reset = False


# Reset Table
if conn is not None:
    if reset:
        with conn.cursor() as cur:
            sql = ["truncate TemHum_Table", "truncate CO2_Table",
                   "truncate Sleep_Table", "truncate Illum_Table"]
            for i in sql:
                cur.execute(i)
                print('Delete ' + i)
            conn.commit()
            sys.exit()

while True:
    if ser is not None:
        rxdata = ser.readline().decode('utf-8')
        ser.flushInput()
        ser.flushOutput()

        # Illum, Sound, Tem, Hum, CO2
        if '!' in rxdata:
            sp1 = rxdata.split('!')
            sp2 = sp1[1].split(',')
            # sp3 = sp2[3].split('$')

            print("Illum : " + sp2[0])
            print("Sound : " + sp2[1])

            if sp2[2] != '0' and sp2[4] > '400':
                print("Tem : " + sp2[2])
                print("Hum : " + sp2[3])
                print("CO2 : " + sp2[4])
                # print("Sound : " + sp3[0])

                if conn is not None:
                    with conn.cursor() as cur:
                        sql1 = "insert into Illum_Table values(%s,%s)"
                        sql2 = "insert into Sleep_Table values(%s,%s)"
                        sql3 = "insert into TemHum_Table values(%s,%s,%s)"
                        sql4 = "insert into CO2_Table values(%s,%s)"

                        xtime = time.strftime("%Y-%m-%d-%H:%M", time.localtime())

                        illum = int(round(float(sp2[0])))
                        tem = int(round(float(sp2[2])))
                        hum = int(round(float(sp2[3])))
                        co2 = int(round(float(sp2[4])))

                        cur.execute(sql1, (xtime,illum))
                        cur.execute(sql2, (xtime,1))
                        cur.execute(sql3, (xtime,tem,hum))
                        cur.execute(sql4, (xtime,co2))
                        conn.commit()

            if '$' in rxdata:
                time.sleep(5)

        else:
            print('No Connected Arduino')


