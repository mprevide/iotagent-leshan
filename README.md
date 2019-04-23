This is an LWM2M IoTAgent, part of the [dojot project](http://dojotdocs.readthedocs.io).
It was written in java and uses [Eclipse Leshan](https://www.eclipse.org/leshan/) as base for LWM2M handling.

The purpose of this module is to translate device configurations sent to dojot via device-manager
into LWM2M configuration and commands.

# About

This is an initial version that only supports a sub set of the LwM2M v1.0 features.<br>
Using this agent you can:
- monitor LwM2M resources
- interact with LwM2M resources (write and execute)
- use DTLS communication (only with PSK)
- firmware update
Please note that:
- LwM2M attributes are not supported
- multidimensional resources are not supported

# How to create a dojot's device in compliance to this agent

First of all your device must use the [base template](client/template_lwm2m.json),
it defines the device's `endpoint` which is the device identifier in the LwM2M protocol.<br>

All attributes that you desire to model must contain a metadata with the following
structure:
```json
  "metadata": [
      ...,
    {
      "label": "path",
      "type": "lwm2m",
      "value_type": "string",
      "static_value": "/3303/0/5601"
    }
  ]
```
Note that the `static_value` reflects the LwM2M resource path, this value must
be customized.

This agent uses the following rules to map the resources:
- dojot dynamic attributes are mapped as LwM2M read resources;
- dojot actuator attributes are mapped as LwM2M write or execution resources.

The following metadata should be included if your attribute has execution property.
```json
  "metadata": [
      ...,
    {
      "type": "lwm2m",
      "label": "operations",
      "static_value": "e",
      "value_type": "string"
    }
  ]
```

# How to encapsulates the service into a Docker container

In order to use this service in the dojot environment we need to encapsulate it
in a Docker container.<br>
You can do it just executing the following command:
```sh
docker build -t dojot/iotagent-lwm2m .
```
Obs: just note that `dojot/iotagent-lwm2m` is the image name, you can replace it
with some other name that you desire.

# Environment variables

This service relies on some environment variables to configure some aspects.
These variables are the following ones:
  - DOJOT_MANAGEMENT_USER:
  - KAFKA_GROUP_ID:     
  - FILE_SERVER_ADDRESS:

   