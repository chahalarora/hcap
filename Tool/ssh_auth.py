#!/usr/bin/env python

import sys, paramiko
from time import sleep
import argparse
import os
from platform   import system as system_name  # Returns the system/OS name
from subprocess import call   as system_call  # Execute a shell command


parser = argparse.ArgumentParser()
parser.add_argument("Hostname", help="This is the name of the host server you would like to run your server on")
parser.add_argument("Username", help="This is your username for the host you are trying to run the server on.")
args = parser.parse_args()

def ping(host):
    # Ping command count option as function of OS
    param ='-n 1' if system_name().lower()=='windows' else '-c 1'
    param1, param2 = param.split()
    a = open("output.txt", "w")
    # Building the command. Ex: "ping -c 1 google.com"
    command = ['ping', param1, param2, host]

    # Pinging
    return system_call(command,stdout=a) == 0

# print(ping(args.Hostname))
if(ping(args.Hostname)):
    port = 22

    try:
        client = paramiko.SSHClient()
        client.load_system_host_keys()
        client.set_missing_host_key_policy(paramiko.WarningPolicy)
        
        client.connect(args.Hostname, port=port, username=args.Username)

        with open('AuthRunCommands.txt') as f:
            for line in f:
                print(line)
                stdin, stdout, stderr = client.exec_command(line)
                print stdout.read(1),


        while True:
            print stdout.read(1),


    finally:
        client.close()
else:
    print("Server unresponsive, please try again!")