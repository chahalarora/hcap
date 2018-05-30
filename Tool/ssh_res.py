#!/usr/bin/env python

import sys, paramiko
from time import sleep

hostname = "linux.cpsc.ucalgary.ca"
username = "shauvik.shadman"
port = 22


try:
    client = paramiko.SSHClient()
    client.load_system_host_keys()
    client.set_missing_host_key_policy(paramiko.WarningPolicy)
    
    client.connect(hostname, port=port, username=username)

    with open('ResRunCommands.txt') as f:
        for line in f:
            print(line)
            stdin, stdout, stderr = client.exec_command(line)
            print stdout.read(1),


    while True:
        print stdout.read(1),


finally:
    client.close()