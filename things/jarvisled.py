import RPi.GPIO as GPIO
import time

pin_1 = 18
pin_2 = 17

def initGPIO ():
   GPIO.setmode(GPIO.BCM)
   GPIO.setwarnings(False)
   GPIO.setup(pin_1,GPIO.OUT)
   GPIO.setup(pin_2,GPIO.OUT)

def ledOn_1 ():
   GPIO.output(pin_1, GPIO.HIGH)
   print "LED 1 on"

def ledOff_1 ():
   GPIO.output(pin_1, GPIO.LOW)
   print "LED 1 off"

def ledOn_2 ():
   GPIO.output(pin_2, GPIO.HIGH)
   print "LED 2 on"

def ledOff_2 ():
   GPIO.output(pin_2, GPIO.LOW)
   print "LED 2 off"
