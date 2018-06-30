import pika
import jarvisled
import button
import threading
from threading import Thread
import time
import RPi.GPIO as GPIO

living_room_led = 17
bedroom_led = 27
hall_led = 22
living_room_button = 12
hall_button = 18

host = 'andrelago.eu'
username = 'rabbitmq'
password = 'rabbitmq'
bedroom_queue = '/house/bedroom_light/actions'
living_room_queue = '/house/living_room_light/actions'
living_room_event_queue = '/house/living_room_motion_sensor/events'
hall_queue = '/house/hall_light/actions'
hall_event_queue = '/house/hall_motion_sensor/events'
#living_room_motion_queue = '/house/living_room_motion_sensor/events'

def initGPIO ():
   GPIO.setmode(GPIO.BCM)
   GPIO.setwarnings(False)

   # output LEDs
   GPIO.setup(living_room_led, GPIO.OUT)
   GPIO.setup(bedroom_led, GPIO.OUT)
   GPIO.setup(hall_led, GPIO.OUT)

   # input buttons
   GPIO.setup(living_room_button, GPIO.IN, pull_up_down=GPIO.PUD_UP)
   GPIO.setup(hall_button, GPIO.IN, pull_up_down=GPIO.PUD_UP)

def button_func (button_pin, queue):
    previous_state = True
    while True:
        input_state = GPIO.input(button_pin)
        if input_state != previous_state:
            previous_state = input_state
            if(input_state == False):
                print('Button Pressed')
                time.sleep(0.2)

####################################
########### SEND MESSAGE ###########
####################################

#channel.basic_publish(exchange='',
#                      routing_key='hello',
#                      body='Hello World!')
#print(" [x] Sent 'Hello World!'")
#connection.close()

####################################
######### LED CONTROLLER  ##########
####################################

def callback_bedroom(ch, method, properties, body):
    jarvisled.turnLed(bedroom_led, body)

def callback_living_room(ch, method, properties, body):
    jarvisled.turnLed(living_room_led, body)

def callback_hall(ch, method, properties, body):
    jarvisled.turnLed(hall_led, body)

def initLedController():
    initGPIO()

    connection = pika.BlockingConnection(pika.ConnectionParameters(host, credentials=pika.PlainCredentials(username=username, password=password)))
    channel = connection.channel()
    channel.queue_declare(queue=bedroom_queue)
    channel.queue_declare(queue=living_room_queue)

    channel.basic_consume(callback_bedroom, queue=bedroom_queue, no_ack=True)
    channel.basic_consume(callback_living_room, queue=living_room_queue, no_ack=True)
    channel.basic_consume(callback_hall, queue=hall_queue, no_ack=True)

    print(' [*] Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()

####################################
######### BUTTON CONTROLLER  #######
####################################

def initBtnController_1() :
    initGPIO()
    button_func(hall_button, hall_event_queue)

def initBtnController_2() :
    initGPIO()
    button_func(living_room_button, living_room_event_queue)

#def callback_livingroompressure_button_pressed():
#    print("Button pressed")
#    channel.basic_publish(exchange='', routing_key=living_room_motion_queue, body='on')

#button_thread = threading.Thread(target = button.button_func)
#button_thread.start()
