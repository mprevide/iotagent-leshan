import requests
import json
import urllib.parse
import jwt
import time
import binascii
import os
import datetime
import hashlib


def get_sha1(filename):
    sha1 = hashlib.sha1()
    # Calculate sha1 for our example file
    with open(filename, 'rb') as f:
        data = f.read()
        sha1.update(data)
    return sha1


class IotClient(object):
    def __init__(self):
        self.jwt_token = self.get_token()
        self.headers = {'Authorization': 'Bearer ' + self.jwt_token}
        r = requests.get("http://localhost:5001/device?attr=fw_version=1.0.1&device_type=ExampleFW", headers=self.headers)
        print(r.text)




    def get_token(self):
        auth_url = 'http://localhost:8000/auth/'
        r = requests.post(auth_url, json={"username": "admin", "passwd": "admin"})
        jwt_token = json.loads(r.text)['jwt']
        return jwt_token



    def upload_image(self, filename, device, fw_version):
        payload = {
            "label": device,
            "fw_version": fw_version,
            "sha1": get_sha1(filename).hexdigest()
        }

        # Upload Metadata
        base_url = 'http://localhost:8000/image/'
        r = requests.post(base_url, json=payload, headers=self.headers)
        image_url = json.loads(r.text)['url']
        image_url = urllib.parse.urljoin(base_url, image_url)
        binary_url = urllib.parse.urljoin(image_url + "/", "binary")
        # Upload File
        files = {'image': open(filename, 'rb')}
        r = requests.post(binary_url, files=files, headers=self.headers)

        return image_url


    def create_template(self, template):
        base_url = 'http://localhost:8000/template'
        r = requests.post(base_url, json=template, headers=self.headers)
        response = json.loads(r.text)
        template_id = response['template']['id']
        return template_id

    def create_device(self, device_payload):
        base_url = 'http://localhost:8000/device'
        r = requests.post(base_url, json=device_payload, headers=self.headers, params={'verbose': True})
        response = json.loads(r.text)
        # device = response['devices'][0]
        device_id = response['device']['id']
        return device_id

    def update_device(self, device_id, device_payload):
        base_url = 'http://localhost:8000/device/' + device_id
        r = requests.put(base_url, json=device_payload, headers=self.headers, params={'verbose': True})
        response = json.loads(r.text)
        # device = response['devices'][0]
        device_id = response['device']['id']
        return device_id

    def actuate(self, device_id, attrs):
        actuate_payload = {
            "attrs": attrs
        }
        base_url = 'http://localhost:8000/device/'
        r = requests.put(base_url + device_id + '/actuate', json=actuate_payload, headers=self.headers)