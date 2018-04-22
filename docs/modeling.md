# Dojot vs LWM2M Models

While LwM2M is provides a multi-level hierarchy format intended to be thorough,
one of Dojot's goal is to be simple, this incurs differences in device modeling
and handling as a general. Some major differences are noted in the table bellow.
Obs.: The terms Resource/Attributes are used interchangeably 

|                        | Dojot          | LwM2M               |
|------------------------|----------------|---------------------|
| Registration           | User Initiated | Device Initiated    |
| Attribute READ         | Only Passive   | Active or Passive   |
| Dynamic Attributes     | Explicit       | Application Defined |
| Repeating Resources    | No             | Yes                 |
| Multivariate Resources | No             | Yes                 |
| Execute Attributes     | No             | Yes                 |
| Runtime Modification   | Yes            | No                  |


To acompass these discrepancies some compromises have to be made.


### Connection
Devices in Dojot are managed by the user. As long as the device is not registered on
device-manager, the iotagent will ignore incoming connection notifications. This check
is based on resources **/3/0/1:Model Number** and **/3/0/2:Serial Number**, which act
as a uni.

Once a registration exists an incoming connection will be accepted and all *dynamic attributes*
will be registered for notification.

A minimal working dojot template is [provided](../client/models/lwm2m_base.json)

 
### Object resource discovery

Once the device is properly connected, all attributes containing lwm2m metadata will
be inspected, eg.:

        {
          "value_type": "boolean",
          "metadata": [
            {
              "type": "lwm2m",
              "value_type": "string",
              "static_value": "/3311/0/5850",
              "label": "oi"
            }
          ],
          "type": "actuator",
          "label": "Light Control: On/Off"
        },

Every time a new object model is discovered in a new device (in this example 3311),
the server ModelBuilder will be updated. Each object model is only discovered once 
to avoid discrepancies (This may be reviewed).

### Model Conversion

In a given device, each Resource/Attribute is uniquely identified by:

- Label and Type on Dojot
- Object/Instance/Resource path on LwM2M

This provides a one-to-one mapping of any resource, represented in the attribute metadata
(See above), but doesn't enforce any rule as to the values expected in this mapping.
To facilitate this conversion, we provide a [script](../client/oma2template.py) 
to convert OMA xml specification 
(see [this](http://www.openmobilealliance.org/wp/OMNA/LwM2M/LwM2MRegistry.html))
 to Dojot template model. More information is available on this
[user guide](../docs/oma2template_tutorial.md)
