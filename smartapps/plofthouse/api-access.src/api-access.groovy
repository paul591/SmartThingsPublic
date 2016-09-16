/**
 *  API Access
 *
 *  Copyright 2016 Paul Lofthouse
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "API Access",
    namespace: "plofthouse",
    author: "Paul Lofthouse",
    description: "API Access",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true)


preferences {
  section("Allow External Service to Control These Things...") {
    input "switches", "capability.switch", title: "Which Switches?", multiple: true, required: false
    input "motions", "capability.motionSensor", title: "Which Motion Sensors?", multiple: true, required: false
    input "locks", "capability.lock", title: "Which Locks?", multiple: true, required: false
    input "thermostat", "capability.thermostat", title:"Which Thermostat", multiple:false, required: false
    input "humidity", "capability.relativeHumidityMeasurement", title:"Which Humidty Source", multiple: true, required: false
    input "contacts", "capability.contactSensor", title:"Which Door Sensors", multiple: true, required: false
  }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}

mappings {
  path("/switches") {
    action: [
      GET: "listSwitches",
      PUT: "updateSwitches"
    ]
  }
  path("/temperature") {
    action: [
      GET: "getTemperature",
    ]
  }
  path("/doors") {
    action: [
      GET: "getDoorStatus",
    ]
  }
  
  path("/humidity") {
    action: [
      GET: "getHumidity",
    ]
  }
  path("/switches/:id") {
    action: [
      GET: "showSwitch",
      PUT: "updateSwitch"
    ]
  }
  
  path("/status")
  {
  action: [
  	GET: "getStatus",
    ]
  }
  
  path("/thermostatMode")
  {
  action: [
  	GET: "getThermostatMode",
    PUT: "setThermostatMode"
    ]
  }
  
  path("/mode")
  {
  action:[
  	GET: "getModes",
    PUT: "setMode"
   ]
  }
}

void setThermostatMode()
{
log.debug("In setThermostatMode")
	def command = request.JSON?.mode
    log.debug(command);
    if(command)
    {
    	thermostat.each{
    		it.setThermostatMode(command)
        }
    }
}
void updateSwitch() {
    def command = request.JSON?.command
    if (command) {
      def mySwitch = switches.find { it.id == params.id }
      if (!mySwitch) {
        httpError(404, "Switch not found")
      } else {
        mySwitch."$command"()
      }
    }
}

// returns a list like
// [[name: "kitchen lamp", value: "off"], [name: "bathroom", value: "on"]]
def listSwitches() {
	log.debug "In Here"
    def resp = []
    switches.each {
      resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    return resp
}

def getTemperature() {
	log.debug("In getTemperature")
    def resp = []
    thermostat.each {
    	resp << [name: "Temperature", value: it.currentValue("temperature")]
        //resp << [name: "Humidity", value: it.currentValue("humidity")]
    }
    return resp
}

def getHumidity() {
	log.debug("In getHumidity")
    def resp = []
    thermostat.each {
    	resp << [name: "humidity", value: it.currentValue("humidity")]
    }
    return resp
}

def getStatus() {
	log.debug("In getStatus")
    def resp = []
    thermostat.each {
    	resp << [name: "Temperature", value: it.currentValue("temperature")]
        //resp << [name: "Humidity", value: it.currentValue("humidity")]
    }
    thermostat.each {
    	resp << [name: "humidity", value: it.currentValue("humidity")]
        resp << [name: "thermostatMode", value: it.currentValue("thermostatMode")]
        resp << [name: "coolingSetpoint", value: it.currentValue("coolingSetpoint")]
    }
    switches.each {
      resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    contacts.each {
      resp << [name: it.displayName, value: it.currentValue("contact")]
    }
    def returnString = "status: " << resp
    return resp
}

def getDoorStatus() {
	log.debug("In getDoorStatus")
    def resp = []
   
    contacts.each {
      resp << [name: it.displayName, value: it.currentValue("contact")]
    }
    def returnString = "status: " << resp
    return resp
}

def getThermostatMode(){
	log.debug("In getHeatingMode");
    def resp = []
    thermostat.each{
    	resp << [name: "thermostatMode", value: it.currentValue("thermostatMode")]
    }
    return resp
}
// TODO: implement event handlersd