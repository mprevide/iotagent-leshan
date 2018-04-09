from iotclient import IotClient


iotc = IotClient()

device_id = iotc.get_device_id("ExampleFW", "1.0.0", "123456789")
attrs = {
    "Light Control: On/Off": True
}
iotc.actuate(device_id, attrs)
