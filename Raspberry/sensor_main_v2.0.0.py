import time
import serial   # to get sensor value
import sys
import os
import subprocess
import pygame
from pygame.locals import *

pygame.init()
ser = serial.Serial('/dev/ttyUSB0', 9600)
#ser = None

conn = None

#reset = True  # flag to reset table
reset = False

while True:
    if ser is not None:
        rxdata = ser.readline().decode('utf-8')
        ser.flushInput()
        ser.flushOutput()

        # Lux, Sound, Tem, Hum, CO2
        if '!' in rxdata:
            sp1 = rxdata.split('!')
            #print(sp1[1])
            sp2 = sp1[1].split(',')
            print("Illum : " + sp2[0])
            print("Sound : " + sp2[1])
            sp2[1] = float(sp2[1])
            print("Tem : " + sp2[2])
            print("Hum : " + sp2[3])
            print("CO2 : " + sp2[4])
            
            
            if sp2[1] > 1.6:
                pygame.mixer.init()
                pygame.mixer.music.load('/home/pi/relaxleep/speaker/littlestar.mp3')
                clock = pygame.time.Clock()
                pygame.mixer.music.play()
                print('play now')
                while pygame.mixer.music.get_busy():
                    print('play music')
                    time.sleep(5)
                    pygame.mixer.music.stop()
                    print('off music')

        else:
            print('No Connected Arduino')

    
