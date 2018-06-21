import RPi.GPIO as GPIO
import time

button_pin = 22

def button_func (callback):
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(button_pin, GPIO.IN, pull_up_down=GPIO.PUD_UP)

    previous_state = True
    while True:
        input_state = GPIO.input(button_pin)
        if input_state != previous_state:
            previous_state = input_state
            if(input_state == False):
                print('Button Pressed')
                time.sleep(0.2)
                callback()