#! /bin/usr/python3

from iotclient import IotClient
from copy import deepcopy
import json


def load_template(filename):
    with open(filename, "r") as f:
        return json.load(f)


iotc = IotClient()

iotc.upload_image("example.hex", "ExampleFW", "1.0.1")

template1 = load_template("template1_0_0.json")
template2 = load_template("template1_0_1.json")
temp_sensor_template = load_template("3303.json")
light_bulb_template = load_template("3311.json")

template1_id = str(iotc.create_template(template1))
template2_id = str(iotc.create_template(template2))
temp_sensor_template_id = str(iotc.create_template(temp_sensor_template))
light_bulb_template_id = str(iotc.create_template(light_bulb_template))

device_payload = {
    "templates": [template1_id],
    "label": "ExampleFW"
}

new_device_payload = {
    "templates": [template2_id],
    "label": "ExampleFW"
}

device_id = iotc.create_device(device_payload)
iotc.update_device(device_id, new_device_payload)

new_device_payload = {
    # "templates": [template1_id, temp_sensor_template_id, light_bulb_template_id],
    "templates": [template1_id, temp_sensor_template_id, light_bulb_template_id],
    "label": "ExampleFW"
}


temp_dict = {val['label']:val for val in temp_sensor_template['attrs']}
light_dict = {val['label']:val for val in light_bulb_template['attrs']}

temp_set = set(temp_dict)
light_set = set(light_dict)




for name in temp_set.intersection(light_set):
    print(name, temp_dict[name])

iotc.update_device(device_id, new_device_payload)
# actuating_attr = {
#         "luminosity": 10.6
# }
# iotc.actuate(actuating_attr)
