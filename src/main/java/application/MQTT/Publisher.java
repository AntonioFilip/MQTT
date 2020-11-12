package application.MQTT;

import java.lang.Thread;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/*import org.apache.logging.log4j.Marker;
import java.util.Objects;*/
import org.eclipse.paho.client.mqttv3.*;

public class Publisher{

	private static final Logger logger = LogManager.getLogger(Publisher.class.getName());
	
	public static void main(String [] args) throws MqttPersistenceException, MqttException {
	InputStream resources = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
	Properties properties = new Properties();
	try {
		properties.load(resources);
	}catch(IOException e){
		logger.error("Could not load connection resources", e);
	}
	
	String clientId = UUID.randomUUID().toString();
	MqttClient publisher = null;
	try {
		String mqttBrokerUrl = properties.getProperty("mqttbroker_url");
		publisher = new MqttClient(mqttBrokerUrl, clientId);
		logger.info("Publisher created",mqttBrokerUrl);
	}catch(MqttException e) {
		logger.error("Publisher creation failed"+e.getMessage()+"Reason:"+e.getReasonCode());
	}
	
	
    MqttConnectOptions options = new MqttConnectOptions();
	options.setCleanSession(Boolean.TRUE);
	options.setConnectionTimeout(8);
	options.setAutomaticReconnect(Boolean.TRUE);

    try {
        publisher.connect(options);
    } catch (MqttException e) {
        logger.error("Connection to broker failed {} / {}", e.getMessage(), e.getReasonCode());
    }
    //publisher.publish(properties.getProperty("topic"), new byte[0],0,true);
    
	EngineSensor VolvoEngine = new EngineSensor(publisher,clientId);
	ScheduledExecutorService scheduledEvent = Executors.newScheduledThreadPool(1);
	scheduledEvent.scheduleAtFixedRate(VolvoEngine, 3, 3, TimeUnit.SECONDS);
	
	
	}
}
