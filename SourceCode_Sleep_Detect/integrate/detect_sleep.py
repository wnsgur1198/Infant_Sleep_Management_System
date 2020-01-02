# USAGE Command---------------------------------
# python detect_sleep.py --prototxt MobileNetSSD_deploy.prototxt.txt --model MobileNetSSD_deploy.caffemodel

import imutils
from imutils.video import VideoStream
from imutils.video import FPS
import numpy as np
import argparse
import time
import cv2
import pymysql


# Variable for DB Storage----------------
cntBody = 0
eyeValue = []
poseValue = []


# Rotate to Screen Direction----------------------
def Rotate(src, degrees):
    if degrees == 90:
        dst = cv2.transpose(src)
        dst - cv2.flip(dst, 1)
    elif degrees == 180:
        dst = cv2.flip(src, -1)
    elif degrees == 270:
        dst = cv2.transpose(src)
        dst = cv2.flip(dst, 0)
    else:
        dst = None
    return dst


# Detect To Pose--------------------------------
def detect_pose(frame, poseValue):
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    faces = sideCascade.detectMultiScale(
        gray,
        scaleFactor=1.3,
        minNeighbors=5,
        minSize=(30, 30)
    )
    for (x, y, w, h) in faces:
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 0, 255), 2)

        # Detect Sideview-------------------
        poseValue.append(1)
        return poseValue

    poseValue.append(0)
    return poseValue


# Detect To Face & Eyes--------------------------------
def detect_face_and_eye(frame, eyeValue):
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    faces = faceCascade.detectMultiScale(
        gray,
        scaleFactor=1.3,
        minNeighbors=5,
        minSize=(30, 30)
    )
    for (x, y, w, h) in faces:
        cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 0), 2)
        roi_gray = gray[y:y + h, x:x + w]
        roi_color = frame[y:y + h, x:x + w]

        eyes = eyeCascade.detectMultiScale(
            roi_gray,
            scaleFactor=1.5,
            minNeighbors=10,
            minSize=(5, 5),
        )
        for (ex, ey, ew, eh) in eyes:
            cv2.rectangle(roi_color, (ex, ey), (ex + ew, ey + eh), (0, 255, 0), 2)

            # Detect Eye-------------------
            eyeValue.append(1)

            return eyeValue

        eyeValue.append(0)

    return eyeValue


# Detect To Body--------------------------------
def detect_body(frame, detections, dbFlag, _cntBody, poseValue, eyeValue):
    for i in np.arange(0, detections.shape[2]):
        confidence = detections[0, 0, i, 2]
        if confidence > args["confidence"]:
            idx = int(detections[0, 0, i, 1])
            box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
            (startX, startY, endX, endY) = box.astype("int")
            label = "{}: {:.2f}%".format(CLASSES[idx], confidence * 100)
            cv2.rectangle(frame, (startX, startY), (endX, endY), COLORS[idx], 2)
            y = startY - 15 if startY - 15 > 15 else startY + 15
            cv2.putText(frame, label, (startX, y), cv2.FONT_HERSHEY_SIMPLEX, 0.5, COLORS[idx], 2)

            # Decide Human Body
            if CLASSES[idx] == "person":

                _cntBody += 1

                print('Count: ' + str(_cntBody))

                if _cntBody == 5: # 30:  # 5sec

                    # Sleep Pose-----------------------------------
                    if len(eyeValue) == 0:
                        for p in poseValue:
                            if p == 1:
                                print("Side!")
                                # Insert Table
                                if dbFlag:
                                    cur.execute("insert into Pose_Table values(%s,%s)", (time.strftime("%Y-%m-%d-%H:%M", time.localtime()), 1))
                                poseValue = []
                                _cntBody = 0
                                return _cntBody, poseValue, eyeValue

                        print("Back!")
                        # Insert Table
                        if dbFlag:
                            cur.execute("insert into Pose_Table values(%s,%s)", (time.strftime("%Y-%m-%d-%H:%M", time.localtime()), 0))
                        poseValue = []
                        _cntBody = 0
                        return _cntBody, poseValue, eyeValue

                    # Sleep State------------------------------------
                    for v in eyeValue:
                        if v == 1:
                            print("Awake!")
                            # Insert Table
                            if dbFlag:
                                cur.execute("insert into Sleep_Table values(%s,%s)", (time.strftime("%Y-%m-%d-%H:%M", time.localtime()), 1))
                            eyeValue = []
                            _cntBody = 0
                            return _cntBody, poseValue, eyeValue

                    print("Sleep!")
                    # Insert Table
                    if dbFlag:
                        cur.execute("insert into Sleep_Table values(%s,%s)", (time.strftime("%Y-%m-%d-%H:%M", time.localtime()), 0))
                    eyeValue = []
                    _cntBody = 0
                    return _cntBody, poseValue, eyeValue

            return _cntBody, poseValue, eyeValue


# ----------------------------------------------

# Detect Sideview Face----------------------
sideCascade = cv2.CascadeClassifier('Cascades/haarcascade_profileface.xml')

# Detect Face-----------------------------------
faceCascade = cv2.CascadeClassifier('Cascades/haarcascade_frontalface_default.xml')

# Detect Eyes----------------------------------
eyeCascade = cv2.CascadeClassifier('Cascades/haarcascade_eye.xml')

# Detect Human Body---------------------------
ap = argparse.ArgumentParser()
ap.add_argument("-p", "--prototxt", required=True, help="path to Caffe 'deploy' prototxt file")
ap.add_argument("-m", "--model", required=True, help="path to Caffe pre-trained model")
ap.add_argument("-c", "--confidence", type=float, default=0.2, help="minimum probability to filter weak detections")
args = vars(ap.parse_args())
CLASSES = ["background", "aeroplane", "bicycle", "bird", "boat",
           "bottle", "bus", "car", "cat", "chair", "cow", "diningtable",
           "dog", "horse", "motorbike", "person", "pottedplant", "sheep",
           "sofa", "train", "tvmonitor"]
COLORS = np.random.uniform(0, 255, size=(len(CLASSES), 3))
net = cv2.dnn.readNetFromCaffe(args["prototxt"], args["model"])

# Receive Video--------------------------------------------
videoFlag = True
# videoFlag = False

if videoFlag:   # 1 Fast Reading
    # vs = VideoStream(0).start()                                               # PC의 카메라 영상
    # vs = VideoStream("http://192.168.123.70:8090/?action=stream").start()     # 라즈베리파이의 카메라 영상
    vs = VideoStream("../sample/video/sleep_body_side.mp4").start()             # 저장된 샘플 영상
else:   # 2 Little Slow Reading
    # vs = cv2.VideoCapture(0)                                              # PC의 카메라 영상
    # vs = cv2.VideoCapture("http://192.168.123.70:8090/?action=stream")    # 라즈베리파이의 카메라 영상
    vs = cv2.VideoCapture("../sample/video/sleep_body_back.mp4")            # 저장된 샘플 영상

# Connect to Database---------------------------------------------------
# conn = pymysql.connect(host="relaxleep.ckbl1fdsxpkw.ap-northeast-2.rds.amazonaws.com", user="user", passwd="00000000", db="relaxleepDB")
conn = None

# Reset to Table---------------------------------------------------
# reset = True
reset = False

fps = FPS().start()

while True:
    if videoFlag:
        frame = vs.read()         # 1
    else:
        ret, frame = vs.read()    # 2

    # Rotate Video Direction-------------------------
    frame = Rotate(frame, 90)

    frame = imutils.resize(frame, width=400)
    (h, w) = frame.shape[:2]
    blob = cv2.dnn.blobFromImage(cv2.resize(frame, (300, 300)), 0.007843, (300, 300), 127.5)
    net.setInput(blob)
    detections = net.forward()

    # Detect Pose
    poseValue = detect_pose(frame, poseValue)

    # Detect Face and Eye
    eyeValue = detect_face_and_eye(frame, eyeValue)

    try:
        if conn is not None:
            with conn.cursor() as cur:

                # Reset Table
                if reset:
                    cur.execute("truncate Sleep_Table")
                    cur.execute("truncate Pose_Table;")
                    conn.commit()
                    break

                cntBody, poseValue, eyeValue = detect_body(frame, detections, True, cntBody, poseValue, eyeValue)
                conn.commit()

        else:  # No DB
            cntBody, poseValue, eyeValue = detect_body(frame, detections, False, cntBody, poseValue, eyeValue)

        cv2.imshow('Decide Sleep State', frame)
        key = cv2.waitKey(1) & 0xFF
        if key == 27:   # press ESC
            break

        fps.update()

    except KeyboardInterrupt:
        print("quit")

fps.stop()
cv2.destroyAllWindows()
vs.stop()
