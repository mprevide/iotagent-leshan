#! /bin/usr/python3

from iotclient import IotClient
from copy import deepcopy

iotc = IotClient()

iotc.upload_image("example.hex", "ExampleFW", "1.0.1")

template1 = {
    "label": 'Template_1_0_0',
    "attrs": [
        {
            "label": "fw_version",
            "type": "static",
            "value_type": "string",
            "static_value": "1.0.0",
            "metadata": [
                {"label": "oi", "static_value": "/3/0/3", "type": "lwm2m", "value_type": "string"}
            ]
        },
        {
            "label": "device_type",
            "type": "static",
            "value_type": "string",
            "static_value": "ExampleFW",
            "metadata": [
                {"label": "oi", "static_value": "/3/0/1", "type": "lwm2m", "value_type": "string"}
            ]
        },
        {
            "label": "serial_number",
            "type": "static",
            "value_type": "string",
            "static_value": "123456789",
            "metadata": [
                {"label": "oi", "static_value": "/3/0/2", "type": "lwm2m", "value_type": "string"}
            ]
        },
        {
            "label": "luminosity",
            "type": "actuator",
            "value_type": "float",
            "extra": {
                "path": "5000/0/2"
            }
        }

    ]
}

template2 = deepcopy(template1)
template2['label'] = 'Template_1_0_1'
for attr in template2['attrs']:
    if attr['label'] == 'fw_version':
        attr['static_value'] = '1.0.1'

template1_id = iotc.create_template(template1)
template2_id = iotc.create_template(template2)

device_payload = {
    "templates": [str(template1_id)],
    "label": "ExampleFW"
}

new_device_payload = {
    "templates": [str(template2_id)],
    "label": "ExampleFW"
}

device_id = iotc.create_device(device_payload)
iotc.update_device(device_id, new_device_payload)

# actuating_attr = {
#         "luminosity": 10.6
# }
# iotc.actuate(actuating_attr)
