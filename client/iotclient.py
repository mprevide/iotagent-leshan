import requests
import json
import urllib.parse
import jwt
import time
import binascii
import os
import datetime
import hashlib
import sys


def load_template(filename):
    with open(filename, "r") as f:
        return json.load(f)


def get_sha1(filename):
    sha1 = hashlib.sha1()
    # Calculate sha1 for our example file
    with open(filename, 'rb') as f:
        data = f.read()
        sha1.update(data)
    return sha1


class IotClient(object):
    def __init__(self):

        if len(sys.argv) == 4:
            host = sys.argv[1]
            login = sys.argv[2]
            passwd = sys.argv[3]
            tenant = sys.argv[4]
        else:
            print('You can pass in command line: url, login and passwd for dojot.')
            print('Eg: python3 filename.py http://test:8000 login pass tenant')
            host = 'http://localhost:8000'
            login = 'admin'
            passwd = 'admin'
            tenant = 'admin'

        print('Using url='+host+', login=' +
              login+' and passwd='+passwd+' in tenant=.'+tenant)

        self.host = host
        self.login = login
        self.passwd = passwd
        self.tenant = tenant
        self.jwt_token = self.get_token()
        self.headers = {'Authorization': 'Bearer ' + self.jwt_token}

    def get_device_id(self, device_label, serial_number):
        url_query = self.host+"/device?attr=device_type={}&serial_number={}".format(
            device_label, serial_number)
        r = requests.get(url_query, headers=self.headers)
        devices = json.loads(r.text)["devices"]
        if not devices:
            return ""
        return devices[0]["id"]

    def get_token(self):
        credentials = {
            "username": self.login,
            "password": self.passwd,
            "client_id": "cli",
            "grant_type": "password",
        }
        auth_url = self.host+'/auth/realms/'+self.tenant+'/protocol/openid-connect/token'
        payload = requests.post(
            auth_url, data=credentials)
        jwt_token = payload.json()['access_token']
        return jwt_token

    def upload_image(self, filename, template_name, fw_version):
        payload = {
            "label": template_name,
            "fw_version": fw_version,
        }

        # Upload Metadata
        base_url = self.host+'/fw-image'
        r = requests.post(base_url + "/image/",
                          json=payload, headers=self.headers)
        image_url = json.loads(r.text)['url']
        image_url = base_url + image_url
        binary_url = image_url + "/binary"
        print(binary_url)
        # Upload File
        files = {'image': open(filename, 'rb')}
        r = requests.post(binary_url, files=files, headers=self.headers)
        print(r.text)

        return image_url

    def create_template(self, template):
        base_url = self.host+'/template'
        r = requests.post(base_url, json=template, headers=self.headers)
        response = json.loads(r.text)
        print(response)
        return response['template']

    def create_device(self, device_payload):
        base_url = self.host+'/device'
        r = requests.post(base_url, json=device_payload,
                          headers=self.headers, params={'verbose': False})
        response = json.loads(r.text)
        # device = response['devices'][0]
        device_id = response['devices'][0]['id']
        return device_id

    def update_device(self, device_id, device_payload):
        base_url = self.host+'/device/' + device_id
        r = requests.put(base_url, json=device_payload,
                         headers=self.headers, params={'verbose': True})
        response = json.loads(r.text)
        # device = response['devices'][0]
        device_id = response['device']['id']
        return device_id

    def actuate(self, device_id, attrs):
        actuate_payload = {
            "attrs": attrs
        }
        base_url = self.host+'/device/'
        r = requests.put(base_url + device_id + '/actuate',
                         json=actuate_payload, headers=self.headers)

    def clear_images(self):
        base_url = self.host+'/fw-image/image'
        r = requests.get(base_url, headers=self.headers)
        response = json.loads(r.text)
        for image in response:
            r = requests.delete(
                base_url + "/"+image['id'], headers=self.headers)

    def clear_devices(self):
        base_url = self.host+'/device'
        r = requests.get(base_url, headers=self.headers)
        response = json.loads(r.text)
        for device in response['devices']:
            url = base_url + "/" + device['id']
            r = requests.delete(url, headers=self.headers)

    def clear_templates(self):
        base_url = self.host+'/template'
        r = requests.get(base_url, headers=self.headers)
        response = json.loads(r.text)
        for template in response['templates']:
            url = base_url + "/" + str(template['id'])
            r = requests.delete(url, headers=self.headers)

    def gen_psk(self, device_id, key_len):
        base_url = self.host+'/device/gen_psk/' + \
            device_id + '?key_length=' + str(key_len)
        r = requests.post(base_url, headers=self.headers,
                          params={'verbose': True})
        return r.text
