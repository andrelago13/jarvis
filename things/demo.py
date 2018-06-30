import pika
import jarvisled
import button
import threading
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
hall_queue = '/house/hall_light/actions'
living_room_motion_queue = '/house/living_room_motion_sensor/events'

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

#jarvisled.initGPIO()

connection = pika.BlockingConnection(pika.ConnectionParameters(host, credentials=pika.PlainCredentials(username=username, password=password)))
channel = connection.channel()
channel.queue_declare(queue=bedroom_queue)
channel.queue_declare(queue=living_room_queue)

####################################
########### SEND MESSAGE ###########
####################################

#channel.basic_publish(exchange='',
#                      routing_key='hello',
#                      body='Hello World!')
#print(" [x] Sent 'Hello World!'")
#connection.close()

####################################
######### RECEIVE MESSAGE ##########
####################################

def callback_bedroom(ch, method, properties, body):
    jarvisled.turnLed(bedroom_led, body)

def callback_living_room(ch, method, properties, body):
    jarvisled.turnLed(living_room_led, body)

def callback_hall(ch, method, properties, body):
    jarvisled.turnLed(hall_led, body)

#def callback_livingroompressure_button_pressed():
#    print("Button pressed")
#    channel.basic_publish(exchange='', routing_key=living_room_motion_queue, body='on')

#button_thread = threading.Thread(target = button.button_func)
#button_thread.start()

channel.basic_consume(callback_bedroom, queue=bedroom_queue, no_ack=True)
channel.basic_consume(callback_living_room, queue=living_room_queue, no_ack=True)
channel.basic_consume(callback_hall, queue=hall_queue, no_ack=True)

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()