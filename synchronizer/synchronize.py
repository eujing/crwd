from pymongo import MongoClient
import socket
import json
import time
from pprint import pprint

client = MongoClient("localhost", 3001)
db = client.meteor

def recvall(s):
    data = b""
    part = None

    while part is None or part[-1] != ord("\n"):
        part = s.recv(4096)
        data += part

    return data

def listLocations(s):
    s.send((json.dumps({"function": "listLocations"}) + "\n").encode())
    resp = recvall(s)

    return json.loads(resp.decode("utf-8"))

def listSensors(s, location_id):
    s.send((json.dumps({
        "function": "listSensors",
        "location": location_id
    }) + "\n").encode())

    resp = recvall(s)

    return json.loads(resp.decode("utf-8"))

def getLocation(s, location_id, tFront, tBack):
    s.send((json.dumps({
        "function": "getLocation",
        "location": location_id, # int
        "tFront": tFront, # double
        "tBack": tBack, # double
    }) + "\n").encode())

    resp = recvall(s)

    return json.loads(resp.decode("utf-8"))

def getSensor(s, sensor_id, tFront, tBack):
    s.send((json.dumps({
        "function": "getSensor",
        "sensor": sensor_id, # int
        "tFront": tFront, # double
        "tBack": tBack, # double
    }) + "\n").encode())

    resp = recvall(s)

    return json.loads(resp.decode("utf-8"))

def updateMongoDB(s, db, winWidth):
    locations = listLocations(s).get("locations", [])

    for partial_location in locations:
        full_location = getLocation(s, partial_location["id"], winWidth, winWidth)
        pprint(full_location)
        if full_location:
            full_location["id"] = partial_location["id"]
            full_location["title"] = partial_location["title"]
            full_location["longitude"] = partial_location["longitude"]
            full_location["latitude"] = partial_location["latitude"]
            db.locations.replace_one({"_id": partial_location["id"]}, full_location, True)

            sensors = listSensors(s, partial_location["id"]).get("sensors", [])

            for partial_sensor in sensors:
                full_sensor = getSensor(s, partial_sensor["id"], winWidth, winWidth)
                pprint(full_sensor)
                if full_sensor:
                    full_sensor["id"] = partial_sensor["id"]
                    full_sensor["title"] = partial_sensor["title"]
                    full_sensor["location"] = partial_sensor["location"]
                    db.sensors.replace_one({"_id": partial_sensor["id"]}, full_sensor, True)

def main():
    
    # pprint(listLocations(s))
    # pprint(listSensors(s, 2))

    try:
        while True:
            s = socket.socket()
            s.connect(("192.168.43.243", 37825))

            updateMongoDB(s, db, 12.0)
            s.close()
            time.sleep(0.9)

    except KeyboardInterrupt:
        print("Cleaning up ...")

    finally:
        client.close()
        s.close()


if __name__ == "__main__":
    main()
