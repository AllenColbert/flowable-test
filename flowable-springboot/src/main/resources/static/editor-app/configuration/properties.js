/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

var FLOWABLE = FLOWABLE || {};
FLOWABLE.PROPERTY_CONFIG =
{
    "string": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/string-property-write-mode-template.html"
    },
    "boolean": {
        "templateUrl": "editor-app/configuration/org.flowable.db.properties/boolean-property-template.html"
    },
    "text" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/text-property-write-template.html"
    },
    "flowable-calledelementtype" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/calledelementtype-property-write-template.html"
    },
    "flowable-multiinstance" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/multiinstance-property-write-template.html"
    },
    "flowable-processhistorylevel" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/process-historylevel-property-write-template.html"
    },
    "flowable-ordering" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/ordering-property-write-template.html"
    },
    "oryx-dataproperties-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/data-properties-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/data-properties-write-template.html"
    },
    "oryx-formproperties-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/form-properties-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/form-properties-write-template.html"
    },
    "oryx-executionlisteners-multiplecomplex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/execution-listeners-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/execution-listeners-write-template.html"
    },
    "oryx-tasklisteners-multiplecomplex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/task-listeners-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/task-listeners-write-template.html"
    },
    "oryx-eventlisteners-multiplecomplex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/event-listeners-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/event-listeners-write-template.html"
    },
    "oryx-usertaskassignment-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/assignment-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/assignment-write-template.html"
    },
    "oryx-servicetaskfields-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/fields-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/fields-write-template.html"
    },
    "oryx-callactivityinparameters-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/in-parameters-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/in-parameters-write-template.html"
    },
    "oryx-callactivityoutparameters-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/out-parameters-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/out-parameters-write-template.html"
    },
    "oryx-subprocessreference-subprocess-link": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/subprocess-reference-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/subprocess-reference-write-template.html"
    },
    "oryx-formreference-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/form-reference-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/form-reference-write-template.html"
    },
    "oryx-sequencefloworder-complex" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/sequenceflow-order-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/sequenceflow-order-write-template.html"
    },
    "oryx-conditionsequenceflow-complex" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/condition-expression-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/condition-expression-write-template.html"
    },
    "oryx-signaldefinitions-multiplecomplex" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/signal-definitions-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/signal-definitions-write-template.html"
    },
    "oryx-signalref-string" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/signal-property-write-template.html"
    },
    "oryx-messagedefinitions-multiplecomplex" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/message-definitions-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/message-definitions-write-template.html"
    },
    "oryx-messageref-string" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/message-property-write-template.html"
    },
    "oryx-duedatedefinition-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/duedate-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/duedate-write-template.html"
    },
    "oryx-decisiontaskdecisiontablereference-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/decisiontable-reference-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/decisiontable-reference-write-template.html"
    },
    "oryx-casetaskcasereference-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/case-reference-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/case-reference-write-template.html"
    },
    "oryx-processtaskprocessreference-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/process-reference-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/process-reference-write-template.html"
    },
    "oryx-processtaskinparameters-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/in-parameters-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/in-parameters-write-template.html"
    },
    "oryx-processtaskoutparameters-complex": {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/out-parameters-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/out-parameters-write-template.html"
    },
    "oryx-planitemlifecyclelisteners-multiplecomplex": {
            "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/plan-item-lifecycle-listeners-display-template.html",
            "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/plan-item-lifecycle-listeners-write-template.html"
     },
    "flowable-transitionevent" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/transition-event-write-template.html"
    },
    "flowable-planitem-dropdown" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/planitem-dropdown-read-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/planitem-dropdown-write-template.html"
    },
    "flowable-http-request-method" : {
        "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/http-request-method-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/http-request-method-property-write-template.html"
    },
    "flowable-triggermode" : {
            "readModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/trigger-mode-read-template.html",
            "writeModeTemplateUrl": "editor-app/configuration/org.flowable.db.properties/trigger-mode-write-template.html"
    },
};
