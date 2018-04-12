#! /bin/usr/python3

from iotclient import IotClient, load_template

iotc = IotClient()

iotc.upload_image("example.hex", "ExampleFW", "1.0.1")

template1 = load_template("template1_0_0.json")
temp_sensor_template = load_template("3303.json")
light_bulb_template = load_template("3311.json")

template1_id = str(iotc.create_template(template1))
temp_sensor_template_id = str(iotc.create_template(temp_sensor_template))
light_bulb_template_id = str(iotc.create_template(light_bulb_template))

device_payload = {
    "templates": [template1_id, temp_sensor_template_id, light_bulb_template_id],
    "label": "ExampleFW"
}

device_id = iotc.create_device(device_payload)

# actuating_attr = {
#         "luminosity": 10.6
# }
# iotc.actuate(actuating_attr)
