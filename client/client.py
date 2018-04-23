import click
from iotclient import IotClient
import db_fixture

@click.group()
def cli():
    pass

@cli.command()
@click.option('--switch', help='[ON/OFF]', type=str)
@click.option('--dimmer', help='% value', type=int)
def actuate(switch, dimmer):
    """Actuation commands for example"""

    iotc = IotClient()
    device_id = iotc.get_device_id("ExampleFW", "123456789")

    print(iotc)
    attrs = {}

    if switch.lower()=="on":
        attrs["LightControl-On_Off-0"] = True
    elif switch.lower() == "off":
        attrs["LightControl-On_Off-0"] = False

    if dimmer:
        attrs["LightControl-Dimmer-0"] = dimmer

    print(attrs)
    iotc.actuate(device_id, attrs)


@cli.command()
def fixture():
    """Runs Database fixture for use in example"""
    clear.callback(True, True, True, True)
    db_fixture.run()

@cli.command()
@click.option('--images/--no-images', default=False, help='Remove Images', required=False)
@click.option('--devices/--no-devices', default=False, help='Remove device', required=False)
@click.option('--templates/--no-templates', default=False, help='Remove templates', required=False)
@click.option('--all/--no-all', default=False, help='Clear Database', required=False)
def clear(images, devices, templates, all):
    """Clear database entries. --help for options"""
    iotc = IotClient()

    if all:
        iotc.clear_images()
        iotc.clear_devices()
        iotc.clear_templates()
        return

    if images:
        iotc.clear_images()

    if devices:
        iotc.clear_devices()

    if templates:
        iotc.clear_templates()


if __name__ == '__main__':

    # device_id = iotc.get_device_id("ExampleFW", "1.0.0", "123456789")
    # attrs = {
    #     "Light Control: On/Off": True
    # }
    # iotc.actuate(device_id, attrs)
    cli()










