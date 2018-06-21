import pika
import jarvisled
import button
import threading

host = 'andrelago.eu'
username = 'rabbitmq'
password = 'rabbitmq'
bedroom_queue = '/house/bedroom_light/actions'
living_room_queue = '/house/living_room_light/actions'
living_room_motion_queue = '/house/living_room_motion_sensor/events'

jarvisled.initGPIO()

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
    print(" [x] Received %r" % body)
    if body == 'on':
        jarvisled.ledOn_1()
    elif body == 'off':
        jarvisled.ledOff_1()
    else:
        print("Unrecognized message")

def callback_living_room(ch, method, properties, body):
    print(" [x] Received %r" % body)
    if body == 'on':
        jarvisled.ledOn_2()
    elif body == 'off':
        jarvisled.ledOff_2()
    else:
        print("Unrecognized message")

def callback_livingroompressure_button_pressed():
    print("Button pressed")
    channel.basic_publish(exchange='', routing_key=living_room_motion_queue, body='on')

button_thread = threading.Thread(target = button.button_func)
button_thread.start()

channel.basic_consume(callback_bedroom, queue=bedroom_queue, no_ack=True)
channel.basic_consume(callback_living_room, queue=living_room_queue, no_ack=True)
print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()
