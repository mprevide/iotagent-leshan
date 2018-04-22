#! /bin/usr/python3

from iotclient import IotClient, load_template

def run():
    iotc = IotClient()

    iotc.upload_image("example.hex", "lwm2m_base", "1.0.0")
    iotc.upload_image("example.hex", "lwm2m_base", "1.0.1")

    base_template = load_template("models/lwm2m_base.json")
    temp_sensor_template = load_template("models/3303.json")
    light_bulb_template = load_template("models/3311.json")

    base_template_id = str(iotc.create_template(base_template))
    temp_sensor_template_id = str(iotc.create_template(temp_sensor_template))
    light_bulb_template_id = str(iotc.create_template(light_bulb_template))

    device_payload = {
        "templates": [base_template_id, temp_sensor_template_id, light_bulb_template_id],
        "label": "ExampleFW"
    }

    device_id = iotc.create_device(device_payload)

# actuating_attr = {
#         "luminosity": 10.6
# }
# iotc.actuate(actuating_attr)
