package playground.andreas.intersection.sim;

import org.matsim.mobsim.QueueLink;
import org.matsim.mobsim.QueueNetworkFactory;
import org.matsim.mobsim.QueueNetworkLayer;
import org.matsim.mobsim.QueueNode;
import org.matsim.network.Link;
import org.matsim.network.Node;

public final class TrafficLightQueueNetworkFactory implements QueueNetworkFactory<QueueNode, QueueLink> {

	/**
	 * @see org.matsim.mobsim.QueueNetworkFactory#newQueueLink(org.matsim.network.Link, org.matsim.mobsim.QueueNetworkLayer)
	 */
	public QueueLink newQueueLink(Link link, QueueNetworkLayer queueNetwork, QueueNode toQueueNode) {
		return new QLink(link, queueNetwork, toQueueNode);
	}

	/**
	 * @see org.matsim.mobsim.QueueNetworkFactory#newQueueNode(org.matsim.network.Node, org.matsim.mobsim.QueueNetworkLayer)
	 */
	public QueueNode newQueueNode(Node node, QueueNetworkLayer queueNetwork) {
		return new QNode(node, queueNetwork);
	}

}
