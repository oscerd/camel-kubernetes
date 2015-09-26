/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.kubernetes.consumer;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.kubernetes.KubernetesEndpoint;
import org.apache.camel.component.kubernetes.common.PodEvent;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesPodsConsumer extends ScheduledPollConsumer {

	private static final Logger LOG = LoggerFactory
			.getLogger(KubernetesPodsConsumer.class);

	private List<PodEvent> list;

	public KubernetesPodsConsumer(KubernetesEndpoint endpoint,
			Processor processor) {
		super(endpoint, processor);
	}

	@Override
	public KubernetesEndpoint getEndpoint() {
		return (KubernetesEndpoint) super.getEndpoint();
	}

	@Override
	protected void doStart() throws Exception {
		list = new ArrayList<PodEvent>();
		super.doStart();
		getEndpoint().getKubernetesClient().pods().inNamespace("default")
				.watch(new Watcher<Pod>() {

					@Override
					public void eventReceived(
							io.fabric8.kubernetes.client.Watcher.Action action,
							Pod resource) {
						PodEvent pe = new PodEvent(action, resource);
						list.add(pe);
					}

					@Override
					public void onClose(KubernetesClientException cause) {
						// TODO Auto-generated method stub

					}
				});
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		list.clear();
	}

	@Override
	protected int poll() throws Exception {
		Iterator<PodEvent> it = list.iterator();
		while (it.hasNext()) {
			PodEvent podEvent = (PodEvent) it.next();
			Exchange e = getEndpoint().createExchange();
			e.getIn().setBody(podEvent.getPod());
			e.getIn().setHeader("action", podEvent.getAction());
			getProcessor().process(e);
		}
		return list.size();
	}

}
