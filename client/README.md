## Simulator

### Configuring Pipenv

In order to isolate the environment, it is better to use Python's virtual environments. Even better
than using `virtualenv` by itself, we can use `pipenv`.

Make sure you have Python 3 and its correspondent version of pip installed. Then, install `pipenv`:

```shell
pip3 install pipenv
```

Install the environment:

```shell
pipenv --python 3.8
```

Install the dependencies via `pipenv`:

```shell
pipenv install
```

Enter in the virtual environment:
```shell
pipenv shell
```

Check the [pipenv repository](https://github.com/pypa/pipenv/) for more commands and details.

### Simulating a device

#### Create a device to use with simulator

```shell
python3 create_lwm2m_devices.py http://localhost:8000  admin admin
```

This command will create n device (`unsecure-dev-0`) with 3 templates (`lwm2m`, `firmware_update` and `Temperature`) in a dojot running at
 http://localhost:8000 and accessible by the user `admin` and password `admin`.


#### Using simulator

Accessing the folder:

```shell
cd leshan-client-demo/
```

Generating the **jar** :

```shell
sudo apt install default-jre maven #On Debian-based Linux distributions, if necessary.
mvn package
```

To execute the simulator:

```shell
java -jar target/leshan-client-demo-1.0.0-M10-jar-with-dependencies.jar -n unsecure-client-endpoint-0 -u localhost:5683
```

In this case, the simulator will publish to device `unsecure-client-endpoint-0` in a dojot ruining at `localhost` to the `coap` port  (`udp`) `5683`.


To view all available options:

```shell
java -jar target/leshan-client-demo-1.0.0-M10-jar-with-dependencies.jar -h
```


