import groovy.transform.Field

@Field String VERSION = "0.1.0"

@Field List<String> LOG_LEVELS = ["error", "warn", "info", "debug", "trace"]
@Field String DEFAULT_LOG_LEVEL = LOG_LEVELS[1]

metadata {
    definition (name: "MCO Home Multi Sensor Driver", namespace: "tagpoint", author: "Peter Liapin") {
        capability "Actuator"
        capability "Sensor"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Illuminance Measurement"
        capability "Carbon Dioxide Measurement"
        capability "Motion Sensor"
        capability "Sound Pressure Level"
        capability "Smoke Detector"
        capability "Refresh"
        // capability "Configuration"
        capability "Initialize"
        
        attribute "voc", "number"
        attribute "pm25", "number"
        
        command "clearState"
    }

    // preferences {
    //     input "param1", "number", title: "PM2.5 Delta Level", range: "0..127", defaultValue: 0, description: "0 to turn off report. 1-127 to report current PM2.5 value when change >n * 1ug/m3"
    //     input "param2", "number", title: "CO2 Delta Level", range: "0..127", defaultValue: 0, description: "0 to turn off report. 1-127 to report current CO2 value when change > n * 5ppm"
    //     input "param3", "number", title: "Temperature Delta Level", range: "0..127", defaultValue: 0, description: "0 to turn off report. 1-127 to report current temperature value when change > n * 0.5°C"
    //     input "param4", "number", title: "Humidity Delta Level", range: "0..127", defaultValue: 0, description: "0 to turn off report. 1-127 to report current humidity value when change >n%"
    //     input "param5", "number", title: "VOC Delta Level", range: "0..127", defaultValue: 0, description: "0 to turn off report. 1-127 to report current VOC value when change >n * 5ppb"
    //     input "param6", "number", title: "Lux Delta Level", range: "0..32767", defaultValue: 0, description: "0 to turn off report. 1-32767 to report current Illumination value when change >n * 1 Lux"
    //     input "param7", "number", title: "dB Delta Level", range: "0..127", defaultValue: 0, description: "0 to turn off report. 1-127 to report current Noise value when change >n * 1 dB"
    //     input "param8", "number", title: "PIR Delta Level", options: [0: "Turn off report", 1: "Report change"], defaultValue: 0
    //     input "param9", "number", title: "SMOKE Delta Level", options: [0: "Turn off report", 1: "Report change"], defaultValue: 1
    // }    
}

// Parse incoming device messages to generate events
def parse(String description) {
    def cmd = zwave.parse(description)
    if (cmd) {
        return zwaveEvent(cmd)
    }
}

def zwaveEvent(hubitat.zwave.commands.sensormultilevelv10.SensorMultilevelReport cmd) {
    def map = [:]
    switch (cmd.sensorType) {
        case 1:  // Temperature
            map.name = "temperature"
            map.value = cmd.scaledSensorValue
            map.unit = cmd.scale == 1 ? "°F" : "°C"
            break
        case 2:  // PIR Motion
            map.name = "motion"
            map.value = cmd.scaledSensorValue == 0 ? "inactive" : "active"
            break
        case 3:  // Illuminance
            map.name = "illuminance"
            map.value = cmd.scaledSensorValue
            map.unit = "lux"
            break
        case 5:  // Humidity
            map.name = "humidity"
            map.value = cmd.scaledSensorValue
            map.unit = "%"
            break
        case 17: // CO2
            map.name = "carbonDioxide"
            map.value = cmd.scaledSensorValue
            map.unit = "ppm"
            break
        case 30: // SoundPressureLevel 
            map.name = "soundPressureLevel"
            map.value = cmd.scaledSensorValue
            map.unit = "dB"
            break
        case 35: // PM2.5
            map.name = "pm25"
            map.value = cmd.scaledSensorValue
            map.unit = "ug/m3"
            break
        case 39: // VOC
            map.name = "voc"
            map.value = cmd.scaledSensorValue
            map.unit = "ppb"
            break
        case 55: // Smoke detection
            map.name = "smoke"
            map.value = cmd.scaledSensorValue == 0 ? "clear" : "detected"
            break
        default:
            log.debug "Sensor type ${cmd.sensorType} not handled"
            return null
    }
    createEvent(map)
}

// Handle other Z-Wave commands
def zwaveEvent(hubitat.zwave.Command cmd) {
    log.warn "Unhandled command: ${cmd}"
    null
}

// Initialization logic
def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

// def configure() {
//   def cmds = []
//   cmds = cmds + cmdSequence([
//     zwave.associationV2.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId),
//     zwave.configurationV1.configurationSet(parameterNumber: 1, size: 1, scaledConfigurationValue: param1.toInteger()),
//     zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: param2.toInteger()),
//     zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: param3.toInteger()),
//     zwave.configurationV1.configurationSet(parameterNumber: 4, size: 1, scaledConfigurationValue: param4.toInteger()),
//     zwave.configurationV1.configurationSet(parameterNumber: 5, size: 1, scaledConfigurationValue: param5.toInteger()),
//     zwave.configurationV1.configurationSet(parameterNumber: 6, size: 1, scaledConfigurationValue: param6.toInteger()),
//     zwave.configurationV1.configurationSet(parameterNumber: 7, size: 1, scaledConfigurationValue: param7.toInteger()),
//     zwave.configurationV1.configurationSet(parameterNumber: 8, size: 1, scaledConfigurationValue: param8.toInteger()),
//     zwave.configurationV1.configurationSet(parameterNumber: 9, size: 1, scaledConfigurationValue: param9.toInteger())
//   ], 300)
// }

def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
}

def initialize() {
  log.debug "Initialize"
  sendEvent(name: "temperature", value: "0", displayed: true)
  sendEvent(name: "motion", value: "inactive", displayed: true)
  sendEvent(name: "illuminance", value: "0lux", displayed: true)
  sendEvent(name: "humidity", value: "0%", displayed: true)
  sendEvent(name: "carbonDioxide", value: "0ppm", displayed: true)
  sendEvent(name: "soundPressureLevel", value: "0dB", displayed: true)
  sendEvent(name: "pm25", value: "0ug/m3", displayed: true)  
  sendEvent(name: "voc", value: "0", displayed: true)  
  sendEvent(name: "smoke", value: "clear", displayed: true)
}

def refresh() {
  log.debug "refresh() - state: ${state.inspect()}"
  def cmds = []
  cmds = cmds + cmdSequence([
    zwave.sensorMultilevelV11.sensorMultilevelGet(sensorType: 1, scale: (location.temperatureScale=="F"?1:0)), // Temperature
    zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 2),
    zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 5),
    zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 17),
    zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 30),
    zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 35),
    zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 39),
    zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 55)
  ], 100)
}

private cmdSequence(Collection commands, Integer delayBetweenArgs=250) {
  delayBetween(commands.collect{ cmd(it) }, delayBetweenArgs)
}

private cmd(hubitat.zwave.Command cmd) {
  if (getDataValue("zwaveSecurePairingComplete") == "true" && getDataValue("S2") == null) {
    zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
  } else if (getDataValue("zwaveSecurePairingComplete") == "true") {
    zwaveSecureEncap(cmd)
  } else {
    cmd.format()
  }
}

def clearState() {
    log.debug "ClearStates() - Clearing device states"
    state.clear()

    installed()
}