import groovy.transform.Field

@Field static final asyncLock = new Object[0]
@Field deleteRulePrefix = "deleteRule"

@Field gtcomp = "greater than"
@Field ltcomp = "less than"


definition(
    name: "TagpointAlerts",
    namespace: "waveaccess",
    author: "IgorKostyukov",
    description: "",
    category: "",
    iconUrl: "",
    iconX2Url: "")


preferences {
    page(name: "mainPage", title: "Main Page", install: true, uninstall: true) {
        section() {
            input "authHeader", "text", title: "Auth Header", multiple: false, required: true, submitOnChange: true    
            href name: "myHref", "button", page: "addRulePage", title: "Add new Rule"
        }        
       
        section("Existing Rules") {
            def rulesText
            state.subscriptions.each { subscriptionId, subscription ->
                subscription.each { ruleId, ruleData ->
                    rulesText = ""
                    rulesText += "Rule ID: $ruleId\n"
                    rulesText += "Name: ${ruleData.name}\n"
                    rulesText += "Parameter Type: ${ruleData.parameterType}\n"
                    rulesText += "Device: ${ruleData.device.displayName}\n"
                    rulesText += "Comparison: ${ruleData.comparison}\n"
                    rulesText += "Value: ${ruleData.value}\n"
                    rulesText += "URL: ${ruleData.url}\n"                                
                    paragraph(rulesText)                
                    input "${deleteRulePrefix}_${subscriptionId}_${ruleId}", "button", title: "Delete"
                    paragraph("-------------------\n")                    
                }
            }
        }
    }
    
       
    page(name: "addRulePage", title: "Add new rule", install: false, uninstall: true) {
      dynamicPage(name: "addRulePage") {
          section("") {
              input "name", "text", title: "Name", multiple: false, required: true, submitOnChange: true    
              if (name) {
                  input "parameterType", "enum", title: "Parameter Type", options: ["temperature", "motion", "humidity"], submitOnChange: true   
              }             
              if (parameterType) {
                  input "device", getCapability(parameterType), title: "Device", multiple: false, required: true, submitOnChange: true    
              }                                       
              
              if (device) {
                  input "comparison", "enum", title: "Comparison", options: [gtcomp, ltcomp], submitOnChange: true
              }
              if (comparison) {
                    input "value", "decimal", title: "Value", required: true, submitOnChange: true
              }            
              if (value != null) {
                    input "recheck", "number", title: "Recheck", required: true, submitOnChange: true
              }            
              if (recheck  != null) {
                  input "url", "text", title: "URL", required: true, submitOnChange: true
              }              
              if (url) {
                  input "create", "button", title: "Add"
              }              
          }          
     }
   } 
}

def createSubscriptionKey(device, parameterType) {
    return "${device.id}${parameterType}"
}

def recheckHandler(subscriptionKey, ruleId) {
    def rule = findRule(subscriptionKey, ruleId)
    if (rule) {
        rule.enabled = true
    }
}

def handlePostResponse(resp, payload) {
    log.debug "tagpoint respStatus: ${resp?.getStatus()}"
}

@groovy.transform.Synchronized("asyncLock")
def eventHandler(event) {   
    def subscriptionKey = createSubscriptionKey(event.getDevice(), event.name)
    def subscription = state.subscriptions[subscriptionKey]
    if (subscription) {     
        subscription.each { ruleId, rule ->
            if (rule.enabled) {
                if ((rule.comparison == gtcomp && event.getDoubleValue() > rule.value) || (rule.comparison == ltcomp && event.getDoubleValue() < rule.value)) {
                    log.debug "post"                    
                    Map postParams = [
                        uri: rule.url,
                        requestContentType: 'application/json',
                        contentType: 'application/json',
                        headers: ['Auth': authHeader],
                        body : new groovy.json.JsonOutput().toJson( [ value: event.value ])
                    ]   
                   
                    asynchttpPost(handlePostResponse, postParams)                    
                    
                    if (rule.recheck > 0) {
                        rule.enabled = false
                        runIn(rule.recheck*60, recheckHandler, [overwrite: false, data: [subscriptionKey, ruleId]])
                    }
                }            
            }
            else {
                log.debug "rule is disabled"
            }    
        }
    }
    else {
        log.debug "unsubscribe ${event.device.displayName} ${event.name}"
        unsubscribe(event.device, event.name)        
    }        
}    

def findRule(subscriptionId, ruleId) {
    def s = state.subscriptions[subscriptionId]
    if (s) {
        return s[ruleId]
    }
    return null
}

@groovy.transform.Synchronized("asyncLock")
def createRule() {
        def newRuleData = [
            name: name,
            parameterType: parameterType,
            device: device,
            comparison: comparison,
            value: value,
            recheck: recheck,
            url: url,
            enabled: true
        ]        
       
        def ruleId = new Date().time.toString()
        
        if (!state.subscriptions) {
            state.subscriptions = [:]
        }
        
        def subscriptionKey = createSubscriptionKey(newRuleData.device, newRuleData.parameterType)
                
        if (!state.subscriptions) {
            state.subscriptions = [:]
        }
        
        if (!state.subscriptions[subscriptionKey]) {
            state.subscriptions[subscriptionKey] = [:]            
        }        
        
        if (state.subscriptions[subscriptionKey].isEmpty()) {
            log.debug "subscribe - ${subscriptionKey}"
            subscribe(newRuleData.device, newRuleData.parameterType, eventHandler, ["filterEvents": false])            
        }
        
        state.subscriptions[subscriptionKey][ruleId] = newRuleData        
}

@groovy.transform.Synchronized("asyncLock")
def deleteRule(btnName) {
    def substrings = btnName.tokenize("_");
    def subscriptionKey = substrings[1]
    def ruleId = substrings[2]
    def rule = findRule(subscriptionKey, ruleId)
    if (rule) {            
        def subscription = state.subscriptions[subscriptionKey]
        if (subscription) {
            def sr = subscription[ruleId]
            if (sr) {
                subscription.remove(ruleId)
                if (subscription.size() == 0) {
                    state.subscriptions.remove(subscriptionKey)
                }                    
            }
        }
    }    
}

void appButtonHandler(btn) {
    log.debug "Button pushed - ${btn}"   
    if (btn == "create") { 
        createRule()
    }
    else if (btn.startsWith(deleteRulePrefix)) {
        deleteRule(btn)
    }        
}

def getCapability(parameterType) {
    if (parameterType == "temperature") {
        return "capability.temperatureMeasurement"
    }
    else if (parameterType == "humidity") {
        return "capability.relativeHumidityMeasurement"
    }
    else if (parameterType == "motion") {
        return "capability.motionSensor"
    }
    else {
        return ""   
    }
}    

def installed() {
    log.debug "installed"    
    updated()
}

def updated() {
    log.debug "updated"    
}

def uninstalled() {
    unsubscribe()
    log.debug "uninstalled"
}