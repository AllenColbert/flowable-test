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
package com.shareniu.shareniu_flowable_study.cmmn.task.serviceTask;

import java.io.Serializable;

import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;

/**
 */
public class TestBean  implements Serializable{
	private static final long serialVersionUID = -3306315053247369126L;

	public String invoke(DelegatePlanItemInstance planItemInstance) {
        return "hello "  + planItemInstance.getVariable("test");
    }
}