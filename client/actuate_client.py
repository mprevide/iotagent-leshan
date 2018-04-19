import click
from iotclient import IotClient


iotc = IotClient()

@click.command()
@click.option('--switch', help='[ON/OFF]', type=str)
@click.option('--dimmer', help='% value', type=int)
def actuate(switch, dimmer):

    device_id = iotc.get_device_id("ExampleFW", "123456789")

    print(iotc)
    attrs = {}

    if switch.lower()=="on":
        attrs["Light Control: On/Off"] = True
    elif switch.lower() == "off":
        attrs["Light Control: On/Off"] = False

    if dimmer:
        attrs["Light Control: Dimmer"] = dimmer

    print(attrs)
    iotc.actuate(device_id, attrs)

if __name__ == '__main__':

    # device_id = iotc.get_device_id("ExampleFW", "1.0.0", "123456789")
    # attrs = {
    #     "Light Control: On/Off": True
    # }
    # iotc.actuate(device_id, attrs)
    actuate()










